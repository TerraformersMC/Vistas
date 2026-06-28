package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.LogoDrawerAccessor;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
@Mixin(LogoDrawer.class)
public abstract class LogoDrawerMixin implements LogoDrawerAccessor {
    @Shadow
    @Final
    public static Identifier LOGO_TEXTURE;

    @Unique
    private boolean isVistas = false;

    @WrapOperation(
            method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIII)V",
                    ordinal = 0
            )
    )
    @SuppressWarnings("unused")
    private void vistas$render$drawOutline(DrawContext instance, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color, Operation<Void> operation, DrawContext context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.get();
        LogoControl logo = panorama.getLogoControl();
        Matrix3x2fStack matrices = instance.getMatrices();

        matrices.pushMatrix();

        matrices.translate((float) logo.getLogoX(), (float) logo.getLogoY());

        matrices.translate((float) (screenWidth / 2.0D), (float) ((y * 2.0D) - (y / 2.0D)));
        matrices.rotate((float) Math.toRadians(logo.getLogoRot()));
        matrices.translate((float) -(screenWidth / 2.0D), (float) (-(y * 2.0D) + (y / 2.0D)));

        if (!logo.getLogoId().equals(LOGO_TEXTURE) || this.isVistas) {
            Identifier logoTexture = this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId();
            int rx = (screenWidth / 2) - 256;
            int ry = 52 - 256;

            BiConsumer<Integer, Integer> render = (ix, iy) -> instance.drawTexture(renderPipeline, logoTexture, ix, iy, 0, 0, 0, 512, 512, 512, 512);

            if (logo.isOutlined()) {
                vistas$drawWithOutline(rx, ry, render);
            } else {
                render.accept(rx, ry);
            }

            operation.call(instance, renderPipeline, logoTexture, rx, ry, 0.0F, 0.0F, 512, 512, 512, 512, color);
        } else {
            BiConsumer<Integer, Integer> render = (ix, iy) -> instance.drawTexture(renderPipeline, logo.getLogoId(), ix, iy, u, v, width, height, textureWidth, textureHeight);

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
            method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIII)V",
                    ordinal = 1
            )
    )
    @SuppressWarnings("unused")
    private void vistas$render(DrawContext instance, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color, Operation<Void> operation, DrawContext context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.get();
        LogoControl logo = panorama.getLogoControl();
        Matrix3x2fStack matrices = instance.getMatrices();

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
        GlStateManager._disableBlend();
        renderAction.accept(x, y);
    }
}
