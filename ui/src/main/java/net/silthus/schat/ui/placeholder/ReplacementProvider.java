package net.silthus.schat.ui.placeholder;

import net.silthus.schat.message.Message;
import net.silthus.schat.pointer.Setting;

@FunctionalInterface
public interface ReplacementProvider {

    Setting<String> REPLACED_MESSAGE_FORMAT = Setting.setting(String.class, "replaced_message_format", null);

    String replaceText(Message message, String text);
}
