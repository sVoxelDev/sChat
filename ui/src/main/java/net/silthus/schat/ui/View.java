package net.silthus.schat.ui;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.silthus.schat.pointer.Configured;
import net.silthus.schat.pointer.Setting;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;
import static net.silthus.schat.pointer.Setting.setting;

public interface View extends Configured.Modifiable<View> {

    Key VIEW_MARKER_KEY = Key.key("schat", "view");
    Component VIEW_MARKER = Component.storageNBT(VIEW_MARKER_KEY.asString(), VIEW_MARKER_KEY);

    Setting<Integer> VIEW_HEIGHT = setting(Integer.class, "format.height", 100); // minecraft chat box height in lines
    Setting<Format> ACTIVE_CHANNEL_FORMAT = setting(Format.class, "format.active_channel", name -> name.decorate(UNDERLINED));
    Setting<JoinConfiguration> CHANNEL_JOIN_CONFIG = setting(JoinConfiguration.class, "format.channel_join_config", JoinConfiguration.builder()
        .prefix(text("| "))
        .separator(text(" | "))
        .suffix(text(" |"))
        .build());
    Setting<Format> MESSAGE_SOURCE_FORMAT = setting(Format.class, "format.message_source", name -> name.append(text(": ")));

    Component render();

    default boolean isRenderedView(Component render) {
        return render.contains(VIEW_MARKER) || render.children().contains(VIEW_MARKER);
    }
}
