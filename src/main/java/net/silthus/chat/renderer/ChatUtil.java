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

package net.silthus.chat.renderer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import static net.kyori.adventure.text.Component.text;

public final class ChatUtil {

    public static final int PADDING = 1;
    public static final int MAX_LINE_LENGTH = 316;

    public static int getTextLength(Component component) {
        return getTextLength(LegacyComponentSerializer.legacySection().serialize(component));
    }

    public static int getTextLength(String str) {
        if (str == null || str.isEmpty()) return 0;
        str = ChatColor.translateAlternateColorCodes('&', str);
        int length = 0;
        boolean previousColorCode = false;
        boolean isBold = false;
        for (char c : str.toCharArray()) {
            if (ChatColor.COLOR_CHAR == c) {
                previousColorCode = true;
            } else if (previousColorCode) {
                isBold = ChatColor.BOLD.getChar() == c;
                previousColorCode = false;
            } else {
                FontInfo fontInfo = FontInfo.getFontInfo(c);
                length += isBold ? fontInfo.getBoldLength() : fontInfo.getLength();
                length += PADDING;
            }
        }
        return length;
    }

    public static Component wrapText(Component text, Component leftCorner, Component spacer, Component rightCorner) {
        int lineLength = MAX_LINE_LENGTH - getTextLength(leftCorner) - getTextLength(rightCorner);
        return text().append(leftCorner)
                .append(centerText(text, spacer, lineLength))
                .append(rightCorner)
                .build();
    }

    public static Component centerText(Component text, Component spacer) {
        return centerText(text, spacer, MAX_LINE_LENGTH);
    }

    public static Component spaceAndCenterText(Component leftCorner, Component divider, Component rightCorner, Component spacer, Component... text) {
        int lineLength = MAX_LINE_LENGTH - getTextLength(leftCorner) - getTextLength(rightCorner) - (getTextLength(divider) * (text.length - 1));
        int availableSpacePerText = lineLength / text.length;
        TextComponent.Builder builder = text();
        for (int i = 0; i < text.length; i++) {
            Component fragment = text[i];
            builder.append(centerText(fragment, spacer, availableSpacePerText));
            if (i != text.length - 1)
                builder.append(divider);
        }
        return text().append(leftCorner).append(builder.build()).append(rightCorner).build();
    }

    public static Component centerText(final Component text, final Component spacer, int lineLength) {
        int spacerLength = getTextLength(spacer);
        int compensated = 0;
        TextComponent.Builder prefix = text();
        TextComponent.Builder suffix = text();
        while (compensated < getPixelsToCompensate(text, lineLength) - spacerLength) {
            prefix.append(spacer);
            suffix.append(spacer);
            compensated += spacerLength;
        }
        return text().append(prefix).append(text).append(suffix).build();
    }

    private static int getPixelsToCompensate(Component text, int lineLength) {
        return lineLength / 2 - getTextLength(text) / 2;
    }
}
