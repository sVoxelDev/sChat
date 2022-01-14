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

import java.nio.file.Path;
import java.util.function.Function;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class ConfigurationAdapters {

    public static final ConfigurationAdapter.Factory YAML = file -> new YamlConfigurateAdapter(file.toPath());

    private ConfigurationAdapters() {
    }

    private static final class YamlConfigurateAdapter extends ConfigurateAdapter {

        private YamlConfigurateAdapter(Path path) {
            super(path);
        }

        @Override
        protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path, Function<ConfigurationOptions, ConfigurationOptions> defaultOptions) {
            return YamlConfigurationLoader.builder()
                .path(path)
                .indent(4)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESERVE)
                .defaultOptions(defaultOptions::apply)
                .build();
        }
    }
}
