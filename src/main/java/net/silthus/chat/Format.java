package net.silthus.chat;

import net.kyori.adventure.text.Component;
import net.silthus.chat.formats.MiniMessageFormat;

public interface Format {

    static Format defaultFormat() {
        return miniMessage(Constants.Formatting.DEFAULT_FORMAT);
    }

    static Format channelFormat() {
        return miniMessage(Constants.Formatting.DEFAULT_CHANNEL_FORMAT);
    }

    static Format noFormat() {
        return miniMessage(Constants.Formatting.NO_FORMAT);
    }

    static Format miniMessage(String format) {
        return new MiniMessageFormat(format);
    }

    Component applyTo(Message message);
}
