package net.silthus.chat;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FormatTests {

    @Test
    void create_withDefaults() {

        Format format = Format.builder().build();

        assertThat(format)
                .extracting(
                        Format::getPrefix,
                        Format::getSuffix,
                        Format::getChatColor
                ).contains(
                        null,
                        ": ",
                        null
                );
    }

    @Test
    void create_withBuilder() {
        Format format = Format.builder()
                .prefix("[A]")
                .suffix("[B]")
                .chatColor(ChatColor.AQUA)
                .build();

        assertThat(format)
                .extracting(
                        Format::getPrefix,
                        Format::getSuffix,
                        Format::getChatColor
                ).contains(
                        "[A]",
                        "[B]",
                        ChatColor.AQUA
                );
    }

    @Test
    void createFromConfig() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("prefix", "[Test] ");
        cfg.set("suffix", " - ");
        cfg.set("chat_color", "GRAY");

        Format format = Format.of(cfg);

        assertThat(format)
                .extracting(
                        Format::getPrefix,
                        Format::getSuffix,
                        Format::getChatColor
                ).contains(
                        "[Test] ",
                        " - ",
                        ChatColor.GRAY
                );
    }

    @Test
    void color_withHexFormat() {

        setAndAssertColor("#000000", ChatColor.of(Color.BLACK));
    }

    @Test
    void color_withMcName() {

        setAndAssertColor("DARK_PURPLE", ChatColor.DARK_PURPLE);
    }

    @Test
    void color_withMcLegacyFormat() {

        setAndAssertColor("&7", ChatColor.GRAY);
    }

    @Test
    void color_catchesInvalidColors() {

        Format format = Format.builder()
                .chatColor("FOO")
                .build();

        assertThat(format.getChatColor()).isNull();
    }

    @Test
    void color_catchesInvalidLegacyColorCodes() {

        Format format = Format.builder()
                .chatColor("&z")
                .build();

        assertThat(format.getChatColor()).isNull();
    }

    @Test
    void withEmptyConfig() {

        Format format = Format.of(new MemoryConfiguration());

        assertThat(format)
                .extracting(
                        Format::getPrefix,
                        Format::getSuffix,
                        Format::getChatColor
                ).contains(
                        null,
                        ": ",
                        null
                );
    }

    @Test
    void config_withEmptyColorString() {

        MemoryConfiguration cfg = new MemoryConfiguration();
        cfg.set("color", "  ");

        Format format = Format.of(cfg);

        assertThat(format.getChatColor()).isNull();
    }

    private void setAndAssertColor(String colorCode, ChatColor expectedColor) {

        Format format = Format.builder()
                .chatColor(colorCode)
                .build();

        assertThat(format.getChatColor()).isEqualTo(expectedColor);

        format = Format.builder().build()
                .withChatColor(colorCode);
        assertThat(format.getChatColor()).isEqualTo(expectedColor);
    }

    @Nested
    @DisplayName("format(...)")
    class Formatting extends TestBase {

        private ChatSource source;

        @Override
        @BeforeEach
        public void setUp() {
            super.setUp();

            source = ChatSource.of(server.addPlayer());
        }

        @Test
        void format() {

            Format format = Format.builder()
                    .prefix(ChatColor.GOLD + "[Server]" + ChatColor.BLUE)
                    .suffix(ChatColor.RED + ": ")
                    .chatColor(ChatColor.GREEN)
                    .build();

            String message = format.applyTo(Message.of(source, "Hello chatters!"));

            assertThat(message).isEqualTo(ChatColor.GOLD + "[Server]" + ChatColor.BLUE + "Player0" + ChatColor.RED + ": " + ChatColor.GREEN + "Hello chatters!");
        }

        @Test
        void format_withNullPrefixOrSuffix() {
            String message = Format.builder()
                    .prefix(null)
                    .build()
                    .applyTo(Message.of(source, "test"));
            assertThat(message).isEqualTo("Player0: test");

            message = Format.builder()
                    .suffix(null)
                    .build()
                    .applyTo(Message.of(source, "test"));
            assertThat(message).isEqualTo("Player0test");
        }

        @Test
        void format_withNullColorIgnoresTheColor() {

            String message = Format.builder()
                    .chatColor((ChatColor) null)
                    .build()
                    .applyTo(Message.of(source, "test"));
            assertThat(message).isEqualTo("Player0: test");
        }

        @Test
        void format_withNullSource_ignoresSource_andPrefixSuffix() {
            String message = Format.builder().build()
                    .applyTo(Message.of("test"));

            assertThat(message).isEqualTo("test");
        }
    }
}
