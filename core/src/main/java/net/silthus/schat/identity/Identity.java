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
