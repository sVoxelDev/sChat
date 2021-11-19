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

package net.silthus.chat.formats;

import net.kyori.adventure.text.minimessage.Template;
import net.silthus.chat.Formats;
import net.silthus.chat.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiniMessageFormatTemplateResolverTest extends TestBase {

    private MiniMessageFormatTemplateResolver resolver;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        resolver = new MiniMessageFormatTemplateResolver();
        Formats.registerFormatTemplate("sender", MiniMessageFormat.class, miniMessageFormat -> miniMessageFormat.format("<yellow><sender_name>"));
    }

    @Test
    void resolvesSenderTemplate() {
        assertThat(resolver.canResolve("sender")).isTrue();
        assertThat(resolver.resolve("sender"))
                .isNotNull()
                .extracting(Template::value)
                .isEqualTo("<yellow><sender_name>");
    }
}