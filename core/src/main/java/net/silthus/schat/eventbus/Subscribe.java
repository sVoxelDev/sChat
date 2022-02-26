package net.silthus.schat.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Subscribes the event in the method parameters to the event bus.
 *
 * <p>The class containing the method must be {@link EventBus#register}</p>
 *
 * @since next
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
}
