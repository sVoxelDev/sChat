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

package net.silthus.chat.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.locales.MessageKey;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silthus.chat.Chatter;
import net.silthus.chat.SChat;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.Language.Commands.Nicknames.*;
import static net.silthus.chat.Constants.*;

@CommandAlias("nickname")
public class NicknameCommands extends BaseCommand {

    private final SChat plugin;

    public NicknameCommands(SChat plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandAlias("nick")
    @CommandCompletion("*")
    @CommandPermission(PERMISSION_NICKNAME_SET)
    public void setSelf(@Flags("self") Chatter chatter, String name) {
        validateAndSetNickname(chatter, name);
    }

    @Subcommand("set")
    @CommandCompletion("@chatters *")
    @CommandPermission(PERMISSION_NICKNAME_SET_OTHERS)
    public void set(Chatter chatter, String name) {
        validateAndSetNickname(chatter, name);
    }

    @Subcommand("reset")
    @CommandAlias("nick reset")
    @CommandPermission(PERMISSION_NICKNAME_SET)
    public void resetSelf(@Flags("self") Chatter chatter) {
        resetNickname(chatter);
    }

    @Subcommand("reset player")
    @CommandCompletion("@chatters")
    @CommandPermission(PERMISSION_NICKNAME_SET_OTHERS)
    public void reset(Chatter chatter) {
        resetNickname(chatter);
    }

    private void resetNickname(Chatter chatter) {
        chatter.setDisplayName(null);
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(RESET),
                "{nickname}", getDisplayName(chatter)
        );
    }

    private void validateAndSetNickname(Chatter chatter, String name) {
        if (!getCurrentCommandIssuer().hasPermission(PERMISSION_NICKNAME_SET_BLOCKED))
            validateNickname(name);
        setNickname(chatter, name);
    }

    private void setNickname(Chatter chatter, String name) {
        String oldName = getDisplayName(chatter);
        chatter.setDisplayName(text(name));
        getCurrentCommandIssuer().sendMessage(MessageType.INFO, key(CHANGED),
                "{nickname}", name,
                "{old_nickname}", oldName
        );
    }

    @NotNull
    private String getDisplayName(Chatter chatter) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(chatter.getDisplayName());
    }

    private void validateNickname(String name) {
        validateNicknamePattern(name);
        validateBlockedNicknames(name);
    }

    private void validateBlockedNicknames(String name) {
        boolean nicknameIsBlocked = plugin.getPluginConfig().player().blockedNickNames()
                .stream().map(s -> Pattern.compile(s, Pattern.CASE_INSENSITIVE))
                .map(pattern -> pattern.matcher(name))
                .anyMatch(Matcher::matches);
        if (nicknameIsBlocked)
            throw new ConditionFailedException(key(BLOCKED), "{nickname}", name);
    }

    private void validateNicknamePattern(String name) {
        Matcher matcher = plugin.getPluginConfig().player().nickNamePattern().matcher(name);
        if (!matcher.matches())
            throw new ConditionFailedException(key(INVALID), "{nickname}", name);
    }

    private MessageKey key(String key) {
        return SChatCommands.key(NICKNAMES_BASE + "." + key);
    }
}
