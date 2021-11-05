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

import org.assertj.core.api.Assertions;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

@Disabled
class ChatUtilTest {

    @Test
    void getLength_returnsStringLength() {
        final String str = "> [&6Admin] " + ChatColor.GRAY + "User: " + ChatColor.BOLD + "Hi!";
        Assertions.assertThat(ChatUtil.getTextLength(str)).isEqualTo(90);
    }

    @Test
    void centerText_addsLeftSpacingCharacter() {

        final String str = " &6Hello&l! ";
        String centerText = ChatUtil.centerText(str, FontInfo.MINUS.toString());
        assertNotExceedingMaxLineLength(centerText);
        assertThat(centerText).isEqualTo("------------------------ &6Hello&l! ------------------------");
    }

    @Test
    void frameText_addsSpacingAndLeftAndRightSuffix() {
        final String str = "Hi";
        String text = ChatUtil.wrapText(str, "|-", "-", "-|");
        assertNotExceedingMaxLineLength(text);
        assertThat(text).isEqualTo("|--------------------------Hi--------------------------|");
    }

    @Test
    void spaceAndCenterText() {
        final String[] text = {"Hi", "there", "friend!"};
        String result = ChatUtil.spaceAndCenterText("|- ", " | ", "-|", " ", text);
        assertNotExceedingMaxLineLength(result);
        assertThat(result).isEqualTo("|-        Hi        |      there      |     friend!    -|");
    }

    @Test
    void centerText_isNotLonger_thanMaxLineLength() {
        final String str = "Hi";
        String text = ChatUtil.centerText("Hi", "-");
        assertNotExceedingMaxLineLength(text);
    }

    private void assertNotExceedingMaxLineLength(String text) {
        int textLength = ChatUtil.getTextLength(text);
        assertThat(textLength).isLessThan(ChatUtil.MAX_LINE_LENGTH);
        assertThat(textLength).isCloseTo(ChatUtil.MAX_LINE_LENGTH, withinPercentage(5));
    }
}