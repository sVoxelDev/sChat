/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.silthus.schat.platform.locale;

import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.jetbrains.annotations.Nullable;

@Log(topic = "sChat")
public final class TranslationManager {
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private final Set<Locale> installed = ConcurrentHashMap.newKeySet();
    private final Path translationsDirectory;
    private final Path repositoryTranslationsDirectory;
    private final Path customTranslationsDirectory;
    private TranslationRegistry registry;

    public TranslationManager(Path configDirectory) {
        this.translationsDirectory = configDirectory.resolve("translations");
        this.repositoryTranslationsDirectory = this.translationsDirectory.resolve("repository");
        this.customTranslationsDirectory = this.translationsDirectory.resolve("custom");

        try {
            Files.createDirectories(repositoryTranslationsDirectory);
            Files.createDirectories(customTranslationsDirectory);
        } catch (IOException e) {
            // ignore
        }
    }

    public static boolean isTranslationFile(Path path) {
        return path.getFileName().toString().endsWith(".properties");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isAdventureDuplicatesException(Exception e) {
        return e instanceof IllegalArgumentException && (e.getMessage().startsWith("Invalid key") || e.getMessage().startsWith("Translation already exists"));
    }

    public static Component render(Component component) {
        return render(component, Locale.getDefault());
    }

    public static Component render(Component component, @Nullable Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
            if (locale == null) {
                locale = DEFAULT_LOCALE;
            }
        }
        return GlobalTranslator.render(component, locale);
    }

    public static Component render(Component component, @Nullable String locale) {
        return render(component, parseLocale(locale));
    }

    public static @Nullable Locale parseLocale(@Nullable String locale) {
        return locale == null ? null : Translator.parseLocale(locale);
    }

    public static String localeDisplayName(Locale locale) {
        return locale.getDisplayLanguage(locale);
    }

    public Path getTranslationsDirectory() {
        return this.translationsDirectory;
    }

    public Path getRepositoryTranslationsDirectory() {
        return this.repositoryTranslationsDirectory;
    }

    public Path getRepositoryStatusFile() {
        return this.repositoryTranslationsDirectory.resolve("status.json");
    }

    public Set<Locale> getInstalledLocales() {
        return Collections.unmodifiableSet(this.installed);
    }

    public void reload() {
        clearPreviousRegistry();
        createTranslationRegistry();
        loadTranslations();
        registerRegistryAsGlobalSource();
    }

    private void registerRegistryAsGlobalSource() {
        GlobalTranslator.get().addSource(this.registry);
    }

    private void loadTranslations() {
        loadCustomTranslations();
        loadDefaultTranslations();
        loadFromResourceBundle();
    }

    private void loadDefaultTranslations() {
        loadFromFileSystem(this.repositoryTranslationsDirectory, true);
    }

    private void loadCustomTranslations() {
        loadFromFileSystem(this.customTranslationsDirectory, false);
    }

    private void createTranslationRegistry() {
        this.registry = TranslationRegistry.create(Key.key("schat", "main"));
        this.registry.defaultLocale(DEFAULT_LOCALE);
    }

    private void clearPreviousRegistry() {
        if (this.registry != null) {
            GlobalTranslator.get().removeSource(this.registry);
            this.installed.clear();
        }
    }

    /**
     * Loads the base (English) translations from the jar file.
     */
    private void loadFromResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle("schat", DEFAULT_LOCALE, UTF8ResourceBundleControl.get());
        try {
            this.registry.registerAll(DEFAULT_LOCALE, bundle, false);
        } catch (IllegalArgumentException e) {
            if (!isAdventureDuplicatesException(e)) {
                log.log(Level.WARNING, "Error loading default locale file", e);
            }
        }
    }

    /**
     * Loads custom translations (in any language) from the plugin configuration folder.
     */
    public void loadFromFileSystem(Path directory, boolean suppressDuplicatesError) {
        List<Path> translationFiles;
        try (Stream<Path> stream = Files.list(directory)) {
            translationFiles = stream.filter(TranslationManager::isTranslationFile).collect(Collectors.toList());
        } catch (IOException e) {
            translationFiles = Collections.emptyList();
        }

        if (translationFiles.isEmpty()) {
            return;
        }

        Map<Locale, ResourceBundle> loaded = new HashMap<>();
        for (Path translationFile : translationFiles) {
            try {
                Map.Entry<Locale, ResourceBundle> result = loadTranslationFile(translationFile);
                loaded.put(result.getKey(), result.getValue());
            } catch (Exception e) {
                if (!suppressDuplicatesError || !isAdventureDuplicatesException(e)) {
                    log.log(Level.WARNING, "Error loading locale file: " + translationFile.getFileName(), e);
                }
            }
        }

        // try registering the locale without a country code - if we don't already have a registration for that
        loaded.forEach((locale, bundle) -> {
            Locale localeWithoutCountry = new Locale(locale.getLanguage());
            if (!locale.equals(localeWithoutCountry) && !localeWithoutCountry.equals(DEFAULT_LOCALE) && this.installed.add(localeWithoutCountry)) {
                try {
                    this.registry.registerAll(localeWithoutCountry, bundle, false);
                } catch (IllegalArgumentException e) {
                    // ignore
                }
            }
        });
    }

    private Map.Entry<Locale, ResourceBundle> loadTranslationFile(Path translationFile) throws IOException {
        String fileName = translationFile.getFileName().toString();
        String localeString = fileName.substring(0, fileName.length() - ".properties".length());
        Locale locale = parseLocale(localeString);

        if (locale == null) {
            throw new IllegalStateException("Unknown locale '" + localeString + "' - unable to register.");
        }

        PropertyResourceBundle bundle;
        try (BufferedReader reader = Files.newBufferedReader(translationFile, StandardCharsets.UTF_8)) {
            bundle = new PropertyResourceBundle(reader);
        }

        this.registry.registerAll(locale, bundle, false);
        this.installed.add(locale);
        return Maps.immutableEntry(locale, bundle);
    }

}
