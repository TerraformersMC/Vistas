package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.title.VistasPanorama;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Panorama;
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
    @Mutable
    @Final
    protected Panorama panorama;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vistas$useOurRenderer(CallbackInfo ci) {
        panorama = new VistasPanorama();
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void vistas$closeOurRenderer(CallbackInfo ci) {
        if (panorama instanceof VistasPanorama renderer) {
            renderer.close();
        }
    }
}
