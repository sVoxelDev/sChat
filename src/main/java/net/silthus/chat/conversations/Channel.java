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

package net.silthus.chat.conversations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.silthus.chat.*;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.identities.Console;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Channel extends AbstractConversation implements ChatSource {

    public static Channel channel(String identifier) {
        return SChat.instance().getChannelRegistry().getOrCreate(identifier);
    }

    public static Channel channel(String identifier, ChannelConfig config) {
        return SChat.instance().getChannelRegistry().getOrCreate(identifier, config);
    }

    private ChannelConfig config;

    Channel(String identifier) {
        this(identifier, ChannelConfig.defaults());
    }

    Channel(String identifier, ChannelConfig config) {
        super(identifier);
        setConfig(config);
    }

    public void setConfig(ChannelConfig config) {
        removeScope(config);
        this.config = config;
        if (config.name() != null)
            setDisplayName(Component.text(config.name()));

        if (config.sendToConsole())
            addTarget(Console.console());
        else
            removeTarget(Console.console());

        setFormat(config.format());
        applyScope(config);
    }

    private void removeScope(ChannelConfig config) {
        if (this.config == null) return;
        if (!this.config.scope().equals(config.scope()))
            this.config.scope().onRemove(this);
    }

    private void applyScope(ChannelConfig config) {
        config.scope().onApply(this);
    }

    public String getPermission() {
        return Constants.Permissions.getChannelPermission(this);
    }

    public String getAutoJoinPermission() {
        return Constants.Permissions.getAutoJoinPermission(this);
    }

    @Override
    public Message sendMessage(String message) {
        return Message.message(message).to(this).send();
    }

    @Override
    protected void processMessage(Message message) {
        getScopedTargets(message).forEach(target -> target.sendMessage(message));
    }

    @Override
    public void close() {
        super.close();
        getConfig().scope().onRemove(this);
    }

    private Collection<ChatTarget> getScopedTargets(Message message) {
        if (getConfig().scope() == null) return getTargets();
        return getConfig().scope().filterTargets(this, message);
    }
}
