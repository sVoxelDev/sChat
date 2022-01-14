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

package net.silthus.schat.platform.config.adapter;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.silthus.schat.settings.Settings;
import org.jetbrains.annotations.Nullable;

public interface ConfigurationSection {

    ConfigurationSection scoped(String path);

    <V> @Nullable V get(String path, Class<V> type);

    void set(String path, Object value);

    String getString(String path, String def);

    int getInteger(String path, int def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    List<String> getKeys(String path, List<String> def);

    Component getParsedString(String path, Component def);

    Settings getSettings(String path);
}
