package net.silthus.chat.commands;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.silthus.chat.Chatter;
import net.silthus.chat.Constants;
import net.silthus.chat.Identity;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;

class NicknameCommandsTest extends TestBase {

    private PlayerMock player;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        player = server.addPlayer();
        player.addAttachment(plugin, Constants.PERMISSION_NICKNAME_SET, true);
    }

    @Test
    void nick_changesTheChattersDisplayName() {
        player.performCommand("nick Cool");
        assertThat(player.getDisplayName()).isEqualTo("Cool");
        assertThat(Chatter.player(player))
                .extracting(Identity::getDisplayName)
                .isEqualTo(text("Cool"));
    }

    @Test
    void nick_blocksNamesNotMatchingPattern() {
        player.performCommand("nick %[!#_%");
        assertThat(getLastMessage(player)).contains("is not valid");
    }

    @Test
    void nick_isBlockedIfMatchesBlockPattern() {
        player.performCommand("nickname Administrator");
        assertThat(getLastMessage(player)).contains("cannot be used");
    }

    @Test
    void nick_blocksNamesInBlacklist() {
        player.performCommand("nick nOtcH");
        assertThat(getLastMessage(player)).contains("cannot be used");
    }

    @Test
    void nick_reset_setsPlayerName() {
        Chatter chatter = Chatter.player(player);
        chatter.setDisplayName(text("Test"));
        player.setDisplayName("Test");
        player.performCommand("nickname reset");
        assertThat(chatter.getDisplayName()).isEqualTo(text("Player0"));
        assertThat(player.getDisplayName()).isEqualTo("Player0");
    }

    @Test
    void setOther_failsWithNoPermission() {
        PlayerMock player1 = server.addPlayer();
        player.performCommand("nickname set Player1 Foobar");
        assertThat(Chatter.player(player1).getDisplayName()).isEqualTo(text("Player1"));
        assertThat(player1.getDisplayName()).isEqualTo("Player1");
        assertThat(getLastMessage(player)).contains("you do not have permission to perform this command");
    }

    @Test
    void resetOther_failsWithNoPermission() {
        PlayerMock player1 = server.addPlayer();
        player.performCommand("nickname reset Player1");
        assertThat(Chatter.player(player1).getDisplayName()).isEqualTo(text("Player1"));
        assertThat(player1.getDisplayName()).isEqualTo("Player1");
        assertThat(getLastMessage(player)).contains("you do not have permission to perform this command");
    }

    @Test
    void setOther_setsNickName() {
        player.addAttachment(plugin, Constants.PERMISSION_NICKNAME_SET_OTHERS, true);
        PlayerMock player1 = server.addPlayer();

        player.performCommand("nickname set Player1 Foobar");

        assertThat(Chatter.player(player1).getDisplayName()).isEqualTo(text("Foobar"));
        assertThat(player1.getDisplayName()).isEqualTo("Foobar");
    }

    @Test
    void resetOther_resetsNickName() {
        player.addAttachment(plugin, Constants.PERMISSION_NICKNAME_SET_OTHERS, true);
        PlayerMock player1 = server.addPlayer();
        player1.setDisplayName("Foobar");
        Chatter.player(player1).setDisplayName(text("Foobar"));

        player.performCommand("nickname reset Player1");

        assertThat(Chatter.player(player1).getDisplayName()).isEqualTo(text("Player1"));
        assertThat(player1.getDisplayName()).isEqualTo("Player1");
    }
}