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

package net.silthus.schat.platform.plugin.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaPluginLogger implements PluginLogger {

    private final Logger logger;

    public JavaPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String s) {
        this.logger.info(s);
    }

    @Override
    public void warn(String s) {
        this.logger.warning(s);
    }

    @Override
    public void warn(String s, Throwable t) {
        this.logger.log(Level.WARNING, s, t);
    }

    @Override
    public void severe(String s) {
        this.logger.severe(s);
    }

    @Override
    public void severe(String s, Throwable t) {
        this.logger.log(Level.SEVERE, s, t);
    }
}
