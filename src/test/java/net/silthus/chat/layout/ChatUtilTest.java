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