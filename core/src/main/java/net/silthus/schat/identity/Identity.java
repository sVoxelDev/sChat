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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

/**
 * Holds the id, name and display name of an identified entity.
 *
 * <p>Identities could be players, channels, the console and so on.
 * If the identity is a player the id of the player in the platform matches the identity's id.</p>
 *
 * @since next
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"id"})
public final class Identity {

    private static final Identity NIL = Identity.identity("", Component.empty());

    private final @NonNull UUID id;
    private final @NonNull String name;
    private final @NonNull Supplier<Component> displayName;

    private Identity(
        @NonNull UUID id,
        @NonNull String name,
        @NonNull Supplier<Component> displayName
    ) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }

    /**
     * Gets a {@code nil} identity.
     *
     * <p>The identity will have an empty {@link #name()} and {@link #displayName()},
     * but no properties will actual be {@code null}.</p>
     *
     * <p>The {@link #id()} of the nil identity will always be the same during the lifetime
     * of the application, but changes for every lifecycle.</p>
     *
     * @return the nil identity
     * @since next
     */
    public static @NotNull Identity nil() {
        return NIL;
    }

    /**
     * Creates a new identity using the provided id and an empty name.
     *
     * @param id the id of the entity
     * @return the identity
     * @since next
     */
    public static Identity identity(final UUID id) {
        return identity(id, "");
    }

    /**
     * Creates a new identity using the provided id and name.
     *
     * <p>The {@link #displayName()} will be the {@code name}.</p>
     *
     * @param id   the id
     * @param name the name
     * @return the identity
     * @since next
     */
    public static @NotNull Identity identity(@NonNull UUID id, @NonNull String name) {
        return identity(id, name, text(name));
    }

    /**
     * Creates a new identity using the provided name.
     *
     * <p>A random {@code UUID} will be used for the id
     * and the {@link #displayName()} will be the {@code name}.</p>
     *
     * @param name the name
     * @return the identity
     * @since next
     */
    public static @NotNull Identity identity(@NonNull String name) {
        return identity(UUID.randomUUID(), name);
    }

    /**
     * Creates a new identity using the given name and display name.
     *
     * <p>A random {@code UUID} will be used for the id.</p>
     *
     * @param name        the name
     * @param displayName the display name
     * @return the identity
     * @since next
     */
    public static @NotNull Identity identity(@NonNull String name, @NonNull Component displayName) {
        return identity(UUID.randomUUID(), name, displayName);
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
    public static Identity identity(final UUID id, final String name, final Component displayName) {
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
    public static Identity identity(final UUID id, final String name, final Supplier<Component> displayName) {
        return new Identity(id, name, displayName);
    }

    /**
     * Gets the display of the identity.
     *
     * @return the display name
     * @since next
     */
    public Component displayName() {
        return displayName.get();
    }
}
