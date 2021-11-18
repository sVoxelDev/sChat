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

import co.aikar.locales.MessageKey;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.silthus.chat.SChat;
import net.silthus.chat.config.Language;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.silthus.chat.Constants.Language.Commands.COMMANDS_BASE;
import static net.silthus.chat.utils.Patterns.quotedPattern;

public abstract class SChatBaseCommand extends co.aikar.commands.BaseCommand {

    protected final SChat plugin;

    protected SChatBaseCommand(SChat plugin) {
        this.plugin = plugin;
    }

    protected Audience sender() {
        return plugin.getAudiences().sender(getCurrentCommandIssuer().getIssuer());
    }

    protected Component lang(String key) {
        return lang().get(key);
    }

    protected Language lang() {
        return plugin.language().section(COMMANDS_BASE);
    }

    protected MessageKey messageKey(String key) {
        return MessageKey.of(COMMANDS_BASE + "." + key);
    }

    protected void send(String key, String... replacements) {
        Component component = lang(key);
        final List<TextReplacementConfig> configs = getReplacements(replacements);
        for (TextReplacementConfig config : configs) {
            component = component.replaceText(config);
        }
        sender().sendMessage(component);
    }

    public List<TextReplacementConfig> getReplacements(String... replacements) {
        final ArrayList<TextReplacementConfig> configs = new ArrayList<>();
        if (replacements.length >= 2 && replacements.length % 2 == 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                String key = replacements[i];
                String value = replacements[i + 1];
                if (value == null) {
                    value = "";
                }

                configs.add(TextReplacementConfig.builder().match(quotedPattern(key)).replacement(text(value)).build());
            }

        } else {
            throw new IllegalArgumentException("Invalid Replacements");
        }
        return configs;
    }
}
