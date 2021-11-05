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

package net.silthus.chat.layout;

import org.bukkit.ChatColor;

public final class ChatUtil {

    public static final int PADDING = 1;
    public static final int MAX_LINE_LENGTH = 316;

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

    public static String wrapText(String text, String leftCorner, String spacer, String rightCorner) {
        int lineLength = MAX_LINE_LENGTH - getTextLength(leftCorner) - getTextLength(rightCorner);
        return leftCorner + centerText(text, spacer, lineLength) + rightCorner;
    }

    public static String centerText(String text, String spacer) {
        return centerText(text, spacer, MAX_LINE_LENGTH);
    }

    public static String spaceAndCenterText(String leftCorner, String divider, String rightCorner, String spacer, String... text) {
        StringBuilder sb = new StringBuilder();
        int availableSpacePerText = (MAX_LINE_LENGTH / text.length) - (getTextLength(leftCorner) + getTextLength(rightCorner) + (getTextLength(divider) * (text.length - 1)));
        for (int i = 0; i < text.length; i++) {
            String fragment = text[i];
            sb.append(centerText(fragment, spacer, availableSpacePerText));
            if (i != text.length - 1)
                sb.append(divider);
        }
        return leftCorner + sb + rightCorner;
    }

    private static String centerText(String text, String spacer, int lineLength) {
        int spacerLength = getTextLength(spacer);
        int compensated = 0;
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        while (compensated < getPixelsToCenterText(text, lineLength)) {
            prefix.append(spacer);
            suffix.append(spacer);
            compensated += spacerLength;
        }
        return prefix.append(text).append(suffix).toString();
    }

    private static int getPixelsToCenterText(String text, int lineLength) {
        return getCenterPixel(lineLength) - getTextLength(text) / 2;
    }

    private static int getCenterPixel(int lineLength) {
        return lineLength / 2 - PADDING;
    }
}
