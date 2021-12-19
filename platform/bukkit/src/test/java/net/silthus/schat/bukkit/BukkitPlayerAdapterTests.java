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

package net.silthus.schat.bukkit;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.examination.string.MultiLineStringExaminer;
import net.silthus.schat.identity.Identity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.assertj.core.api.Assertions.assertThat;

class BukkitPlayerAdapterTests extends BukkitTestBase {

    private BukkitPlayerAdapter adapter;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        super.setUp();
        adapter = new BukkitPlayerAdapter();
        player = getServer().addPlayer();
    }

    private void assertSameAsPlayer(final Identity identity) {
        assertThat(identity).extracting(
            Identity::getId,
            Identity::getName,
            Identity::getDisplayName
        ).contains(
            player.getUniqueId(),
            player.getName(),
            player.getDisplayName()
        );
    }

    @Nested
    class FromPlayer {

        private Identity identity;

        @BeforeEach
        void setUp() {
            identity = adapter.fromPlayer(player);
        }

        @Test
        void has_all_properties() {
            assertSameAsPlayer(identity);
        }

        @Test
        void has_dynamic_display_name() {
            player.setDisplayName("foobar");
            assertThat(identity.getDisplayName()).isEqualTo(text("foobar"));
        }

        @Test
        void display_name_is_serialized() {
            final Identity identity = adapter.fromPlayer(player);
            player.setDisplayName(ChatColor.RED + "King " + ChatColor.GREEN + "Lui");
            final TextComponent expected = text().append(text("King ", RED)).append(text("Lui", GREEN)).build();

            Assertions.assertEquals(prettyPrintComponent(expected), prettyPrintComponent(identity.getDisplayName()));
        }

        final String prettyPrintComponent(final Component component) {
            return component.examine(MultiLineStringExaminer.simpleEscaping()).collect(Collectors.joining("\n"));
        }

    }

    @Nested
    class FromId {

        @Test
        void fromId_returnsPlayer() {
            final Optional<Identity> identity = adapter.fromId(player.getUniqueId());
            assertThat(identity).isPresent();
            assertSameAsPlayer(identity.get());
        }

        @Test
        void fromId_givenUnknownPlayer_returnsEmpty() {
            assertThat(adapter.fromId(UUID.randomUUID())).isEmpty();
        }
    }

    @Nested
    class ToPlayer {

        @Test
        void toPlayer_returnsPlayer() {
            final Optional<Player> player = adapter.toPlayer(Identity.identity(BukkitPlayerAdapterTests.this.player.getUniqueId()));
            assertThat(player).isPresent().isSameAs(player);
        }
    }
}
