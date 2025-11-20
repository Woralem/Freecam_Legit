package org.PanicS_Knight.freecam_legit.client.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to disable keyboard input processing when freecam is active.
 * Overrides player input state immediately after it is updated by the game.
 * This prevents the physical player from jumping or moving while controlling the freecam.
 *
 * @author PanicS_Knight
 */
@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    /**
     * Resets player input state at the end of the tick.
     * Forces all input flags (jump, sneak, movement) to false when freecam is enabled.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (FreecamHandler.isEnabled()) {
            // Reset all input flags to false (forward, backward, left, right, jump, sneak, sprint)
            this.playerInput = new PlayerInput(false, false, false, false, false, false, false);

            // Reset movement vector to zero to prevent any motion
            this.movementVector = Vec2f.ZERO;
        }
    }
}