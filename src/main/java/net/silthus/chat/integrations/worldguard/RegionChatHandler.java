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

import com.google.common.base.Strings;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import net.silthus.chat.Chatter;
import net.silthus.chat.Constants;
import net.silthus.chat.SChat;
import net.silthus.chat.config.ChannelConfig;
import net.silthus.chat.conversations.Channel;
import net.silthus.chat.conversations.ChannelRegistry;
import net.silthus.chat.integrations.worldguard.WorldGuardIntegration.Flags;
import org.bukkit.Bukkit;

import java.util.Set;

public class RegionChatHandler extends Handler {

    static class Factory extends Handler.Factory<RegionChatHandler> {

        private final WorldGuardIntegration worldGuardIntegration;

        Factory(WorldGuardIntegration worldGuardIntegration) {
            this.worldGuardIntegration = worldGuardIntegration;
        }

        @Override
        public RegionChatHandler create(Session session) {
            return new RegionChatHandler(session, worldGuardIntegration.getPlugin().getChannelRegistry());
        }
    }

    private final ChannelRegistry channelRegistry;

    RegionChatHandler(Session session, ChannelRegistry channelRegistry) {
        super(session);
        this.channelRegistry = channelRegistry;
    }

    @Override
    public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
        handleRegionChannelCreation(player, set);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (noRegionsChanged(from, to, entered, exited)) return true;
        handleRegionChannelCreation(player, toSet);
        unsubscribeRegionChannels(player, exited);
        return true;
    }

    private void handleRegionChannelCreation(LocalPlayer player, ApplicableRegionSet set) {
        final Chatter chatter = Chatter.player(Bukkit.getPlayer(player.getUniqueId()));
        for (ProtectedRegion region : set) {
            final StateFlag.State value = region.getFlag(Flags.ENABLE_REGION_CHAT);
            final Channel channel = getRegionChannel(region);
            if (value == null || value == StateFlag.State.DENY)
                channel.close();
            else if (value == StateFlag.State.ALLOW)
                createAndSubscribeToRegion(chatter, region);
        }
        chatter.updateView();
    }

    private void unsubscribeRegionChannels(LocalPlayer player, Set<ProtectedRegion> exited) {
        final Chatter chatter = Chatter.player(Bukkit.getPlayer(player.getUniqueId()));
        for (ProtectedRegion region : exited) {
            final StateFlag.State value = region.getFlag(Flags.ENABLE_REGION_CHAT);
            if (value == StateFlag.State.ALLOW)
                chatter.unsubscribe(getRegionChannel(region));
            else if (value == null || value == StateFlag.State.DENY)
                getRegionChannel(region).close();
        }
        chatter.updateView();
    }

    private boolean noRegionsChanged(Location from, Location to, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited) {
        return entered.isEmpty() && exited.isEmpty()
                && from.getExtent().equals(to.getExtent());
    }

    private void createAndSubscribeToRegion(Chatter chatter, ProtectedRegion region) {
        final Channel channel = getRegionChannel(region);
        if (!chatter.canJoin(channel)) return;
        if (channel.getConfig().autoJoin())
            chatter.setActiveConversation(channel);
        else
            chatter.subscribe(channel);
    }

    private Channel getRegionChannel(ProtectedRegion region) {
        final String identifier = Constants.WorldGuard.REGION_CHANNEL_PREFIX + region.getId();
        if (channelRegistry.contains(identifier)) return channelRegistry.get(identifier);

        ChannelConfig config = SChat.instance().getPluginConfig().worldGuard().regionConfig(region.getId());
        final String name = region.getFlag(Flags.REGION_CHAT_NAME);
        if (!Strings.isNullOrEmpty(name))
            config = config.withName(name);
        else if (config.name() == null)
            config = config.withName(region.getId());

        return channelRegistry.register(new RegionChannel(identifier, config, region));
    }

}
