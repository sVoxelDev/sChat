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

package net.silthus.chat.integrations.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import lombok.Getter;
import lombok.extern.java.Log;
import net.silthus.chat.Constants;
import net.silthus.chat.SChat;

@Getter
@Log(topic = Constants.PLUGIN_NAME)
public class WorldGuardIntegration {

    public static class Flags {

        public static final StateFlag ENABLE_REGION_CHAT = new StateFlag("schat:region-chat", false);
        public static final StringFlag REGION_CHAT_NAME = new StringFlag("schat:region-chat-name");
    }

    private final SChat plugin;
    private final WorldGuard worldGuard;

    public WorldGuardIntegration(SChat plugin, WorldGuard worldGuard) {
        this.plugin = plugin;
        this.worldGuard = worldGuard;
    }

    public void load() {
        registerFlag(Flags.ENABLE_REGION_CHAT);
        registerFlag(Flags.REGION_CHAT_NAME);
    }

    public void enable() {
        registerSessionHandler(new RegionChatHandler.Factory(this));
    }

    private <TType> void registerFlag(Flag<TType> flag) {
        try {
            worldGuard.getFlagRegistry().register(flag);
            log.info("Registered Custom WorldGuard Flag: " + flag.getName());
        } catch (FlagConflictException e) {
            log.severe(flag.getName() + " is already registered by an other plugin!");
        }
    }

    private void registerSessionHandler(Handler.Factory<?> factory) {
        SessionManager sessionManager = worldGuard.getPlatform().getSessionManager();
        sessionManager.registerHandler(factory, null);
    }
}
