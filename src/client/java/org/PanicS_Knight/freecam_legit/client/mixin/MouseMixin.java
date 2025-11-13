package org.PanicS_Knight.freecam_legit.client.mixin;

import net.minecraft.client.Mouse;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to redirect mouse input to freecam when active.
 * Prevents player rotation and controls freecam camera instead.
 *
 * @author PanicS_Knight
 */
@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    /**
     * Intercepts cursor position updates to control freecam rotation.
     * Consumes mouse input and applies it to camera instead of player.
     */
    @Inject(method = "onCursorPos", at = @At("TAIL"))
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        if (!FreecamHandler.isEnabled() || FreecamHandler.getCameraEntity() == null) {
            return;
        }

        // Apply mouse delta to freecam camera
        FreecamHandler.getCameraEntity().changeLookDirection(
                this.cursorDeltaX,
                this.cursorDeltaY
        );

        // Clear delta to prevent player rotation
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
    }
}