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

package net.silthus.schat.channel;

import net.silthus.schat.pointer.Setting;

import static net.silthus.schat.pointer.Setting.setting;

public final class ChannelSettings {

    /**
     * The priority of the channel may determine the position in the view.
     *
     * <p>The lower the priority value the higher the priority of the channel.</p>
     *
     * <p>Default: {@code 100}.</p>
     */
    public static final Setting<Integer> PRIORITY = setting(Integer.class, "priority", 100);
    /**
     * A protected channel may restrict access to it, by running checks, such as a {@link #JOIN_PERMISSION} check.
     *
     * <p>Default: {@code false}</p>
     */
    public static final Setting<Boolean> PROTECTED = setting(Boolean.class, "protected", false);
    /**
     * Sets the permission that is required to join the channel, if the channel is protected.
     *
     * <p>Default: {@code 'schat.admin.channel.join'}</p>
     */
    public static final Setting<String> JOIN_PERMISSION = setting(String.class, "join_permission", "schat.channel.default.join");
    /**
     * Sets the channel as global, relaying messages to all servers in the network.
     *
     * <p>Default: {@code true}</p>
     */
    public static final Setting<Boolean> GLOBAL = setting(Boolean.class, "global", true);
    /**
     * Marks the channel as a private conversation between two players.
     *
     * <p>Default: {@code false}</p>
     */
    public static final Setting<Boolean> PRIVATE = setting(Boolean.class, "private", false);
    /**
     * Hides the channel in commands, auto completion and listings.
     *
     * <p>Default: {@code false}</p>
     */
    public static final Setting<Boolean> HIDDEN = setting(Boolean.class, "hidden", false);
    /**
     * If set to true auto joins players into the channel when they connect.
     *
     * <p>Default: {@code false}</p>
     */
    public static final Setting<Boolean> AUTO_JOIN = setting(Boolean.class, "auto_join", false);
    /**
     * Forces the player to stay in the channel preventing him from leaving.
     *
     * <p>Default: {@code false}</p>
     */
    public static final Setting<Boolean> FORCED = setting(Boolean.class, "force", false);

    private ChannelSettings() {
    }
}
