package org.PanicS_Knight.freecam_legit.client.camera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central handler for freecam state management.
 * Manages camera lifecycle, player freezing, and global state.
 *
 * @author PanicS_Knight
 * @version 1.1
 */
public final class FreecamHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("FreecamLegit/Handler");

    // Chat message formatting
    private static final String PREFIX = "§a[Freecam] §7";
    private static final String MSG_ENABLED = PREFIX + "Включён (V для выключения)";
    private static final String MSG_DISABLED = PREFIX + "§cВыключен";

    // State management
    private static boolean enabled = false;
    private static FreecamEntity cameraEntity;
    private static Perspective previousPerspective;

    // Player freeze coordinates (only horizontal position frozen, Y free for gravity)
    private static double frozenX;
    private static double frozenZ;
    private static float frozenYaw;
    private static float frozenPitch;

    private FreecamHandler() {
        throw new UnsupportedOperationException("Handler class cannot be instantiated");
    }

    /**
     * Initializes the freecam handler.
     * Should be called once during mod initialization.
     */
    public static void init() {
        LOGGER.info("FreecamHandler initialized");
    }

    /**
     * Toggles freecam on/off.
     * Thread-safe and handles null player gracefully.
     *
     * @param client minecraft client instance
     */
    public static void toggle(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            LOGGER.warn("Cannot toggle freecam: player or world is null");
            return;
        }

        enabled = !enabled;

        if (enabled) {
            enable(client);
        } else {
            disable(client);
        }
    }

    /**
     * Enables freecam mode.
     * Saves player position/rotation and creates camera entity.
     *
     * @param client minecraft client instance
     */
    private static void enable(MinecraftClient client) {
        // Freeze only horizontal position (X/Z), leave Y free for gravity
        assert client.player != null;
        frozenX = client.player.getX();
        frozenZ = client.player.getZ();
        frozenYaw = client.player.getYaw();
        frozenPitch = client.player.getPitch();

        // Create camera at player's current position
        cameraEntity = new FreecamEntity(client.player);

        // Switch to third person to see frozen player
        previousPerspective = client.options.getPerspective();
        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);

        // Send confirmation message
        client.player.sendMessage(Text.literal(MSG_ENABLED), true);

        LOGGER.info("Freecam enabled at position: X={}, Z={}", frozenX, frozenZ);
    }

    /**
     * Disables freecam mode.
     * Destroys camera entity and restores player perspective.
     *
     * @param client minecraft client instance
     */
    private static void disable(MinecraftClient client) {
        // Clean up camera entity
        cameraEntity = null;

        // Restore previous camera perspective
        if (previousPerspective != null) {
            client.options.setPerspective(previousPerspective);
            previousPerspective = null;
        }

        // Send confirmation message
        if (client.player != null) {
            client.player.sendMessage(Text.literal(MSG_DISABLED), true);
        }

        LOGGER.info("Freecam disabled");
    }

    /**
     * Ticks the freecam handler.
     * Enforces player position freeze and updates camera physics.
     * Should be called every client tick.
     *
     * @param client minecraft client instance
     */
    public static void tick(MinecraftClient client) {
        if (!enabled || cameraEntity == null || client.player == null) {
            return;
        }

        // Freeze player at saved horizontal position (allow vertical movement from gravity)
        freezePlayer(client);

        // Update camera position and physics (including collision and distance limits)
        cameraEntity.tick();
    }

    /**
     * Freezes player at saved position.
     * Only freezes horizontal (X/Z) coordinates and rotation.
     * Vertical position (Y) remains free for gravity/physics.
     *
     * @param client minecraft client instance
     */
    private static void freezePlayer(MinecraftClient client) {
        // Get current Y coordinate (affected by gravity)
        assert client.player != null;
        double currentY = client.player.getY();

        // Restore frozen X/Z, keep current Y
        client.player.setPosition(frozenX, currentY, frozenZ);

        // Cancel horizontal velocity, preserve vertical (gravity)
        Vec3d currentVelocity = client.player.getVelocity();
        client.player.setVelocity(0, currentVelocity.y, 0);

        // Freeze rotation
        client.player.setYaw(frozenYaw);
        client.player.setPitch(frozenPitch);
    }

    /**
     * Checks if freecam is currently enabled.
     *
     * @return true if freecam is active
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the current camera entity.
     *
     * @return camera entity, or null if freecam is disabled
     */
    public static FreecamEntity getCameraEntity() {
        return cameraEntity;
    }
}