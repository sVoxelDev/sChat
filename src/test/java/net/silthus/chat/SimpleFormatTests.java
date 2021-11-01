package net.silthus.chat;

import net.md_5.bungee.api.ChatColor;
import net.silthus.chat.formats.SimpleFormat;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleFormatTests {

    @Test
    void create_withDefaults() {

        SimpleFormat format = SimpleFormat.builder().build();

        assertThat(format)
                .extracting(
                        SimpleFormat::getPrefix,
                        SimpleFormat::getSuffix,
                        SimpleFormat::getChatColor
                ).contains(
                        null,
                        ": ",
                        null
                );
    }

    @Test
    void create_withBuilder() {
        SimpleFormat format = SimpleFormat.builder()
                .prefix("[A]")
                .suffix("[B]")
                .chatColor(ChatColor.AQUA)
                .build();

        assertThat(format)
                .extracting(
                        SimpleFormat::getPrefix,
                        SimpleFormat::getSuffix,
                        SimpleFormat::getChatColor
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

        SimpleFormat format = Format.fromConfig(cfg);

        assertThat(format)
                .extracting(
                        SimpleFormat::getPrefix,
                        SimpleFormat::getSuffix,
                        SimpleFormat::getChatColor
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

        SimpleFormat format = SimpleFormat.builder()
                .chatColor("FOO")
                .build();

        assertThat(format.getChatColor()).isNull();
    }

    @Test
    void color_catchesInvalidLegacyColorCodes() {

        SimpleFormat format = SimpleFormat.builder()
                .chatColor("&z")
                .build();

        assertThat(format.getChatColor()).isNull();
    }

    @Test
    void withEmptyConfig() {

        SimpleFormat format = Format.fromConfig(new MemoryConfiguration());

        assertThat(format)
                .extracting(
                        SimpleFormat::getPrefix,
                        SimpleFormat::getSuffix,
                        SimpleFormat::getChatColor
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

        SimpleFormat format = Format.fromConfig(cfg);

        assertThat(format.getChatColor()).isNull();
    }

    private void setAndAssertColor(String colorCode, ChatColor expectedColor) {

        SimpleFormat format = SimpleFormat.builder()
                .chatColor(colorCode)
                .build();

        assertThat(format.getChatColor()).isEqualTo(expectedColor);

        format = SimpleFormat.builder().build()
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

            SimpleFormat format = SimpleFormat.builder()
                    .prefix(ChatColor.GOLD + "[Server]" + ChatColor.BLUE)
                    .suffix(ChatColor.RED + ": ")
                    .chatColor(ChatColor.GREEN)
                    .build();

            String message = format.applyTo(Message.of(source, "Hello chatters!"));

            assertThat(message).isEqualTo(ChatColor.GOLD + "[Server]" + ChatColor.BLUE + "Player0" + ChatColor.RED + ": " + ChatColor.GREEN + "Hello chatters!");
        }

        @Test
        void format_withNullPrefixOrSuffix() {
            String message = SimpleFormat.builder()
                    .build()
                    .withPrefix(null)
                    .applyTo(Message.of(source, "test"));
            assertThat(message).isEqualTo("Player0: test");

            message = SimpleFormat.builder()
                    .build()
                    .withSuffix(null)
                    .applyTo(Message.of(source, "test"));
            assertThat(message).isEqualTo("Player0test");
        }

        @Test
        void format_withNullColorIgnoresTheColor() {

            String message = SimpleFormat.builder()
                    .chatColor((ChatColor) null)
                    .build()
                    .applyTo(Message.of(source, "test"));
            assertThat(message).isEqualTo("Player0: test");
        }

        @Test
        void format_withNullSource_ignoresSource_andPrefixSuffix() {
            String message = SimpleFormat.builder().build()
                    .applyTo(Message.of("test"));

            assertThat(message).isEqualTo("test");
        }
    }
}
