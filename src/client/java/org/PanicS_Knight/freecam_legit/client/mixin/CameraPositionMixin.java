package org.PanicS_Knight.freecam_legit.client.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.PanicS_Knight.freecam_legit.client.camera.FreecamHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to override camera position and rotation when freecam is active.
 * Redirects camera to use freecam entity instead of player.
 *
 * @author PanicS_Knight
 */
@Mixin(Camera.class)
public abstract class CameraPositionMixin {

    @Unique
    private static final double CAMERA_EYE_OFFSET = 1.62;

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    /**
     * Intercepts camera update to apply freecam position and rotation.
     * Uses interpolated position for smooth movement between ticks.
     */
    @Inject(method = "update", at = @At("TAIL"))
    private void onCameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson,
                                boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!FreecamHandler.isEnabled() || FreecamHandler.getCameraEntity() == null) {
            return;
        }

        var camera = FreecamHandler.getCameraEntity();

        // Get interpolated position for smooth rendering
        Vec3d pos = camera.getPos(tickDelta);

        // Apply camera position with eye offset
        setPos(pos.x, pos.y + CAMERA_EYE_OFFSET, pos.z);

        // Apply camera rotation
        setRotation(camera.getYaw(), camera.getPitch());
    }
}