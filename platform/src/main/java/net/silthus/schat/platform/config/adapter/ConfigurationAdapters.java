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
package net.silthus.schat.platform.config.adapter;

import java.nio.file.Path;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class ConfigurationAdapters {

    public static final ConfigurationAdapter.Factory YAML = file -> new YamlConfigurateAdapter(file.toPath());

    private ConfigurationAdapters() {
    }

    private static final class YamlConfigurateAdapter extends ConfigurateAdapter<YamlConfigurationLoader.Builder, YamlConfigurationLoader> {
        private YamlConfigurateAdapter(Path path) {
            super(path);
        }

        @Override
        protected AbstractConfigurationLoader.Builder<YamlConfigurationLoader.Builder, YamlConfigurationLoader> createLoader(Path path) {
            return YamlConfigurationLoader.builder()
                .path(path)
                .indent(4)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESERVE);
        }
    }
}
