/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.chat;

import net.silthus.chat.conversations.Channel;
import net.silthus.chat.renderer.View;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

public interface Chatter extends Identity, ChatTarget, ChatSource, Listener {

    static Chatter player(OfflinePlayer player) {
        return SChat.instance().getChatterManager().getOrCreateChatter(player);
    }

    static Chatter chatter(Identity identity) {
        return SChat.instance().getChatterManager().getOrCreateChatter(identity);
    }

    View getView();

    void setView(View view);

    void updateView();

    void join(Channel channel) throws AccessDeniedException;

    boolean canJoin(Channel channel);

    boolean canAutoJoin(Channel channel);

    boolean canLeave(Conversation conversation);

    boolean canSendMessage(Channel channel);

    boolean hasPermission(String permission);

    void save();

    void load();
}
