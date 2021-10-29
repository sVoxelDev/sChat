package net.silthus.chat;

import org.bukkit.ChatColor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class ChatUtilTest {

    @Test
    void getLength_returnsStringLength() {
        final String str = "> [&6Admin] " + ChatColor.GRAY + "User: " + ChatColor.BOLD + "Hi!";
        assertThat(ChatUtil.getTextLength(str)).isEqualTo(90);
    }

    @Test
    void centerText_addsLeftSpacingCharacter() {

        final String str = " &6Hello&l! ";
        String centerText = ChatUtil.centerText(str, FontInfo.MINUS.toString());
        assertThat(centerText).isEqualTo("-------------------- &6Hello&l! --------------------");
    }

    @Test
    void frameText_addsSpacingAndLeftAndRightSuffix() {
        final String str = "Hi";
        String text = ChatUtil.wrapText(str, "|-", "-", "-|");
        assertThat(text).isEqualTo("|----------------------Hi----------------------|");
    }

    @Test
    void spaceAndCenterText() {
        final String[] text = {"Hi", "there", "friend!"};
        String result = ChatUtil.spaceAndCenterText("|- ", " | ", "-|", " ", text);
        assertThat(result).isEqualTo("|-      Hi      |    there    |   friend!  -|");
    }
}