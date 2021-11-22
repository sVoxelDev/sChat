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

package net.silthus.chat.identities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.silthus.chat.ChatSource;
import net.silthus.chat.Constants;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.config.ConsoleConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public final class Console extends AbstractChatter implements ChatSource, Listener {

    public static Console instance;

    public static Console console() {
        if (instance == null)
            throw new UnsupportedOperationException("The console chat target is not initialized! Is sChat enabled?");
        return instance;
    }

    public static Console init(@NonNull ConsoleConfig config) {
        if (instance != null)
            throw new UnsupportedOperationException("The console chat target is already initialized. Can only initialize once!");
        instance = new Console(config);
        return instance;
    }

    private ConsoleConfig config;

    private Console(ConsoleConfig config) {
        super(UUID.randomUUID(), Constants.Targets.CONSOLE);
        setConfig(config);
    }

    public void setConfig(ConsoleConfig config) {
        this.config = config;
        setDisplayName(config.name());
    }

    @Override
    public Optional<Audience> getAudience() {
        return Optional.of(SChat.instance().getAudiences().console());
    }

    @Override
    protected void processMessage(Message message) {
        SChat.instance().getAudiences().console().sendMessage(message.formatted());
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsoleChat(ServerCommandEvent event) {
        if (event.getCommand().startsWith("/")) return;

        Optional.ofNullable(getActiveConversation())
                .or(() -> SChat.instance().getChannelRegistry().find(config.defaultChannel()))
                .ifPresent(conversation -> message(event.getCommand()).to(conversation).send());
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
