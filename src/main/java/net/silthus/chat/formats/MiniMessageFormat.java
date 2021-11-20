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

package net.silthus.chat.formats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silthus.chat.Constants;
import net.silthus.chat.Format;
import net.silthus.chat.Message;
import net.silthus.chat.SChat;
import net.silthus.chat.identities.PlayerChatter;
import net.silthus.chat.integrations.placeholders.Placeholders;
import net.silthus.chat.renderer.ChatUtil;
import net.silthus.configmapper.ConfigOption;

import static net.silthus.chat.Constants.Formatting.DEFAULT_FORMAT;

@Data
@Accessors(fluent = true)
@Log(topic = Constants.PLUGIN_NAME)
@EqualsAndHashCode(of = {"format"})
public class MiniMessageFormat implements Format {

    private final Placeholders placeholders;

    @ConfigOption
    private String format = DEFAULT_FORMAT;
    @ConfigOption
    private boolean center = false;
    @ConfigOption
    private String centerSpacer = " ";

    @Override
    public Component applyTo(Message message) {
        final MiniMessage miniMessage = MiniMessage.builder()
                .templateResolver(message)
                .build();
        Component component = miniMessage.deserialize(format, new MiniMessageFormatTemplateResolver());
        if (message.getSource() instanceof PlayerChatter)
            component = SChat.instance().getPlaceholders().setPlaceholders((PlayerChatter) message.getSource(), component);
        if (center)
            component = ChatUtil.centerText(component, miniMessage.deserialize(centerSpacer, new MiniMessageFormatTemplateResolver()));
        return component;
    }
}
