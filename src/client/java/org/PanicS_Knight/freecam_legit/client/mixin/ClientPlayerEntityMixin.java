package org.PanicS_Knight.freecam_legit.client.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to block player movement input when freecam is active.
 * Prevents player from moving while controlling freecam.
 *
 * @author PanicS_Knight
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Shadow
    public Input input;

    /**
     * Blocks all player movement input when freecam is enabled.
     * Injects at the start of tickMovement to prevent input processing.
     */
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        if (!FreecamHandler.isEnabled()) {
            return;
        }

        // Clear all movement input (WASD, jump, sneak, sprint)
        input.playerInput = new PlayerInput(
                false, // forward
                false, // backward
                false, // left
                false, // right
                false, // jump
                false, // sneak
                false  // sprint
        );
    }
}