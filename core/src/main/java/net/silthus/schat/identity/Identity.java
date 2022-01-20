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

package net.silthus.schat.identity;

import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.silthus.schat.pointer.Pointer;
import net.silthus.schat.pointer.Pointered;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.pointer.Pointer.pointer;
import static net.silthus.schat.pointer.Pointers.pointers;

public sealed interface Identity extends Pointered permits IdentityImpl {

    UUID NIL_IDENTITY_ID = new UUID(0, 0); // 00000000-0000-0000-0000-000000000000

    Pointer<UUID> ID = pointer(UUID.class, "id");
    Pointer<String> NAME = pointer(String.class, "name");
    Pointer<Component> DISPLAY_NAME = pointer(Component.class, "display_name");

    /**
     * Gets a {@code nil} identity.
     *
     * <p>The identity will have an empty {@link #getName()} and {@link #getDisplayName()},
     * but no properties will actual be {@code null}.</p>
     *
     * <p>The {@link #getUniqueId()} of the nil identity will always be the same during the lifetime
     * of the application, but changes for every lifecycle.</p>
     *
     * @return the nil identity
     * @since next
     */
    static @NotNull Identity nil() {
        return IdentityImpl.NIL;
    }

    /**
     * Creates a new identity using the provided id and name.
     *
     * <p>The {@link #getDisplayName()} will be the {@code name}.</p>
     *
     * @param id   the id
     * @param name the name
     * @return the identity
     * @since next
     */
    static @NotNull Identity identity(@NonNull UUID id, @NonNull String name) {
        return identity(id, name, text(name));
    }

    /**
     * Creates a new identity using the provided name.
     *
     * <p>A random {@code UUID} will be used for the id
     * and the {@link #getDisplayName()} will be the {@code name}.</p>
     *
     * @param name the name
     * @return the identity
     * @since next
     */
    static @NotNull Identity identity(@NonNull String name) {
        return identity(UUID.randomUUID(), name);
    }

    /**
     * Creates a new identity using the provided values.
     *
     * @param id          the id
     * @param name        the name
     * @param displayName the display name
     * @return the identity
     * @since next
     */
    static Identity identity(final UUID id, final String name, final Component displayName) {
        return identity(id, name, () -> displayName);
    }

    /**
     * Creates a new identity using the provided values and a dynamic supplier for the display name.
     *
     * @param id          the id
     * @param name        the name
     * @param displayName the display name supplier
     * @return the identity
     * @since next
     */
    static Identity identity(final UUID id, final String name, final Supplier<Component> displayName) {
        return new IdentityImpl(id, pointers()
            .withStatic(ID, id)
            .withStatic(NAME, name)
            .withDynamic(DISPLAY_NAME, displayName)
            .create()
        );
    }

    UUID getUniqueId();

    default String getName() {
        return getOrDefault(NAME, "");
    }

    default Component getDisplayName() {
        return getOrDefault(DISPLAY_NAME, empty());
    }
}
