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

package net.silthus.chat.formats;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.Constants;
import net.silthus.chat.Format;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.minimessage.template.TemplateResolver.templates;

@Log(topic = Constants.PLUGIN_NAME)
@EqualsAndHashCode(of = {"format"})
public class MiniMessageFormat implements Format {

    @Getter
    private final String format;

    public MiniMessageFormat(String miniMessage) {
        if (!miniMessage.contains("<message>")) {
            log.warning("Format '" + miniMessage + "' without <message> tag! Appending <message> tag...");
            miniMessage += "<message>";
        }
        this.format = miniMessage;
    }

    @Override
    public Component applyTo(Message message) {
        return MiniMessage.miniMessage().deserialize(format, templates(
                channelTemplate(message),
                vaultPrefixTemplate(message),
                senderWorld(message),
                senderTemplate(message),
                vaultSuffixTemplate(message),
                messageTemplate(message)
        ));
    }

    private Template messageTemplate(Message message) {
        return Template.template("message", message.getText());
    }

    private Template senderTemplate(Message message) {
        return Template.template("sender_name", message.getSource().getDisplayName());
    }

    private Template senderWorld(Message message) {
        final Player player = Bukkit.getPlayer(message.getSource().getUniqueId());
        if (player != null) {
            return Template.template("sender_world", player.getWorld().getName());
        }
        return Template.template("sender_world", Component.empty());
    }

    private Template channelTemplate(Message message) {
        if (message.getConversation() != null) {
            return Template.template("channel_name", message.getConversation().getDisplayName());
        }
        return Template.template("channel_name", Component.empty());
    }

    private Template vaultPrefixTemplate(Message message) {
        return Template.template("sender_vault_prefix", SChat.instance().getVaultProvider().getPrefix(message.getSource()));
    }

    private Template vaultSuffixTemplate(Message message) {
        return Template.template("sender_vault_suffix", SChat.instance().getVaultProvider().getSuffix(message.getSource()));
    }
}
