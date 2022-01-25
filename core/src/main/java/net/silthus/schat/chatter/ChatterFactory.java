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

import static net.silthus.schat.chatter.Chatter.chatter;
import static net.silthus.schat.view.ViewConnector.createSimpleViewConnector;

public abstract class ChatterFactory {
    protected final ViewProvider viewProvider;

    public ChatterFactory(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public final Chatter createChatter(UUID id) {
        return chatter(getIdentity(id))
            .viewConnector(chatter -> createSimpleViewConnector(chatter, viewProvider, getDisplay(id)))
            .permissionHandler(getPermissionHandler(id))
            .create();
    }

    @NotNull
    protected abstract Identity getIdentity(UUID id);

    protected abstract Chatter.PermissionHandler getPermissionHandler(UUID id);

    protected abstract Display getDisplay(UUID id);
}
