package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraformersmc.vistas.title.VistasPanorama;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

    /**
     * Renders the Vistas cubemap(s) instead of the Minecraft one.
     */
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/CubeMap;render(FF)V"
            )
    )
    private void vistas$renderCubemaps(
            CubeMap instance,
            float rotXInDegrees,
            float rotYInDegrees,
            Operation<Void> original
    ) {
        if (Minecraft.getInstance().gameRenderer.panorama() instanceof VistasPanorama panorama) {
            panorama.renderCubemaps();
        }
    }
}
