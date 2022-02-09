package net.silthus.schat.events.chatter;

import net.silthus.schat.chatter.Chatter;
import net.silthus.schat.events.SChatEvent;

/**
 * The {@code ChatterJoinedServerEvent} is fired when a chatter has joined the server.
 */
public record ChatterJoinedServerEvent(Chatter chatter) implements SChatEvent {
}
