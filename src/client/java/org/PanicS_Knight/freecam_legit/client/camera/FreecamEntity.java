package org.PanicS_Knight.freecam_legit.client.camera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.PanicS_Knight.freecam_legit.config.ModConfig;

/**
 * Represents the freecam virtual camera entity.
 * Handles camera movement, rotation, physics, and collision detection.
 *
 * @author PanicS_Knight
 * @version 1.2
 */
public class FreecamEntity {
    // Camera dimensions (similar to player)
    private static final double CAMERA_WIDTH = 0.6;
    private static final double CAMERA_HEIGHT = 1.8;

    // Movement constants
    private static final float ACCELERATION_FACTOR = 0.5f;
    private static final double MIN_VELOCITY_THRESHOLD = 0.00001;

    // Mouse sensitivity constants
    private static final double MOUSE_SENSITIVITY_BASE = 0.6;
    private static final double MOUSE_SENSITIVITY_OFFSET = 0.2;
    private static final float MOUSE_MULTIPLIER = 8.0f;
    private static final float MOUSE_SMOOTHING = 0.15f;

    // Pitch limits (degrees)
    private static final float MAX_PITCH = 90.0f;
    private static final float MIN_PITCH = -90.0f;

    private final MinecraftClient client;

    // Position and previous position for interpolation
    private Vec3d position;
    private Vec3d prevPosition;

    // Rotation (in degrees)
    private float yaw;
    private float pitch;

    // Current velocity vector
    private Vec3d velocity;

    /**
     * Creates a new freecam entity at the player's current position and rotation.
     *
     * @param player the client player to initialize from
     */
    public FreecamEntity(ClientPlayerEntity player) {
        this.client = MinecraftClient.getInstance();
        this.position = player.getEntityPos();
        this.prevPosition = position;
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
        this.velocity = Vec3d.ZERO;
    }

    /**
     * Updates camera rotation based on mouse movement.
     * Applies sensitivity settings and clamps pitch to valid range.
     *
     * @param cursorDeltaX horizontal mouse movement
     * @param cursorDeltaY vertical mouse movement
     */
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        // Calculate sensitivity with cubic scaling (like vanilla Minecraft)
        float sensitivity = (float) (client.options.getMouseSensitivity().getValue()
                * MOUSE_SENSITIVITY_BASE + MOUSE_SENSITIVITY_OFFSET);
        float multiplier = sensitivity * sensitivity * sensitivity * MOUSE_MULTIPLIER;

        // Apply sensitivity and smoothing
        double adjustedX = cursorDeltaX * multiplier;
        double adjustedY = cursorDeltaY * multiplier;

        this.yaw += (float) adjustedX * MOUSE_SMOOTHING;
        this.pitch += (float) adjustedY * MOUSE_SMOOTHING;

        // Clamp pitch to prevent camera flip
        this.pitch = MathHelper.clamp(this.pitch, MIN_PITCH, MAX_PITCH);
    }

    /**
     * Ticks the camera entity.
     * Updates position based on input, applies block collisions, and enforces distance limits.
     */
    public void tick() {
        if (client.player == null) {
            return;
        }

        // Store previous position for smooth interpolation
        this.prevPosition = this.position;

        // Calculate target velocity from input
        Vec3d targetVelocity = calculateTargetVelocity();

        // Smoothly accelerate/decelerate towards target velocity
        this.velocity = velocity.lerp(targetVelocity, ACCELERATION_FACTOR);

        // 1. Apply movement with standard block collisions
        applyMovementWithCollisions();

        // 2. Apply "Sphere Collision" (Max distance constraint)
        applyDistanceConstraint();
    }

    /**
     * Calculates target velocity based on current input state.
     *
     * @return target velocity vector
     */
    private Vec3d calculateTargetVelocity() {
        ModConfig config = ModConfig.getInstance();

        // Gather movement input
        float forward = getForwardInput();
        float strafe = getStrafeInput();
        float vertical = getVerticalInput();

        // Calculate movement speed with sprint modifier
        float speed = config.moveSpeed;
        if (client.options.sprintKey.isPressed()) {
            speed *= config.sprintMultiplier;
        }

        // If no input, return zero velocity (smooth stop)
        if (forward == 0 && strafe == 0 && vertical == 0) {
            return Vec3d.ZERO;
        }

        // Calculate movement vectors based on camera rotation
        Vec3d horizontalMotion = calculateHorizontalMotion(forward, strafe, speed);

        // Add vertical component (pure up/down, not affected by pitch)
        return horizontalMotion.add(0, vertical * speed, 0);
    }

    /**
     * Gets normalized forward/backward input.
     *
     * @return forward input (-1, 0, or 1)
     */
    private float getForwardInput() {
        float forward = 0;
        if (client.options.forwardKey.isPressed()) forward += 1;
        if (client.options.backKey.isPressed()) forward -= 1;
        return forward;
    }

    /**
     * Gets normalized left/right strafe input.
     *
     * @return strafe input (-1, 0, or 1)
     */
    private float getStrafeInput() {
        float strafe = 0;
        if (client.options.leftKey.isPressed()) strafe += 1;
        if (client.options.rightKey.isPressed()) strafe -= 1;
        return strafe;
    }

    /**
     * Gets normalized vertical input.
     *
     * @return vertical input (-1, 0, or 1)
     */
    private float getVerticalInput() {
        float vertical = 0;
        if (client.options.jumpKey.isPressed()) vertical += 1;
        if (client.options.sneakKey.isPressed()) vertical -= 1;
        return vertical;
    }

    /**
     * Calculates horizontal motion vector based on look direction.
     * Uses "Creative-style" flight logic (ignores pitch for WASD movement).
     *
     * @param forward forward input value
     * @param strafe strafe input value
     * @param speed movement speed
     * @return horizontal motion vector (Y component is always 0)
     */
    private Vec3d calculateHorizontalMotion(float forward, float strafe, float speed) {
        // Normalize diagonal movement to prevent faster movement
        float horizontalMagnitude = (float) Math.sqrt(forward * forward + strafe * strafe);
        if (horizontalMagnitude > 0) {
            forward /= horizontalMagnitude;
            strafe /= horizontalMagnitude;
        }

        // Convert rotation to radians
        float yawRad = (float) Math.toRadians(this.yaw);

        // Creative Flight Logic:
        Vec3d forwardDir = new Vec3d(
                -Math.sin(yawRad),
                0,
                Math.cos(yawRad)
        );

        // Calculate Right direction (Perpendicular to forward)
        Vec3d rightDir = new Vec3d(
                Math.cos(yawRad),
                0,
                Math.sin(yawRad)
        );

        // Combine forward and strafe movement
        Vec3d motion = forwardDir.multiply(forward).add(rightDir.multiply(strafe));

        return motion.multiply(speed);
    }

    /**
     * Applies current velocity to position with collision detection.
     * Prevents camera from moving through solid blocks.
     */
    private void applyMovementWithCollisions() {
        if (velocity.lengthSquared() < MIN_VELOCITY_THRESHOLD) {
            return;
        }

        // Create bounding box for collision detection
        Box boundingBox = new Box(
                position.x - CAMERA_WIDTH / 2, position.y, position.z - CAMERA_WIDTH / 2,
                position.x + CAMERA_WIDTH / 2, position.y + CAMERA_HEIGHT, position.z + CAMERA_WIDTH / 2
        );

        // Move with collision checking and update position directly
        this.position = CollisionHelper.moveWithCollision(
                position,
                velocity,
                boundingBox,
                client.world
        );
    }

    /**
     * Limits the camera distance from the player using "Sphere Collision" logic.
     * Acts as a solid spherical barrier: clamps position and cancels outward velocity.
     * This prevents the camera from snapping back violently.
     */
    private void applyDistanceConstraint() {
        if (client.player == null) return;

        Vec3d anchor = client.player.getEntityPos();
        Vec3d offset = this.position.subtract(anchor);
        double distance = offset.length();
        double maxDist = ModConfig.getInstance().maxDistance;

        // If we are outside or exactly at the boundary
        if (distance > maxDist) {
            // 1. Clamp Position: Place camera exactly on the sphere surface
            Vec3d direction = offset.normalize();
            this.position = anchor.add(direction.multiply(maxDist));

            // 2. Project Velocity: "Slide" along the wall
            // We remove the component of velocity that pushes us OUT of the sphere.
            // Formula: V_new = V_old - (V_old . Normal) * Normal
            double dotProduct = this.velocity.dotProduct(direction);

            // Only modify velocity if we are actually moving AWAY (dotProduct > 0)
            if (dotProduct > 0) {
                Vec3d outwardComponent = direction.multiply(dotProduct);
                this.velocity = this.velocity.subtract(outwardComponent);
            }
        }
    }

    /**
     * Gets interpolated position for smooth rendering between ticks.
     *
     * @param tickDelta partial tick time (0.0 to 1.0)
     * @return interpolated position
     */
    public Vec3d getPos(float tickDelta) {
        return new Vec3d(
                MathHelper.lerp(tickDelta, prevPosition.x, position.x),
                MathHelper.lerp(tickDelta, prevPosition.y, position.y),
                MathHelper.lerp(tickDelta, prevPosition.z, position.z)
        );
    }

    /**
     * Gets current camera position (non-interpolated).
     *
     * @return current position
     */
    public Vec3d getPos() {
        return position;
    }

    /**
     * Gets current yaw rotation.
     *
     * @return yaw in degrees
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Gets current pitch rotation.
     *
     * @return pitch in degrees
     */
    public float getPitch() {
        return pitch;
    }
}