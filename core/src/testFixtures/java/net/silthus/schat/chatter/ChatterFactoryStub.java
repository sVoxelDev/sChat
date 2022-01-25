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

import java.util.UUID;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.view.Display;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.view.ViewFactory.empty;
import static net.silthus.schat.view.ViewProvider.simpleViewProvider;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ChatterFactoryStub extends ChatterFactory {

    public ChatterFactoryStub() {
        super(simpleViewProvider(empty()));
    }

    public ChatterFactoryStub(ViewProvider viewProvider) {
        super(viewProvider);
    }

    @Override
    protected @NotNull Identity getIdentity(UUID id) {
        return Identity.identity(id, randomAlphabetic(10));
    }

    @Override
    protected Chatter.PermissionHandler getPermissionHandler(UUID id) {
        return permission -> false;
    }

    @Override
    protected Display getDisplay(UUID id) {
        return Display.empty();
    }
}
