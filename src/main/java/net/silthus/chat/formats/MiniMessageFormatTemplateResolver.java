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
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import net.silthus.chat.Formats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiniMessageFormatTemplateResolver implements TemplateResolver {

    @Override
    public boolean canResolve(@NotNull String key) {
        return Formats.containsTemplate(key);
    }

    @Override
    public @Nullable Template resolve(@NotNull String key) {
        return Formats.formatFromTemplate(key, MiniMessageFormat.class)
                .map(MiniMessageFormat::format)
                .map(result -> Template.template(key, result))
                .orElse(null);
    }
}
