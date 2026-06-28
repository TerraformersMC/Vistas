package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.LogoRendererAccessor;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
@Mixin(LogoRenderer.class)
public abstract class LogoRendererMixin implements LogoRendererAccessor {
    @Shadow
    @Final
    public static Identifier MINECRAFT_LOGO;

    @Unique
    private boolean isVistas = false;

    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IFI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIII)V",
                    ordinal = 0
            )
    )
    @SuppressWarnings("unused")
    private void vistas$render$drawOutline(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color, Operation<Void> operation, GuiGraphicsExtractor context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.get();
        LogoControl logo = panorama.getLogoControl();
        Matrix3x2fStack matrices = instance.pose();

        matrices.pushMatrix();

        matrices.translate((float) logo.getLogoX(), (float) logo.getLogoY());

        matrices.translate((float) (screenWidth / 2.0D), (float) ((y * 2.0D) - (y / 2.0D)));
        matrices.rotate((float) Math.toRadians(logo.getLogoRot()));
        matrices.translate((float) -(screenWidth / 2.0D), (float) (-(y * 2.0D) + (y / 2.0D)));

        if (!logo.getLogoId().equals(MINECRAFT_LOGO) || this.isVistas) {
            Identifier logoTexture = this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId();
            int rx = (screenWidth / 2) - 256;
            int ry = 52 - 256;

            BiConsumer<Integer, Integer> render = (ix, iy) -> instance.blit(renderPipeline, logoTexture, ix, iy, 0, 0, 0, 512, 512, 512, 512);

            if (logo.isOutlined()) {
                vistas$drawWithOutline(rx, ry, render);
            } else {
                render.accept(rx, ry);
            }

            operation.call(instance, renderPipeline, logoTexture, rx, ry, 0.0F, 0.0F, 512, 512, 512, 512, color);
        } else {
            BiConsumer<Integer, Integer> render = (ix, iy) -> instance.blit(renderPipeline, logo.getLogoId(), ix, iy, u, v, width, height, textureWidth, textureHeight);

            if (logo.isOutlined()) {
                vistas$drawWithOutline(x, y, render);
            } else {
                render.accept(x, y);
            }

            operation.call(instance, renderPipeline, logo.getLogoId(), x, y, u, v, width, height, textureWidth, textureHeight, color);
        }

        matrices.popMatrix();
    }

    @WrapOperation(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IFI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIII)V",
                    ordinal = 1
            )
    )
    @SuppressWarnings("unused")
    private void vistas$render(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color, Operation<Void> operation, GuiGraphicsExtractor context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.get();
        LogoControl logo = panorama.getLogoControl();
        Matrix3x2fStack matrices = instance.pose();

        if (!logo.doesShowEdition()) {
            return;
        }

        matrices.pushMatrix();

        matrices.translate((float) logo.getLogoX(), (float) logo.getLogoY());

        matrices.translate((float) (screenWidth / 2.0D), 45F);
        matrices.rotate((float) Math.toRadians(logo.getLogoRot()));
        matrices.translate((float) -(screenWidth / 2.0D), -45F);

        operation.call(instance, renderPipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);

        matrices.popMatrix();
    }

    @Override
    public void vistas$setIsVistas(boolean value) {
        this.isVistas = value;
    }

    @Unique
    private static void vistas$drawWithOutline(int x, int y, BiConsumer<Integer, Integer> renderAction) {
        GlStateManager._blendFuncSeparate(GlConst.GL_ZERO, GlConst.GL_ONE_MINUS_SRC_ALPHA, GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        renderAction.accept(x + 1, y);
        renderAction.accept(x - 1, y);
        renderAction.accept(x, y + 1);
        renderAction.accept(x, y - 1);
        GlStateManager._disableBlend(0);
        renderAction.accept(x, y);
    }
}
