package net.silthus.schat.events.channel;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;

/**
 * The event is fired when a setting of a channel has changed.
 *
 * <p>This event is only fired if a setting changes using {@link Channel#set(Setting, Object)}.
 * The {@link ChannelSettingsChanged} event is fired if all settings are replaced using {@link Channel#settings(Settings)}.</p>
 *
 * @param <V> the type of the setting
 * @see ChannelSettingsChanged
 * @since next
 */
public record ChannelSettingChangedEvent<V>(
    Channel channel,
    Setting<V> setting,
    V oldValue,
    V newValue
) implements SChatEvent {
}
