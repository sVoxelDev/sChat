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

package net.silthus.schat.message;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import org.junit.jupiter.api.Test;

import static net.silthus.schat.AssertionHelper.assertNPE;
import static net.silthus.schat.chatter.ChatterMock.randomChatter;
import static net.silthus.schat.message.NewMessage.message;
import static org.assertj.core.api.Assertions.assertThat;

class NewMessageTest implements Messenger {

    private boolean processCalled = false;
    private boolean deliverCalled = false;

    @Test
    void has_unique_id() {
        final NewMessage message = message();
        assertThat(message.id()).isNotNull();
        assertThat(message.id()).isNotEqualTo(message().id());
    }

    @Test
    void given_same_id_are_equal() {
        final UUID id = UUID.randomUUID();
        final NewMessage message = message().id(id).send(this);
        final NewMessage message2 = message().id(id).send(this);
        assertThat(message).isEqualTo(message2);
    }

    @Test
    void given_no_target_uses_nil_identity() {
        assertThat(message().target()).isEqualTo(Identity.nil());
    }

    @Test
    void given_no_text_uses_empty_component() {
        assertThat(message().text()).isEqualTo(Component.empty());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void send_given_null_messenger_throws_npe() {
        assertNPE(() -> message().send(null));
    }

    @Test
    void send_calls_process_on_messenger() {
        message().send(this);
        assertThat(processCalled).isTrue();
    }

    @Test
    void send_creates_message() {
        final NewMessage message = message().send(this);
        assertThat(message).isNotNull();
    }

    @Test
    void given_process_returns_different_message_then_send_uses_returned_processed_message() {
        final NewMessage message = message().send(new ProcessedMessageMessengerStub());
        assertThat(message.id()).isEqualTo(ProcessedMessageMessengerStub.STUB_ID);
    }

    @Test
    void send_calls_deliver_on_messenger() {
        message().send(this);
        assertThat(deliverCalled).isTrue();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void to_given_null_throws_npe() {
        assertNPE(() -> message().to(null));
    }

    @Test
    void to_given_chatter_adds_target() {
        final Chatter chatter = randomChatter();
        final NewMessage.Draft draft = message().to(chatter);
        assertThat(draft.target()).isEqualTo(chatter.getIdentity());
    }

    @Override
    public NewMessage.Draft process(NewMessage.Draft message) {
        processCalled = true;
        return message;
    }

    @Override
    public NewMessage deliver(NewMessage message) {
        deliverCalled = true;
        return message;
    }
}
