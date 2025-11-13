package org.PanicS_Knight.freecam_legit.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to block player interactions when freecam is active.
 * Prevents attacking, block breaking, and item usage during freecam.
 *
 * @author PanicS_Knight
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * Blocks attack action when freecam is enabled.
     */
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (FreecamHandler.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Blocks item use action when freecam is enabled.
     */
    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if (FreecamHandler.isEnabled()) {
            ci.cancel();
        }
    }

    /**
     * Blocks item pick (middle click) when freecam is enabled.
     */
    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        if (FreecamHandler.isEnabled()) {
            ci.cancel();
        }
    }

    /**
     * Blocks block breaking when freecam is enabled.
     */
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(boolean breaking, CallbackInfo ci) {
        if (FreecamHandler.isEnabled()) {
            ci.cancel();
        }
    }
}