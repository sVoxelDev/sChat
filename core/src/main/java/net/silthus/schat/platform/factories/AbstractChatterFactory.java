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

package net.silthus.schat.platform.factories;

import java.util.UUID;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.chatter.ChatterFactory;
import net.silthus.schat.identity.Identity;
import net.silthus.schat.view.ViewConnector;
import net.silthus.schat.view.ViewProvider;
import org.jetbrains.annotations.NotNull;

import static net.silthus.schat.chatter.Chatter.chatter;

public abstract class AbstractChatterFactory implements ChatterFactory {
    protected final ViewProvider viewProvider;

    public AbstractChatterFactory(ViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    @Override
    public final Chatter createChatter(UUID id) {
        return chatter(createIdentity(id))
            .viewConnector(createViewConnector(id))
            .permissionHandler(createPermissionHandler(id))
            .create();
    }

    @NotNull
    protected abstract Identity createIdentity(UUID id);

    protected abstract Chatter.PermissionHandler createPermissionHandler(UUID id);

    protected abstract ViewConnector.Factory createViewConnector(UUID id);
}
