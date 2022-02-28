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
package net.silthus.schat.util.gson;

import com.google.gson.Gson;
import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.identity.Identity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.assertj.core.api.Assertions.assertThat;

class IdentitySerializerTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonProvider.gsonProvider().prettyGson();
    }

    @Test
    void serialize_stores_identity_properties() {
        final Identity identity = Identity.identity("Bob", text("Bobby"));
        final String json = gson.toJson(identity);
        assertThat(json).isEqualTo("""
            {
              "id": "%s",
              "name": "Bob",
              "display_name": {
                "text": "Bobby"
              }
            }""".formatted(identity.uniqueId().toString()));
    }

    @Test
    void serializes_chatter_as_chatter() {
        final Chatter chatter = Chatter.chatter(Identity.identity("Bob"));
        final String json = gson.toJson(chatter);
        assertThat(json).isEqualTo("""
            {
              "identity": {
                "id": "%s",
                "name": "Bob",
                "display_name": {
                  "text": "Bob"
                }
              },
              "channels": []
            }""".formatted(chatter.uniqueId().toString()));
    }

    @Test
    void deserializes_identity() {
        final Identity origin = Identity.identity("Bob", text("Bobby"));
        final String json = gson.toJson(origin);
        final Identity identity = gson.fromJson(json, Identity.class);
        assertThat(identity).extracting(
            Identity::uniqueId,
            Identity::name,
            Identity::displayName
        ).contains(
            origin.uniqueId(),
            origin.name(),
            origin.displayName()
        );
    }
}
