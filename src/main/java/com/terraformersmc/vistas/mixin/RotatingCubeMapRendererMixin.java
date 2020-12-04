package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.api.panorama.Panoramas;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.terraformersmc.vistas.api.panorama.Panorama;

@Environment(EnvType.CLIENT)
@Mixin(RotatingCubeMapRenderer.class)
public abstract class RotatingCubeMapRendererMixin {

    @Shadow
    private float time;

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V"))
    public void VISTAS_changeSpeed(Args args, float delta, float alpha) {
        Panorama pan = Panoramas.getCurrent();
        boolean frozen = pan.getMovementSettings().isFrozen();
        float addedX = pan.getMovementSettings().getAddedX();
        float addedY = pan.getMovementSettings().getAddedY();
        boolean woozy = pan.getMovementSettings().isWoozy();
        float speedMultiplier = pan.getMovementSettings().getSpeedMultiplier();
        args.set(1, frozen ? addedX : (float) (woozy ? this.time * 0.1F * speedMultiplier : args.get(1)) + addedX);
        args.set(2, frozen ? addedY : ((float) args.get(2) * speedMultiplier) + addedY);
    }

}
