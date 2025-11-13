package org.PanicS_Knight.freecam_legit.client.camera;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Utility class for handling collision detection in freecam movement.
 * Ensures camera cannot pass through solid blocks.
 *
 * @author PanicS_Knight
 * @version 1.0
 */
public final class CollisionHelper {

    private static final double MIN_MOVEMENT_THRESHOLD = 0.0001;

    private CollisionHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Moves position along motion vector while checking for collisions.
     * Tests each axis (X, Y, Z) independently to allow sliding along walls.
     *
     * @param currentPos starting position
     * @param motion desired movement vector
     * @param boundingBox collision bounding box
     * @param world world to check collisions in
     * @return final position after collision resolution
     */
    public static Vec3d moveWithCollision(Vec3d currentPos, Vec3d motion, Box boundingBox, World world) {
        // Skip if movement is negligible
        if (motion.lengthSquared() < MIN_MOVEMENT_THRESHOLD) {
            return currentPos;
        }

        // Try moving along X axis
        currentPos = tryMoveAxis(currentPos, motion.x, 0, 0, boundingBox, world);
        boundingBox = boundingBox.offset(motion.x, 0, 0);

        // Try moving along Y axis
        currentPos = tryMoveAxis(currentPos, 0, motion.y, 0, boundingBox, world);
        boundingBox = boundingBox.offset(0, motion.y, 0);

        // Try moving along Z axis
        currentPos = tryMoveAxis(currentPos, 0, 0, motion.z, boundingBox, world);

        return currentPos;
    }

    /**
     * Attempts to move along a single axis if space is empty.
     *
     * @param pos current position
     * @param dx movement along X axis
     * @param dy movement along Y axis
     * @param dz movement along Z axis
     * @param boundingBox collision bounding box
     * @param world world to check
     * @return new position (moved if possible, unchanged if blocked)
     */
    private static Vec3d tryMoveAxis(Vec3d pos, double dx, double dy, double dz, Box boundingBox, World world) {
        Box testBox = boundingBox.offset(dx, dy, dz);

        // Check if target space is empty (no collision)
        if (world.isSpaceEmpty(testBox)) {
            return pos.add(dx, dy, dz);
        }

        // Blocked, return original position
        return pos;
    }
}