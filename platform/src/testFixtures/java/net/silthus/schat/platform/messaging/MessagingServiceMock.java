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

package net.silthus.schat.platform.messaging;

import java.lang.reflect.Type;
import lombok.NonNull;
import net.silthus.schat.messaging.PluginMessage;
import net.silthus.schat.platform.SchedulerMock;
import net.silthus.schat.util.gson.GsonSerializer;

import static net.silthus.schat.util.gson.GsonProvider.gsonSerializer;
import static org.assertj.core.api.Assertions.assertThat;

public class MessagingServiceMock extends MessagingService {

    private PluginMessage.Type lastReceivedMessage;
    private int processedMessageCount = 0;

    public MessagingServiceMock() {
        super(new SchedulerMock(), gsonSerializer());
    }

    @Override
    public SchedulerMock getScheduler() {
        return (SchedulerMock) super.getScheduler();
    }

    public void registerGsonTypeAdapter(Type type, Object adapter) {
        if (getSerializer() instanceof GsonSerializer gsonSerializer)
            gsonSerializer.registerTypeAdapter(type, adapter);
    }

    @Override
    protected void sendOutgoingMessage(String data) {
        consumeIncomingMessageAsString(data);
    }

    @Override
    public boolean consumeIncomingMessage(@NonNull PluginMessage.Type message) {
        final boolean processed = super.consumeIncomingMessage(message);
        this.lastReceivedMessage = message;
        if (processed)
            processedMessageCount++;
        return processed;
    }

    public void assertLastReceivedMessageIs(Object message) {
        assertThat(lastReceivedMessage).isNotNull()
            .extracting(PluginMessage.Type::content).isEqualTo(message);
    }

    public void assertProcessedMessageCountIs(int count) {
        assertThat(processedMessageCount).isEqualTo(count);
    }
}
