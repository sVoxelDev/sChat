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

import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.Constants;
import net.silthus.chat.Format;
import net.silthus.chat.Message;

import static net.kyori.adventure.text.minimessage.template.TemplateResolver.templates;

@Log(topic = Constants.PLUGIN_NAME)
public class MiniMessageFormat implements Format {

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
                messageTemplate(message),
                senderTemplate(message),
                channelTemplate(message)
        ));
    }

    private Template messageTemplate(Message message) {
        return Template.template("message", message.getText());
    }

    private Template senderTemplate(Message message) {
        return Template.template("sender_name", message.getSource().getName());
    }

    private Template channelTemplate(Message message) {
        if (message.getConversation() != null) {
            return Template.template("channel_name", message.getConversation().getName());
        }
        return Template.template("channel_name", Component.empty());
    }
}
