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

import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.silthus.chat.annotations.Name;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;

public class FormatsTests extends TestBase {

    @Test
    void getFormat_returns_empty() {
        Optional<Format> format = Formats.format("abc");
        assertThat(format).isEmpty();
    }

    @Test
    void getFormat_withClass_returnsFormat() {
        final TestFormat format = Formats.format(TestFormat.class);
        assertThat(format).isNotNull();
        assertThat(format.format("test"))
                .extracting(TestFormat::format)
                .isEqualTo("test");
    }

    @Test
    void register_addsFormat() {
        Formats.registerFormat(TestFormat.class);
        assertThat(Formats.format("test"))
                .isPresent().get()
                .isInstanceOf(TestFormat.class);
    }

    @Test
    void register_withSupplier() {
        Formats.registerFormat(FormatWithSupplier.class, FormatWithSupplier::new);
        assertThat(Formats.format("my-format"))
                .isPresent().get()
                .isInstanceOf(FormatWithSupplier.class);
    }

    @Test
    void create_withConfig() {
        Formats.registerFormat(TestFormat.class);
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("format", "foobar");
        Optional<Format> format = Formats.format("test", cfg);
        assertThat(format).isPresent().get()
                .extracting("format").isEqualTo("foobar");
    }

    @Test
    void registerFormatTemplate_addsTemplate() {
        final MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("format", "test");
        Formats.registerFormatTemplate("test-template", TestFormat.class, cfg);
        Optional<Format> format = Formats.formatFromTemplate("test-template");
        assertThat(format).isPresent().get()
                .isInstanceOf(TestFormat.class)
                .extracting("format")
                .isEqualTo("test");
    }

    @Data
    @Accessors(fluent = true)
    static class TestFormat implements Format {

        @ConfigOption
        private String format;

        @Override
        public Component applyTo(Message message) {
            return message.getSource().getDisplayName().append(text(": ")).append(message.getText());
        }
    }

    @Name("my-format")
    private static class FormatWithSupplier implements Format {

        @Override
        public Component applyTo(Message message) {
            return null;
        }
    }
}
