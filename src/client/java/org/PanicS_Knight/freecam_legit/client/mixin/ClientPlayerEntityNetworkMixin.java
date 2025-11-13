package org.PanicS_Knight.freecam_legit.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to prevent sending player movement packets to server when freecam is active.
 * This ensures player appears frozen to other players and the server.
 *
 * @author PanicS_Knight
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityNetworkMixin {

    /**
     * Blocks sending movement packets to server when freecam is enabled.
     * Prevents server-side player position/rotation updates.
     */
    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPackets(CallbackInfo ci) {
        if (FreecamHandler.isEnabled()) {
            // Cancel packet sending - player appears frozen on server
            ci.cancel();
        }
    }
}