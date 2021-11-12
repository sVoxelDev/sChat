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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

class ChatUtilTest {

    @Test
    void getLength_returnsStringLength() {
        TextComponent text = text().append(text("> ["))
                .append(text("Admin").color(NamedTextColor.RED))
                .append(text("]"))
                .append(text("User: ").color(NamedTextColor.GRAY))
                .append(text("Hi!").decorate(TextDecoration.BOLD))
                .build();
        Assertions.assertThat(ChatUtil.getTextLength(text)).isEqualTo(86);
    }

    @Test
    void centerText_addsLeftSpacingCharacter() {

        TextComponent text = text(" Hello").color(NamedTextColor.RED)
                .append(text("! ").decorate(TextDecoration.BOLD));

        Component centerText = ChatUtil.centerText(text, text("-"));
        assertNotExceedingMaxLineLength(centerText);
        assertThat(toText(centerText))
                .isEqualTo("-----------------------&c Hello&l! &r-----------------------");
    }

    @Test
    void centerText_reallyLongText() {
        final Component str = text(" Hi there my good old friend! ");
        Component text = ChatUtil.centerText(str, text("-"));
        assertNotExceedingMaxLineLength(text);
        assertThat(toText(text)).isEqualTo("------------- Hi there my good old friend! -------------");
    }

    @Test
    void frameText_addsSpacingAndLeftAndRightSuffix() {
        Component text = ChatUtil.wrapText(text("Hi"), text("|-"), text("-"), text("-|"));
        assertNotExceedingMaxLineLength(text);
        assertThat(toText(text)).isEqualTo("|-------------------------Hi-------------------------|");
    }

    @Test
    void spaceAndCenterText() {
        final Component[] text = {text("Hi"), text("there"), text("friend!")};
        Component result = ChatUtil.spaceAndCenterText(text("|-"), text(" | "), text("-|"), text(" "), text);
        assertNotExceedingMaxLineLength(result);
        assertThat(toText(result)).isEqualTo("|-          Hi           |        there        |        friend!       -|");
    }

    @Test
    void centerText_isNotLonger_thanMaxLineLength() {
        Component text = ChatUtil.centerText(text("Hi"), text("-"));
        assertNotExceedingMaxLineLength(text);
        assertThat(toText(text)).isEqualTo("-------------------------Hi-------------------------");
    }

    private void assertNotExceedingMaxLineLength(Component text) {
        int textLength = ChatUtil.getTextLength(text);
        assertThat(textLength).isLessThan(ChatUtil.MAX_LINE_LENGTH);
        assertThat(textLength).isCloseTo(ChatUtil.MAX_LINE_LENGTH, withinPercentage(6));
    }

    private String toText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }
}