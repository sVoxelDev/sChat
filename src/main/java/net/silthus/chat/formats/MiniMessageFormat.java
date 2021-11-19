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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.*;
import net.silthus.chat.config.Language;
import net.silthus.chat.identities.PlayerChatter;
import net.silthus.chat.integrations.placeholders.Placeholders;
import net.silthus.configmapper.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.event.ClickEvent.clickEvent;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.minimessage.template.TemplateResolver.templates;
import static net.silthus.chat.Constants.Formatting.DEFAULT_FORMAT;
import static net.silthus.chat.Constants.Language.Formats.PLAYER_CLICK;

@Data
@Accessors(fluent = true)
@Log(topic = Constants.PLUGIN_NAME)
@EqualsAndHashCode(of = {"format"})
public class MiniMessageFormat implements Format {

    private final Placeholders placeholders;

    @ConfigOption
    private String format = DEFAULT_FORMAT;

    @Override
    public Component applyTo(Message message) {
        checkForMessageTag();
        final Component component = MiniMessage.miniMessage().deserialize(format, templates(
                channelTemplate(message),
                vaultPrefixTemplate(message),
                senderWorld(message),
                senderTemplate(message),
                vaultSuffixTemplate(message),
                messageTemplate(message)
        ));
        if (message.getSource() instanceof PlayerChatter) {
            return SChat.instance().getPlaceholders().setPlaceholders((PlayerChatter) message.getSource(), component);
        }
        return component;
    }

    private void checkForMessageTag() {
        if (!format.contains("<message>")) {
            log.warning("Format '" + format + "' without <message> tag! Appending <message> tag...");
            format += "<message>";
        }
    }

    private Template messageTemplate(Message message) {
        return Template.template("message", message.getText());
    }

    private Template senderTemplate(Message message) {
        return Template.template("sender_name", message.getSource().getDisplayName()
                .hoverEvent(showText(playerHover(message)))
                .clickEvent(sendPrivateMessage(message)));
    }

    private ClickEvent sendPrivateMessage(Message message) {
        return clickEvent(ClickEvent.Action.RUN_COMMAND, Constants.Commands.PRIVATE_MESSAGE.apply(message.getSource()));
    }

    private Component playerHover(Message message) {
        Component playerHover = lang().get(PLAYER_CLICK);
        final TextReplacementConfig playerNameReplacement = playerName(message.getSource());
        if (playerNameReplacement != null)
            playerHover = playerHover.replaceText(playerNameReplacement);
        return playerHover;
    }

    private TextReplacementConfig playerName(ChatSource source) {
        if (source == null) return null;
        final Player player = Bukkit.getPlayer(source.getUniqueId());
        if (player == null) return null;
        return TextReplacementConfig.builder()
                .match("<player_name>")
                .replacement(player.getDisplayName())
                .build();
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

    private Language lang() {
        return SChat.instance().language().section(Constants.Language.Formats.BASE_KEY);
    }
}
