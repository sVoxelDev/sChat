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
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.silthus.schat.AssertionHelper.assertNPE;
import static org.assertj.core.api.Assertions.assertThat;

class IdentityTests {

    private static final @NotNull TextComponent DISPLAY_NAME = text("Bob");
    private static final String NAME = "Test";

    @Test
    @SuppressWarnings("ConstantConditions")
    void given_identity_with_null_id_throws_npe() {
        assertNPE(() -> Identity.identity((UUID) null));
    }

    @Test
    void given_identity_with_id_then_identity_has_id() {
        final UUID id = randomUUID();
        final Identity identity = Identity.identity(id);
        assertThat(identity.uniqueId()).isEqualTo(id);
    }

    @Test
    void given_identity_with_same_id_then_identities_are_equal() {
        final UUID id = randomUUID();
        final Identity identity1 = Identity.identity(id);
        final Identity identity2 = Identity.identity(id, NAME);
        assertThat(identity1).isEqualTo(identity2);
        assertThat(identity1).isNotSameAs(identity2);
    }

    @Nested
    class given_nil_identity {
        private Identity identity;

        @BeforeEach
        void setUp() {
            identity = Identity.nil();
        }

        @Test
        void then_properties_match_nil_properties() {
            assertThat(identity)
                .extracting(
                    Identity::uniqueId,
                    Identity::name,
                    Identity::displayName
                ).contains(
                    Identity.NIL_IDENTITY_ID,
                    "",
                    empty()
                );
        }
    }

    @Nested
    class given_identity_with_name {
        private Identity identity;

        @BeforeEach
        void setUp() {
            identity = Identity.identity(NAME);
        }

        @Test
        void then_has_name() {
            assertThat(identity.name()).isEqualTo(NAME);
        }

        @Test
        void then_display_name_is_name() {
            assertThat(identity.displayName()).isEqualTo(text(NAME));
        }

        @Nested
        class given_display_name {
            @BeforeEach
            void setUp() {
                identity = Identity.identity(NAME, DISPLAY_NAME);
            }

            @Test
            void then_uses_custom_display_name() {
                assertThat(identity.displayName()).isEqualTo(DISPLAY_NAME);
            }
        }

        @Nested
        class given_dynamic_display_name {
            private @NotNull TextComponent displayName;

            @BeforeEach
            void setUp() {
                displayName = DISPLAY_NAME;
                identity = Identity.identity(NAME, () -> displayName);
            }

            @Test
            void then_uses_display_name() {
                assertThat(identity.displayName()).isEqualTo(DISPLAY_NAME);
            }

            @Test
            void when_display_name_changes_then_display_name_is_updated() {
                displayName = text("Bobby");
                assertThat(identity.displayName()).isEqualTo(displayName);
            }
        }
    }
}
