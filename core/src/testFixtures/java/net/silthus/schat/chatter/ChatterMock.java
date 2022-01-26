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

package net.silthus.schat.chatter;

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.message.Message;
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

    public void assertReceivedMessage(Component text) {
        assertThat(getMessages())
            .extracting(Message::text)
            .contains(text);
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
