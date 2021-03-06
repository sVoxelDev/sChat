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
package net.silthus.schat.bukkit;

import java.io.File;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public final class BukkitLoader extends JavaPlugin {
    private static final int BSTATS_ID = 13304;

    private final BukkitBootstrap bootstrap;

    public BukkitLoader() {
        this.bootstrap = new BukkitBootstrap(this);
    }

    // testing constructor
    public BukkitLoader(@NotNull JavaPluginLoader loader,
                        @NotNull PluginDescriptionFile description,
                        @NotNull File dataFolder,
                        @NotNull File file) {
        super(loader, description, dataFolder, file);
        this.bootstrap = new BukkitBootstrap(this);
    }

    @Override
    public void onLoad() {
        bootstrap.onLoad();
    }

    @Override
    public void onEnable() {
        enableBStats();
        bootstrap.onEnable();
    }

    @Override
    public void onDisable() {
        bootstrap.onDisable();
    }

    private void enableBStats() {
        new Metrics(this, BSTATS_ID);
    }
}
