/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
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
 * @since 1.0.0
 */
public record ChannelSettingsChanged(
    Channel channel,
    Settings oldSettings,
    Settings newSettings
) implements SChatEvent {
}
