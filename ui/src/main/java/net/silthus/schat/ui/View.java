package net.silthus.schat.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.settings.Configured;
import net.silthus.schat.settings.Setting;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.settings.Setting.setting;

public interface View extends Configured.Modifiable<View> {

    Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "format.active_channel", name -> name.decorate(UNDERLINED));
    Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "format.channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());
    Setting<Format> MESSAGE_SOURCE_FORMAT = setting(Format.class, "format.message_source", name -> name.append(text(": ")));

    Component render();
}
