package net.silthus.chat;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ChannelTests {

    private Channel channel;

    @BeforeEach
    void setUp() {
        channel = new Channel("test");
    }

    @Test
    void create() {

        assertThat(channel)
                .extracting(
                        Channel::getAlias,
                        Channel::getPermission
                ).contains(
                        "test",
                        Constants.CHANNEL_PERMISSION + ".test"
                );
        assertThat(channel.config())
                .extracting(
                        Channel.Config::name,
                        Channel.Config::prefix,
                        Channel.Config::suffix,
                        Channel.Config::color
                ).contains(
                        "test",
                        null,
                        ": ",
                        ChatColor.WHITE
                );
    }

    @Nested
    @DisplayName("with formatting")
    class Formatting extends TestBase {

        @Test
        void format() {

            channel.config()
                    .prefix(ChatColor.GOLD + "[Server]" + ChatColor.BLUE)
                    .suffix(ChatColor.RED + ": ");

            String message = channel.format(new ChatMessage(server.addPlayer(), "Hello chatters!"));

            assertThat(message).isEqualTo(ChatColor.GOLD + "[Server]" + ChatColor.BLUE + "Player0" + ChatColor.RED + ": " + ChatColor.WHITE + "Hello chatters!");
        }

        @Test
        void format_withNullPrefixOrSuffix() {

            String message = channel.format(new ChatMessage(server.addPlayer(), "test"));

            assertThat(message).isEqualTo("Player0: §ftest");
        }
    }

    @Nested
    @DisplayName("with config")
    class WithConfig {

        @Test
        void createFromConfig() {

            MemoryConfiguration cfg = new MemoryConfiguration();
            cfg.set("name", "Test");
            cfg.set("prefix", "[Test] ");
            cfg.set("suffix", " - ");
            cfg.set("color", "GRAY");

            Channel channel = new Channel("test", cfg);

            assertThat(channel.config())
                    .extracting(
                            Channel.Config::name,
                            Channel.Config::prefix,
                            Channel.Config::suffix,
                            Channel.Config::color
                    ).contains(
                            "Test",
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

            channel.config().color(ChatColor.BLUE);

            assertThatCode(() -> channel.config().color("FOO"))
                    .doesNotThrowAnyException();

            assertThat(channel.config().color()).isEqualTo(ChatColor.BLUE);
        }

        @Test
        void color_catchesInvalidLegacyColorCodes() {

            channel.config().color(ChatColor.BLUE);

            assertThatCode(() -> channel.config().color("&z"))
                    .doesNotThrowAnyException();

            assertThat(channel.config().color()).isEqualTo(ChatColor.BLUE);
        }

        private void setAndAssertColor(String colorCode, ChatColor expectedColor) {

            Channel.Config config = channel.config()
                    .color(colorCode);

            assertThat(config.color()).isEqualTo(expectedColor);
        }

    }
}
