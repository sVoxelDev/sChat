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

package net.silthus.chat.integrations.bungeecord;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.google.gson.Gson;
import net.silthus.chat.ChatSource;
import net.silthus.chat.Identity;
import net.silthus.chat.Message;
import net.silthus.chat.TestBase;
import net.silthus.chat.targets.Chatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageDtoTests extends TestBase {

    @Test
    void create() {
        PlayerMock player = server.addPlayer();
        Message message = Message.message("test").from(ChatSource.player(player)).build();

        assertThat(serialized(message))
                .extracting(
                        MessageDto::message,
                        msg -> msg.sender().name(),
                        msg -> msg.sender().identifier(),
                        msg -> msg.sender().type()
                ).contains(
                        "{\"text\":\"test\"}",
                        "{\"text\":\"Player0\"}",
                        player.getUniqueId().toString(),
                        MessageDto.Sender.Type.PLAYER
                );
    }

    @Test
    void toMessage_createsMessageFromDto() {
        PlayerMock player = server.addPlayer();
        MessageDto dto = new MessageDto(Message.message("Hi").from(ChatSource.player(player)).build());
        Message message = dto.toMessage();
        assertThat(toText(message)).isEqualTo("Player0: Hi");
        assertThat(message.getSource())
                .isInstanceOf(Chatter.class)
                .extracting(
                        Identity::isPlayer,
                        Identity::getPlayer
                ).contains(
                        true,
                        player
                );
    }

    private MessageDto serialized(Message message) {
        Gson gson = new Gson();
        MessageDto dto = new MessageDto(message);
        return gson.fromJson(gson.toJson(dto), MessageDto.class);
    }
}
