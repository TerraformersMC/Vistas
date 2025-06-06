package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.title.VistasRotatingCubemapRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    protected CubeMapRenderer panoramaRenderer;

    @Shadow
    @Mutable
    @Final
    protected RotatingCubeMapRenderer rotatingPanoramaRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vistas$useOurRenderer(CallbackInfo ci) {
        rotatingPanoramaRenderer = new VistasRotatingCubemapRenderer(panoramaRenderer);
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void vistas$closeOurRenderer(CallbackInfo ci) {
        if (rotatingPanoramaRenderer instanceof VistasRotatingCubemapRenderer renderer) {
            renderer.close();
        }
    }
}
