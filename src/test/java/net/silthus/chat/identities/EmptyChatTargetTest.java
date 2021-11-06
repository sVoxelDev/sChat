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

package net.silthus.chat.identities;

import net.silthus.chat.ChatTarget;
import org.junit.jupiter.api.Test;

import static net.silthus.chat.Constants.Targets.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

class EmptyChatTargetTest {

    @Test
    void create() {
        ChatTarget target = ChatTarget.nil();
        assertThat(target)
                .extracting(ChatTarget::getName)
                .isEqualTo(EMPTY);
    }
}