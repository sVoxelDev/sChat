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

package net.silthus.schat.platform.plugin;

import java.util.HashMap;
import java.util.Map;
import net.silthus.schat.channel.Channel;
import net.silthus.schat.platform.config.ChannelConfig;
import net.silthus.schat.platform.config.ConfigKeys;
import net.silthus.schat.platform.plugin.logging.PluginLogger;
import net.silthus.schat.repository.Repository;

@SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
class ChannelLoader {

    private static final String UPDATE_SYMBOL = "\uD83D\uDDD8";
    private static final String TRASH_SYMBOL = "\uD83D\uDDD1";
    private static final String ERROR_SYMBOL = "✖";
    private static final String SUCCESS_SYMBOL = "✔";
    public static final String ARROW_SYMBOL = " ➔ ";

    private final AbstractSChatServerPlugin server;
    private final PluginLogger logger;

    private int newCount = 0;
    private int updateCount = 0;
    private int removedCount = 0;
    private int errorCount = 0;
    private Map<String, ChannelConfig> previousChannels = new HashMap<>();

    ChannelLoader(AbstractSChatServerPlugin server) {
        this.server = server;
        logger = server.logger();
    }

    void load() {
        final Map<String, ChannelConfig> configs = server.config().get(ConfigKeys.CHANNELS);
        logger.info("Loading channels from config...");

        removeOldChannels(configs);
        loadOrUpdateChannels(configs);

        printSummary();

        previousChannels = Map.copyOf(configs);
    }

    private void printSummary() {
        logger.info("... loaded (" + SUCCESS_SYMBOL + ") " + newCount + " new channel(s)\n"
            + "... updated (" + UPDATE_SYMBOL + ") " + updateCount + " channel(s)\n"
            + "... removed (" + TRASH_SYMBOL + ") " + removedCount + " channel(s)\n"
            + "... failed to update or load (" + ERROR_SYMBOL + ") " + errorCount + " channel(s)"
        );
        resetCounter();
    }

    private void resetCounter() {
        newCount = 0;
        updateCount = 0;
        removedCount = 0;
        errorCount = 0;
    }

    private void removeOldChannels(Map<String, ChannelConfig> configs) {
        for (String oldKey : previousChannels.keySet())
            if (!configs.containsKey(oldKey))
                removeChannel(oldKey);
    }

    private void loadOrUpdateChannels(Map<String, ChannelConfig> configs) {
        for (Map.Entry<String, ChannelConfig> entry : configs.entrySet())
            if (previousChannels.containsKey(entry.getKey()))
                updateChannel(entry);
            else
                loadChannelFromConfig(entry.getValue());
    }

    private void removeChannel(String oldKey) {
        final Channel channel = server.channelRepository().remove(oldKey);
        if (channel != null) {
            channel.close();
            logger.info("\t" + TRASH_SYMBOL + " " + oldKey);
            removedCount++;
        }
    }

    private void updateChannel(Map.Entry<String, ChannelConfig> entry) {
        try {
            server.channelRepository().get(entry.getKey())
                .settings(entry.getValue().settings());
            logger.info("\t" + UPDATE_SYMBOL + " " + entry.getKey());
            updateCount++;
        } catch (Repository.NotFound e) {
            errorCount++;
            logger.info("\t" + UPDATE_SYMBOL + ARROW_SYMBOL + ERROR_SYMBOL + entry.getKey() + ": " + e.getMessage());
        }
    }

    private void loadChannelFromConfig(ChannelConfig channelConfig) {
        try {
            tryLoadChannelFromConfig(channelConfig);
        } catch (Exception e) {
            errorCount++;
            logger.info("\t" + ERROR_SYMBOL + " " + channelConfig.key() + ": " + e.getMessage());
        }
    }

    private void tryLoadChannelFromConfig(ChannelConfig channelConfig) {
        final Channel channel = channelConfig.toChannel();
        server.channelRepository().add(channel);
        logger.info("\t" + SUCCESS_SYMBOL + " " + channel.key());
        newCount++;
    }
}
