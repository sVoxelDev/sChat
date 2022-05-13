package net.silthus.schat.util;

import java.util.UUID;
import net.silthus.schat.pointer.Pointer;

/**
 * Represents a typical Minecraft location object.
 *
 * <p>Use the helper inside the implementing platform to wrap and unwrap native location objects.</p>
 *
 * @param world the world of the location
 * @param x x-axis
 * @param y y-axis
 * @param z z-axis
 * @param yaw horizontal rotation
 * @param pitch vertical rotation
 * @since next
 */
public record Location(World world, double x, double y, double z, float yaw, float pitch) {

    public static final Pointer<Location> LOCATION = Pointer.pointer(Location.class, "location");

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0f, 90f);
    }

    /**
     * Represents a typical Minecraft world identified by an id and name.
     *
     * @param id the unique id of the world
     * @param name the name of the world
     * @since next
     */
    public record World(UUID id, String name) {
    }
}
