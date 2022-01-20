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

package net.silthus.schat.pointer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsTest {

    static final Setting<String> DYNAMIC_TEST = Setting.setting(String.class, "dynamic", "foobar");
    static final Setting<String> DEFAULT_VAL_TEST = Setting.setting(String.class, "default", "test");
    static final Pointer<String> TEST_POINTER = Pointer.pointer(String.class, "test");

    @Test
    void create_isEmpty() {
        assertThat(Settings.createSettings().getSettings()).isEmpty();
    }

    @Test
    void populateWithInitialValues() {
        final Settings settings = Settings.settings()
            .withStatic(DEFAULT_VAL_TEST, "static")
            .withDynamic(DYNAMIC_TEST, () -> "dynamic")
            .create();
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

    @Test
    void getOrDefault_valueIsSet_returnsValue() {
        final Settings settings = Settings.settings().withStatic(DEFAULT_VAL_TEST, "bob").create();
        assertThat(settings.getOrDefaultFrom(DEFAULT_VAL_TEST, () -> "bobby")).isEqualTo("bob");
    }

    @Test
    void set_updatesValue() {
        final Settings settings = Settings.settings().withStatic(DEFAULT_VAL_TEST, "bob").create();
        settings.set(DEFAULT_VAL_TEST, "bobby");
        assertThat(settings.get(DEFAULT_VAL_TEST)).isEqualTo("bobby");
    }

    @Test
    void copy_copiesAllSettings() {
        final Settings original = Settings.settings()
            .withStatic(DEFAULT_VAL_TEST, "static")
            .withDynamic(DYNAMIC_TEST, () -> "dynamic")
            .create();
        final Settings settings = original.copy().withStatic(DEFAULT_VAL_TEST, "foobar").create();

        assertThat(original.get(DEFAULT_VAL_TEST)).isEqualTo("static");
        assertThat(original.get(DYNAMIC_TEST)).isEqualTo("dynamic");

        assertThat(settings.get(DEFAULT_VAL_TEST)).isEqualTo("foobar");
        assertThat(settings.get(DYNAMIC_TEST)).isEqualTo("dynamic");
    }

    @Test
    void copy_copiesUnknown_Settings() {
        final Settings original = Settings.settings().withUnknown("test", setting -> "foobar").create();
        final Settings copy = original.copy().create();

        assertThat(copy.get(Setting.setting(String.class, "test", null))).isEqualTo("foobar");
    }

    @Test
    void given_unknown_type() {
        final Settings settings = Settings.settings().withUnknown("default", setting -> "foobar").create();
        assertThat(settings.get(DEFAULT_VAL_TEST)).isEqualTo("foobar");
    }

    @Test
    void given_point_in_settings_retrieves_pointer_value() {
        final Settings settings = Settings.settings()
            .withStatic(TEST_POINTER, "foobar")
            .withDynamic(DYNAMIC_TEST, () -> "barfoo")
            .create();
        assertThat(settings.get(TEST_POINTER)).isPresent().get().isEqualTo("foobar");
        assertThat(settings.get(DYNAMIC_TEST)).isEqualTo("barfoo");
    }
}
