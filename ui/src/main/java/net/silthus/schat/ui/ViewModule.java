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
package net.silthus.schat.ui;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.silthus.schat.channel.PrivateChannel;
import net.silthus.schat.eventbus.EventBus;
import net.silthus.schat.eventbus.Subscribe;
import net.silthus.schat.events.chatter.ChatterJoinedServerEvent;
import net.silthus.schat.ui.format.Format;
import net.silthus.schat.ui.format.MiniMessageFormat;
import net.silthus.schat.ui.placeholder.Replacements;
import net.silthus.schat.ui.view.ViewConfig;
import net.silthus.schat.ui.view.ViewFactory;
import net.silthus.schat.ui.view.ViewProvider;
import net.silthus.schat.ui.views.Views;
import net.silthus.schat.ui.views.tabbed.TabbedChannelsView;
import net.silthus.schat.util.gson.GsonProvider;

import static net.silthus.schat.ui.view.ViewConfig.FORMAT_CONFIG;
import static net.silthus.schat.ui.view.ViewProvider.cachingViewProvider;

@Getter
@Accessors(fluent = true)
public class ViewModule {

    private final ViewConfig config;
    private final EventBus eventBus;
    private final ViewFactory viewFactory;
    private final GsonProvider gsonProvider;
    private final ViewProvider viewProvider;
    private final Replacements replacements = new Replacements();

    public ViewModule(ViewConfig config, EventBus eventBus, GsonProvider gsonProvider) {
        this.config = config;
        this.eventBus = eventBus;
        this.viewFactory = chatter -> {
            final TabbedChannelsView view = Views.tabbedChannels(chatter, config());
            eventBus.register(view);
            return view;
        };
        this.gsonProvider = gsonProvider;
        this.viewProvider = cachingViewProvider(viewFactory());

        init();
    }

    public void init() {
        gsonProvider.builder().registerTypeHierarchyAdapter(Format.class, new MiniMessageFormatSerializer());
        eventBus().register(this);
        eventBus().register(replacements);
        configurePrivateChats();
    }

    @Subscribe
    private void onChatterJoin(ChatterJoinedServerEvent event) {
        viewProvider.view(event.chatter()).update();
    }

    private void configurePrivateChats() {
        PrivateChannel.configure(builder -> builder.set(FORMAT_CONFIG, config().privateChatFormat()));
    }

    private static final class MiniMessageFormatSerializer implements JsonSerializer<Format>, JsonDeserializer<Format> {

        @Override
        public JsonElement serialize(Format src, Type typeOfSrc, JsonSerializationContext context) {
            if (src instanceof MiniMessageFormat format)
                return new JsonPrimitive(format.format());
            else
                return JsonNull.INSTANCE;
        }

        @Override
        public Format deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull())
                return null;
            else
                return new MiniMessageFormat(json.getAsString());
        }
    }
}
