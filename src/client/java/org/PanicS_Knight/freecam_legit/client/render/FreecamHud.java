package org.PanicS_Knight.freecam_legit.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.PanicS_Knight.freecam_legit.config.ModConfig;

/**
 * Renders freecam status overlay on the HUD.
 * Displays current distance from player and controls hint.
 *
 * @author PanicS_Knight
 * @version 1.0
 */
public final class FreecamHud {
    // UI positioning constants
    private static final int MARGIN = 10;
    private static final int LINE_HEIGHT = 12;

    // Text colors
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_GRAY = 0x888888;

    private FreecamHud() {
        throw new UnsupportedOperationException("HUD class cannot be instantiated");
    }

    /**
     * Renders the freecam HUD overlay.
     * Called every render frame when HUD is visible.
     *
     * @param context drawing context
     * @param tickCounter render tick counter (provided by Fabric API but unused)
     */
    @SuppressWarnings("unused") // tickCounter required by HudRenderCallback signature
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        // Only render if freecam is active and overlay is enabled
        if (!FreecamHandler.isEnabled() || !ModConfig.getInstance().showOverlay) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || FreecamHandler.getCameraEntity() == null) {
            return;
        }

        // Calculate distance from player to camera
        double distance = FreecamHandler.getCameraEntity()
                .getPos()
                .distanceTo(client.player.getPos());

        // Format status text
        String statusText = String.format("§e§lFREECAM §r§7(%.1fm)", distance);
        String hintText = "§7V - выключить";

        // Calculate position (top-right corner)
        int screenWidth = context.getScaledWindowWidth();
        int statusX = screenWidth - client.textRenderer.getWidth(statusText) - MARGIN;
        int hintX = screenWidth - client.textRenderer.getWidth(hintText) - MARGIN;
        int y = MARGIN;

        // Render status text
        context.drawTextWithShadow(
                client.textRenderer,
                statusText,
                statusX, y,
                COLOR_WHITE
        );

        // Render hint text below
        context.drawTextWithShadow(
                client.textRenderer,
                hintText,
                hintX, y + LINE_HEIGHT,
                COLOR_GRAY
        );
    }
}