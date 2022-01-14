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

package net.silthus.schat.platform.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.function.Function;
import lombok.SneakyThrows;
import net.silthus.schat.platform.config.adapter.ConfigurateAdapter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class TestConfigurationAdapter extends ConfigurateAdapter {

    public static final String TEST_CONFIG_NAME = "test-config.yml";

    @SneakyThrows
    public TestConfigurationAdapter(InputStream source, File target) {
        super(target.toPath());
        copyConfig(source, target);
    }

    @SneakyThrows
    public TestConfigurationAdapter(File target) {
        super(target.toPath());
        copyConfig(getTestConfigAsStream(), target);
    }

    @Override
    protected ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path, Function<ConfigurationOptions, ConfigurationOptions> defaultOptions) {
        return YamlConfigurationLoader.builder()
            .path(path)
            .defaultOptions(defaultOptions::apply)
            .build();
    }

    private InputStream getTestConfigAsStream() {
        return Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(TEST_CONFIG_NAME));
    }

    private void copyConfig(InputStream source, File target) throws IOException {
        Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
