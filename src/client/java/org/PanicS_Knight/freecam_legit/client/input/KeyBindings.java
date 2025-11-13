package org.PanicS_Knight.freecam_legit.client.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.lwjgl.glfw.GLFW;

/**
 * Manages client-side keybindings for freecam controls.
 *
 * @author PanicS_Knight
 * @version 1.0
 */
public final class KeyBindings {
    private static final String CATEGORY = "key.category.freecam_legit";
    private static final String TOGGLE_KEY = "key.freecam_legit.toggle";

    /**
     * Keybinding to toggle freecam on/off.
     * Default: V key
     */
    public static KeyBinding TOGGLE_FREECAM;

    private KeyBindings() {
        throw new UnsupportedOperationException("Keybindings class cannot be instantiated");
    }

    /**
     * Registers all keybindings.
     * Should be called once during mod initialization.
     */
    public static void register() {
        TOGGLE_FREECAM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                TOGGLE_KEY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CATEGORY
        ));
    }

    /**
     * Handles keybinding input.
     * Should be called every client tick.
     *
     * @param client minecraft client instance
     */
    public static void handleInput(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        // Process toggle keybinding
        while (TOGGLE_FREECAM.wasPressed()) {
            FreecamHandler.toggle(client);
        }
    }
}