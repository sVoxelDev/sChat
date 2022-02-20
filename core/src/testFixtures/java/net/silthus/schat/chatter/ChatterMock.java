/*
 * This file is part of sChat, licensed under the MIT License.
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.silthus.schat.chatter;

import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.assertj.core.api.ObjectAssert;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ChatterMock extends ChatterImpl {

    public static @NotNull ChatterMock randomChatter() {
        return chatterMock(randomIdentity());
    }

    public static Chatter randomChatter(UUID id) {
        return chatterMock(Identity.identity(id));
    }

    public static @NotNull ChatterMock chatterMock(Identity identity) {
        return new ChatterMock((Builder) Chatter.chatterBuilder(identity));
    }

    public static @NotNull ChatterMock chatterMock(Consumer<Chatter.Builder> builder) {
        return chatterMock(randomIdentity(), builder);
    }

    public static @NotNull ChatterMock chatterMock(Identity identity, Consumer<Chatter.Builder> builder) {
        final Chatter.Builder chatter = Chatter.chatterBuilder(identity);
        builder.accept(chatter);
        return new ChatterMock((Builder) chatter);
    }

    private int viewUpdateCount = 0;
    private boolean viewUpdated = false;
    private final PermissionHandler permissionHandler = mock(PermissionHandler.class);

    private ChatterMock(Builder builder) {
        super(builder);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(permission);
    }

    @Override
    public void updateView() {
        super.updateView();
        this.viewUpdateCount++;
        this.viewUpdated = true;
    }

    public void mockHasPermission(boolean result) {
        when(permissionHandler.hasPermission(any())).thenReturn(result);
    }

    public void mockHasPermission(String permission, boolean state) {
        when(permissionHandler.hasPermission(permission)).thenReturn(state);
    }

    public void assertReceivedMessage(Component text) {
        assertThat(messages())
            .extracting(Message::text)
            .contains(text);
    }

    public void assertReceivedMessage(Message message) {
        assertThat(messages()).contains(message);
    }

    public ObjectAssert<Channel> assertJoinedChannel(String key) {
        return assertThat(channels())
            .filteredOn(channel -> channel.key().equals(key))
            .isNotEmpty()
            .first();
    }

    public void assertJoinedChannel(String key, Component displayName) {
        assertJoinedChannel(key)
            .extracting(Channel::displayName)
            .isEqualTo(displayName);
    }

    public void assertActiveChannel(Channel channel) {
        assertThat(activeChannel()).isPresent().get()
            .isEqualTo(channel);
    }

    public void assertViewUpdated() {
        assertThat(viewUpdated).isTrue();
    }

    public void assertViewUpdated(int times) {
        assertThat(viewUpdateCount).isEqualTo(times);
    }

    public void assertViewNotUpdated() {
        assertThat(viewUpdated).isFalse();
    }

    public void resetViewUpdate() {
        viewUpdateCount = 0;
        viewUpdated = false;
    }

    public void assertJoinedChannel(Channel channel) {
        assertThat(isJoined(channel)).isTrue();
    }

    public void assertNotJoinedChannel(Channel channel) {
        assertThat(channels()).doesNotContain(channel);
        assertThat(channel.targets()).doesNotContain(this);
    }
}
