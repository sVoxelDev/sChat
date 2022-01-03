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

package net.silthus.schat.platform.config.adapter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.usecases.ChannelConfig;
import net.silthus.schat.platform.config.serializers.ChannelConfigSerializer;
import net.silthus.schat.platform.config.serializers.MiniMessageComponentSerializer;
import net.silthus.schat.platform.config.serializers.SettingsSerializer;
import net.silthus.schat.settings.Settings;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public abstract class ConfigurateAdapter extends ConfigurateConfigSection implements ConfigurationAdapter {

    private static final TypeSerializerCollection SERIALIZERS = TypeSerializerCollection.builder()
        .register(Component.class, new MiniMessageComponentSerializer())
        .register(Settings.class, new SettingsSerializer())
        .register(ChannelConfig.class, new ChannelConfigSerializer())
        .build();

    private static final Function<ConfigurationOptions, ConfigurationOptions> DEFAULT_OPTIONS = options ->
        options.serializers(serializers -> serializers.registerAll(SERIALIZERS));

    private final ConfigurationLoader<? extends ConfigurationNode> loader;

    protected ConfigurateAdapter(Path path) {
        this.loader = createLoader(path, DEFAULT_OPTIONS);
    }

    protected abstract ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path, Function<ConfigurationOptions, ConfigurationOptions> defaultOptions);

    @Override
    public void save() {
        try {
            loader.save(getRoot());
        } catch (ConfigurateException e) {
            throw new SaveFailed(e);
        }
    }

    @Override
    public void load() {
        try {
            setRoot(loader.load());
        } catch (IOException e) {
            throw new LoadFailed(e);
        }
    }
}
