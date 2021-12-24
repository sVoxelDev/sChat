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

package net.silthus.schat.settings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsTest {

    static final Setting<String> DYNAMIC_TEST = Setting.setting(String.class, "dynamic", "foobar");
    static final Setting<String> DEFAULT_VAL_TEST = Setting.setting(String.class, "default", "test");

    @Test
    void create_isEmpty() {
        assertThat(Settings.createSettings().settings()).isEmpty();
    }

    @Test
    void populateWithInitialValues() {
        final Settings settings = Settings.builder()
            .withStatic(DEFAULT_VAL_TEST, "static")
            .withDynamic(DYNAMIC_TEST, () -> "dynamic")
            .build();
        assertThat(settings.get(DEFAULT_VAL_TEST)).isEqualTo("static");
        assertThat(settings.get(DYNAMIC_TEST)).isEqualTo("dynamic");
    }

    @Test
    void returnsDefaultValueIfNotSet() {
        final Settings settings = Settings.createSettings();
        assertThat(settings.get(DEFAULT_VAL_TEST)).isEqualTo("test");
    }

    @Test
    void givenNotSet_returnsProvidedDefault() {
        final Settings settings = Settings.createSettings();
        assertThat(settings.getOrDefault(DEFAULT_VAL_TEST, "foobar")).isEqualTo("foobar");
    }
}
