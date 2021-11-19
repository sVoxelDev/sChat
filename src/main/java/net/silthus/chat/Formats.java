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

import lombok.extern.java.Log;
import net.silthus.chat.formats.MiniMessageFormat;
import net.silthus.configmapper.bukkit.BukkitConfigMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static net.silthus.chat.utils.AnnotationUtils.name;
import static net.silthus.chat.utils.ReflectionUtil.getDefaultSupplier;

@Log(topic = Constants.PLUGIN_NAME)
public final class Formats {

    private final static Map<String, RegisteredFormat<?>> formats = new HashMap<>();
    private final static Map<String, FormatTemplate<?>> templates = new HashMap<>();

    static {
        registerFormat(MiniMessageFormat.class, () -> new MiniMessageFormat(SChat.instance().getPlaceholders()));
    }

    public static Format defaultFormat() {
        return miniMessage(Constants.Formatting.DEFAULT_FORMAT);
    }

    public static Format channelFormat() {
        return miniMessage(Constants.Formatting.DEFAULT_CHANNEL_FORMAT);
    }

    public static Format noFormat() {
        return miniMessage(Constants.Formatting.NO_FORMAT);
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
        templates.put(templateName, new FormatTemplate<>(templateName, formatClass, config));
    }

    public static Optional<Format> formatFromTemplate(String templateName) {
        return Optional.ofNullable(templates.get(templateName))
                .map(formatTemplate -> format(formatTemplate.format(), formatTemplate.config()));
    }

    @NotNull
    private static <TFormat extends Format> RegisteredFormat<TFormat> registerFormat(String name, Class<TFormat> format, Supplier<TFormat> supplier) {
        final RegisteredFormat<TFormat> registeredFormat = new RegisteredFormat<>(name, format, supplier);
        final RegisteredFormat<?> oldFormat = formats.put(name, registeredFormat);
        if (oldFormat != null)
            log.warning("Existing format " + oldFormat.formatClass().getCanonicalName() + " with key '"
                    + name + "' was replaced by " + format.getCanonicalName());
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
            ConfigurationSection config
    ) {

    }

    private Formats() {
    }
}
