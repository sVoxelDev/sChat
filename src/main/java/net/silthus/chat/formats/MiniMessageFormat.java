package net.silthus.chat.formats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.Channel;
import net.silthus.chat.Format;
import net.silthus.chat.Message;

import static net.kyori.adventure.text.minimessage.template.TemplateResolver.templates;

public class MiniMessageFormat implements Format {

    private final String format;

    public MiniMessageFormat(String miniMessage) {
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
        return Template.template("message", message.getMessage());
    }

    private Template senderTemplate(Message message) {
        return Template.template("sender_name", message.getSource().getDisplayName());
    }

    private Template channelTemplate(Message message) {
        if (message.getTarget() instanceof Channel) {
            return Template.template("channel_name", ((Channel) message.getTarget()).getName());
        }
        return Template.template("channel_name", Component.empty());
    }
}
