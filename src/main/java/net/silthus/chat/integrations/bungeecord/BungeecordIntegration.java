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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import lombok.extern.java.Log;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static net.silthus.chat.Constants.BUNGEECORD_CHANNEL;
import static net.silthus.chat.Constants.SCHAT_MESSAGES_CHANNEL;

@Log(topic = Constants.PLUGIN_NAME)
@SuppressWarnings("UnstableApiUsage")
public class BungeecordIntegration implements PluginMessageListener {

    private final SChat plugin;
    private final Gson gson = new Gson();

    // TODO: synchronize chatters across servers using offline chatter
    // TODO: send channels/conversations across servers
    public BungeecordIntegration(SChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] serverMessage) {
        if (!channel.equals(BUNGEECORD_CHANNEL)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(serverMessage);
        String subChannel = in.readUTF();
        if (subChannel.equals(SCHAT_MESSAGES_CHANNEL)) {
            short len = in.readShort();
            byte[] bytes = new byte[len];
            in.readFully(bytes);

            String json = ByteStreams.newDataInput(bytes).readUTF();
            // TODO: remove debug print
            log.info("Got plugin channel message:");
            log.info(json);
            MessageDto dto = gson.fromJson(json, MessageDto.class);
            Message message = dto.toMessage().copy().send();
        }
    }

    public void sendServerMessage(Message message) {
        Bukkit.getOnlinePlayers().stream()
                .findFirst()
                .ifPresent(player -> {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Forward");
                    out.writeUTF("ALL");
                    out.writeUTF(SCHAT_MESSAGES_CHANNEL);

                    ByteArrayDataOutput dtoOut = ByteStreams.newDataOutput();
                    // TODO: remove debug print
                    String json = gson.toJson(new MessageDto(message));
                    log.info("Sending plugin channel message:");
                    log.info(json);
                    dtoOut.writeUTF(json);

                    byte[] bytes = dtoOut.toByteArray();
                    out.writeShort(bytes.length);
                    out.write(bytes);

                    if (!SChat.isTesting())
                        player.sendPluginMessage(plugin, BUNGEECORD_CHANNEL, out.toByteArray());
                });
    }
}
