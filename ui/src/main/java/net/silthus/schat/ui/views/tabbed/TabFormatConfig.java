package net.silthus.schat.ui.views.tabbed;

import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silthus.schat.message.Message;
import net.silthus.schat.message.MessageSource;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.ui.format.Format;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@Data
@Accessors(fluent = true)
@ConfigSerializable
public class TabFormatConfig {

    public static final Setting<TabFormatConfig> FORMAT_CONFIG = Setting.setting(TabFormatConfig.class, "format", new TabFormatConfig());

    private TextColor activeColor = NamedTextColor.GREEN;
    private TextDecoration activeDecoration = TextDecoration.UNDERLINED;

    private TextColor inactiveColor = NamedTextColor.GRAY;
    private TextDecoration inactiveDecoration = null;

    private Format messageFormat = (view, msg) ->
        msg.get(Message.SOURCE)
            .filter(MessageSource.IS_NOT_NIL)
            .map(identity -> identity.displayName().colorIfAbsent(YELLOW).append(text(": ", GRAY)))
            .orElse(Component.empty())
            .append(((Message) msg).text().colorIfAbsent(GRAY));

    private Format selfMessageFormat = (view, msg) ->
        translatable("schat.chat.message.you").color(DARK_AQUA)
            .append(text(": ", GRAY))
            .append(((Message) msg).text().colorIfAbsent(GRAY));
}
