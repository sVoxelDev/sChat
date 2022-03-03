package net.silthus.schat.events.channel;

import net.silthus.schat.channel.Channel;
import net.silthus.schat.events.SChatEvent;
import net.silthus.schat.pointer.Setting;
import net.silthus.schat.pointer.Settings;

/**
 * The event is fired if all settings of a channel change.
 *
 * <p>This is only the case if {@link Channel#settings(Settings)} is used to overwrite the channel settings.
 * The {@link ChannelSettingChangedEvent} is fired if a setting changed using {@link Channel#set(Setting, Object)}.</p>
 *
 * @see ChannelSettingChangedEvent
 * @since next
 */
public record ChannelSettingsChanged(
    Channel channel,
    Settings oldSettings,
    Settings newSettings
) implements SChatEvent {
}
