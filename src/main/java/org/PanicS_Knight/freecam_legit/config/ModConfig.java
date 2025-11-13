package org.PanicS_Knight.freecam_legit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration management for Freecam Legit mod.
 * Handles loading, saving and accessing mod settings.
 *
 * @author PanicS_Knight
 * @version 1.0
 */
public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("FreecamLegit/Config");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final String CONFIG_FILE_NAME = "freecam_legit.json";

    // Default configuration values
    private static final float DEFAULT_MOVE_SPEED = 0.2f;
    private static final float DEFAULT_SPRINT_MULTIPLIER = 2.5f;
    private static final float DEFAULT_MOUSE_SENSITIVITY = 1.0f;
    private static final int DEFAULT_MAX_DISTANCE = 12;
    private static final boolean DEFAULT_SHOW_OVERLAY = true;

    // Configuration fields
    public float moveSpeed = DEFAULT_MOVE_SPEED;
    public float sprintMultiplier = DEFAULT_SPRINT_MULTIPLIER;
    public float mouseSensitivity = DEFAULT_MOUSE_SENSITIVITY;
    public int maxDistance = DEFAULT_MAX_DISTANCE;
    public boolean showOverlay = DEFAULT_SHOW_OVERLAY;

    private static ModConfig instance;

    /**
     * Gets the singleton instance of the config.
     * Lazy-loads the configuration if not already loaded.
     *
     * @return the configuration instance
     */
    public static ModConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    /**
     * Loads configuration from file or creates default if not exists.
     *
     * @return loaded or default configuration
     */
    private static ModConfig load() {
        Path configPath = getConfigPath();

        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                ModConfig config = GSON.fromJson(json, ModConfig.class);
                LOGGER.info("Configuration loaded successfully");
                return config;
            } catch (IOException e) {
                LOGGER.error("Failed to load configuration, using defaults", e);
            }
        }

        ModConfig config = new ModConfig();
        config.save();
        LOGGER.info("Created default configuration");
        return config;
    }

    /**
     * Saves current configuration to file.
     */
    public void save() {
        Path configPath = getConfigPath();

        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(this));
            LOGGER.info("Configuration saved successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to save configuration", e);
        }
    }

    /**
     * Gets the configuration file path.
     *
     * @return path to configuration file
     */
    private static Path getConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve(CONFIG_FILE_NAME);
    }

    /**
     * Validates and clamps configuration values to acceptable ranges.
     */
    public void validate() {
        moveSpeed = Math.max(0.01f, Math.min(5.0f, moveSpeed));
        sprintMultiplier = Math.max(1.0f, Math.min(10.0f, sprintMultiplier));
        mouseSensitivity = Math.max(0.1f, Math.min(3.0f, mouseSensitivity));
        maxDistance = Math.max(1, Math.min(64, maxDistance));
    }
}