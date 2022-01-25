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

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.identity.IdentityHelper.randomIdentity;
import static org.assertj.core.api.Assertions.assertThat;

public final class ChatterMock extends ChatterImpl {

    private ChatterMock(Builder builder) {
        super(builder);
    }

    public static @NotNull ChatterMock randomChatter() {
        return chatterMock(randomIdentity());
    }

    public static @NotNull ChatterMock chatterMock(Identity identity) {
        return new ChatterMock((Builder) Chatter.chatter(identity));
    }

    public static @NotNull ChatterMock chatterMock(Consumer<Chatter.Builder> builder) {
        final Chatter.Builder chatter = Chatter.chatter(randomIdentity());
        builder.accept(chatter);
        return new ChatterMock((Builder) chatter);
    }

    private int viewUpdateCount = 0;
    private boolean viewUpdated = false;

    public void assertReceivedMessage(Component text) {
        assertThat(getMessages())
            .extracting(Message::text)
            .contains(text);
    }

    @Override
    public void updateView() {
        super.updateView();
        this.viewUpdateCount++;
        this.viewUpdated = true;
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
}
