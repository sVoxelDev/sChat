/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.silthus.chat.config.FormatConfig;
import net.silthus.chat.formats.MiniMessageFormat;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.silthus.chat.Constants.Formatting.*;
import static net.silthus.chat.utils.AnnotationUtils.name;
import static net.silthus.chat.utils.ReflectionUtil.getDefaultSupplier;

@Log(topic = Constants.PLUGIN_NAME)
public final class Formats {

    public static final Map<String, FormatConfig> DEFAULT_FORMATS = Map.of(
            DEFAULT, FormatConfig.miniMessage(DEFAULT_FORMAT),
            CHANNEL, FormatConfig.miniMessage(CHANNEL_FORMAT),
            NO_FORMAT, FormatConfig.miniMessage(NO_FORMAT_FORMAT),
            SENDER, FormatConfig.miniMessage(SENDER_FORMAT),
            SENDER_HOVER, FormatConfig.miniMessage(SENDER_HOVER_FORMAT),
            CHANNEL_FORMATTED, FormatConfig.miniMessage(CHANNEL_FORMATTED_FORMAT),
            PRIVATE_MESSAGE, FormatConfig.miniMessage(PRIVATE_MESSAGE_FORMAT),
            SENDER_NO_VAULT, FormatConfig.miniMessage(SENDER_NO_VAULT_FORMAT),
            BROADCAST, FormatConfig.miniMessage(BROADCAST_FORMAT)
    );

    private final static Map<String, RegisteredFormat<?>> formats = new HashMap<>();
    private final static Map<String, FormatTemplate<?>> templates = new HashMap<>();

    static {
        registerFormat(MiniMessageFormat.class, () -> new MiniMessageFormat(SChat.instance().getPlaceholders()));
    }

    public static Format defaultFormat() {
        return formatFromTemplate(DEFAULT).orElseGet(() -> miniMessage(DEFAULT_FORMAT));
    }

    public static Format channelFormat() {
        return defaultFormat(CHANNEL);
    }

    public static Format noFormat() {
        return defaultFormat(NO_FORMAT);
    }

    public static Format defaultFormat(String key) {
        return formatFromTemplate(key).orElseGet(() -> DEFAULT_FORMATS.get(key).toFormat());
    }

    public static Format miniMessage(String format) {
        return miniMessage().format(format);
    }

    public static MiniMessageFormat miniMessage() {
        return format(MiniMessageFormat.class);
    }

    public static Optional<Format> format(String name) {
        return format(name, new MemoryConfiguration());
    }

    public static Optional<Format> format(ConfigurationSection config) {
        if (config == null) return Optional.empty();
        return format(config.getString("type", name(MiniMessageFormat.class)), config);
    }

    public static Optional<Format> format(String name, ConfigurationSection config) {
        return Optional.ofNullable(formats.get(name))
                .map(format -> format.supplier().get())
                .map(format -> BukkitConfigMap.of(format).with(config).apply());
    }

    public static <TFormat extends Format> TFormat format(Class<TFormat> formatClass) {
        return format(formatClass, new MemoryConfiguration());
    }

    public static <TFormat extends Format> TFormat format(Class<TFormat> formatClass, ConfigurationSection config) {
        return formatClass.cast(format(name(formatClass), config)
                .orElseGet(() -> BukkitConfigMap.of(registerFormat(formatClass)).with(config).apply()));
    }

    public static <TFormat extends Format> TFormat registerFormat(Class<TFormat> format) {
        return registerFormat(format, getDefaultSupplier(format));
    }

    public static <TFormat extends Format> TFormat registerFormat(Class<TFormat> format, Supplier<TFormat> supplier) {
        return registerFormat(name(format), format, supplier).supplier().get();
    }

    public static <TFormat extends Format> void registerFormatTemplate(String templateName, Class<TFormat> formatClass, ConfigurationSection config) {
        registerFormatTemplate(templateName, formatClass, configure -> BukkitConfigMap.of(configure).with(config).apply());
    }

    public static <TFormat extends Format> void registerFormatTemplate(String templateName, Class<TFormat> formatClass, Function<TFormat, TFormat> config) {
        final FormatTemplate<?> oldTemplate = templates.put(templateName, new FormatTemplate<>(templateName, formatClass, config));
        if (oldTemplate != null)
            log.warning("Existing format template '" + oldTemplate.name + "' was replaced.");
        log.info("Registered format template: " + templateName);
    }

    @SneakyThrows
    public static void registerFormatTemplate(String templateName, String type, ConfigurationSection config) {
        final RegisteredFormat<?> registeredFormat = formats.get(type);
        if (registeredFormat == null) {
            log.warning("The template '" + templateName + "' tried to use an unknown format type: '" + type + "'");
            return;
        }
        registerFormatTemplate(templateName, registeredFormat.formatClass(), config);
    }

    public static Optional<Format> formatFromTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName))
                .map(formatTemplate -> formatTemplate.config().apply(format(formatTemplate.format())));
    }

    public static <TFormat extends Format> Optional<TFormat> formatFromTemplate(String templateName, Class<TFormat> formatClass) {
        return formatFromTemplate(templateName).map(formatClass::cast);
    }

    public static boolean containsTemplate(String template) {
        return templates.containsKey(template);
    }

    @NotNull
    private static <TFormat extends Format> RegisteredFormat<TFormat> registerFormat(String name, Class<TFormat> format, Supplier<TFormat> supplier) {
        final RegisteredFormat<TFormat> registeredFormat = new RegisteredFormat<>(name, format, supplier);
        final RegisteredFormat<?> oldFormat = formats.put(name, registeredFormat);
        if (oldFormat != null)
            log.warning("Existing format " + oldFormat.formatClass().getCanonicalName() + " with key '"
                    + name + "' was replaced by " + format.getCanonicalName());
        log.info("Registered format type: " + name);
        return registeredFormat;
    }

    private static record RegisteredFormat<TFormat extends Format>(
            String name,
            Class<TFormat> formatClass,
            Supplier<TFormat> supplier
    ) {

    }

    private static record FormatTemplate<TFormat extends Format>(
            String name,
            Class<TFormat> format,
            Function<TFormat, TFormat> config
    ) {

    }

    private Formats() {
    }
}
