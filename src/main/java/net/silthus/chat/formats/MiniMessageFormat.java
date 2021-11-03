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
        if (message.getChannel() != null) {
            return Template.template("channel_name", message.getChannel().getName());
        }
        return Template.template("channel_name", Component.empty());
    }
}
