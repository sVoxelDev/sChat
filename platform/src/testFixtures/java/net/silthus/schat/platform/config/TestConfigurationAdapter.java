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
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import lombok.SneakyThrows;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapter;
import net.silthus.schat.platform.config.adapter.ConfigurationAdapters;

public final class TestConfigurationAdapter {

    public static final String TEST_CONFIG_NAME = "test-config.yml";

    @SneakyThrows
    public static ConfigurationAdapter testConfigAdapter() {
        return testConfigAdapter(File.createTempFile("schat-config", ".yml"));
    }

    @SneakyThrows
    public static ConfigurationAdapter testConfigAdapter(File target) {
        return testConfigAdapter(getTestConfigAsStream(), target);
    }

    @SneakyThrows
    public static ConfigurationAdapter testConfigAdapter(InputStream source, File target) {
        copyConfig(source, target);
        return ConfigurationAdapters.YAML.create(target);
    }

    private static InputStream getTestConfigAsStream() {
        return Objects.requireNonNull(TestConfigurationAdapter.class.getClassLoader().getResourceAsStream(TEST_CONFIG_NAME));
    }

    private static void copyConfig(InputStream source, File target) throws IOException {
        Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private TestConfigurationAdapter() {
    }
}
