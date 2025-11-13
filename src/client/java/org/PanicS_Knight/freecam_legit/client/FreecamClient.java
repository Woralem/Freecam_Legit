package org.PanicS_Knight.freecam_legit.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.PanicS_Knight.freecam_legit.client.input.KeyBindings;
import org.PanicS_Knight.freecam_legit.client.render.FreecamHud;
import org.PanicS_Knight.freecam_legit.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main client-side entry point for Freecam Legit mod.
 * Initializes all subsystems and registers event handlers.
 *
 * @author PanicS_Knight
 * @version 1.0
 */
public class FreecamClient implements ClientModInitializer {
    @SuppressWarnings("unused") // Used for identification in logs and future features
    public static final String MOD_ID = "freecam_legit";

    public static final String MOD_NAME = "Freecam Legit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {} mod...", MOD_NAME);

        // Load configuration
        ModConfig config = ModConfig.getInstance();
        config.validate();
        LOGGER.info("Configuration loaded: moveSpeed={}, maxDistance={}",
                config.moveSpeed, config.maxDistance);

        // Register keybindings
        KeyBindings.register();
        LOGGER.info("Keybindings registered");

        // Initialize freecam handler
        FreecamHandler.init();

        // Register tick event handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBindings.handleInput(client);
            FreecamHandler.tick(client);
        });
        LOGGER.info("Tick event handlers registered");

        // Register HUD renderer
        @SuppressWarnings("deprecation")
        var hudCallback = HudRenderCallback.EVENT;
        hudCallback.register(FreecamHud::render);
        LOGGER.info("HUD renderer registered");

        LOGGER.info("{} initialized successfully!", MOD_NAME);
    }
}