package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.mixin.accessor.PanoramaRendererAccessor;
import com.terraformersmc.vistas.title.VistasPanoramaRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
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
    protected PanoramaRenderer panorama;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void vistas$useOurRenderer(CallbackInfo ci) {
        panorama = new VistasPanoramaRenderer(
                ((PanoramaRendererAccessor)panorama).vistas$getCubeMap()
        );
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void vistas$closeOurRenderer(CallbackInfo ci) {
        if (panorama instanceof VistasPanoramaRenderer renderer) {
            renderer.close();
        }
    }
}
