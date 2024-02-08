package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.LogoDrawerAccessor;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
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
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 0
            )
    )
    @SuppressWarnings("unused")
    private void vistas$render$drawOutline(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> operation, DrawContext context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.getValue();
        LogoControl logo = panorama.getLogoControl();
        MatrixStack matrices = context.getMatrices();

        matrices.push();

        matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

        matrices.translate((screenWidth / 2.0D), (y * 2.0D) - (y / 2.0D), 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) logo.getLogoRot()));
        matrices.translate(-(screenWidth / 2.0D), -(y * 2.0D) + (y / 2.0D), 0.0D);

        if (!logo.getLogoId().equals(LOGO_TEXTURE) || this.isVistas) {
            Identifier logoTexture = this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId();
            int rx = (screenWidth / 2) - 256;
            int ry = 52 - 256;

            BiConsumer<Integer, Integer> render = (ix, iy) -> context.drawTexture(logoTexture, ix, iy, 0, 0, 0, 512, 512, 512, 512);

            if (logo.isOutlined()) {
                vistas$drawWithOutline(rx, ry, render);
            } else {
                render.accept(rx, ry);
            }

            operation.call(instance, logoTexture, rx, ry, 0, 0, 512, 512, 512, 512, 512);
        } else {
            BiConsumer<Integer, Integer> render = (ix, iy) -> context.drawTexture(logo.getLogoId(), ix, iy, u, v, width, height, textureWidth, textureHeight);

            if (logo.isOutlined()) {
                vistas$drawWithOutline(x, y, render);
            } else {
                render.accept(x, y);
            }

            operation.call(instance, logo.getLogoId(), x, y, u, v, width, height, textureWidth, textureHeight);
        }

        matrices.pop();
    }

    @WrapOperation(
            method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 1
            )
    )
    @SuppressWarnings("unused")
    private void vistas$render(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> operation, DrawContext context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.getValue();
        LogoControl logo = panorama.getLogoControl();
        MatrixStack matrices = context.getMatrices();

        if (!logo.doesShowEdition()) {
            return;
        }

        matrices.push();

        matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

        matrices.translate((screenWidth / 2.0D), 45, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) logo.getLogoRot()));
        matrices.translate(-(screenWidth / 2.0D), -45, 0.0D);

        operation.call(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);

        matrices.pop();
    }

    @Override
    public void vistas$setIsVistas(boolean value) {
        this.isVistas = value;
    }

    @Unique
    private static void vistas$drawWithOutline(int x, int y, BiConsumer<Integer, Integer> renderAction) {
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        renderAction.accept(x + 1, y);
        renderAction.accept(x - 1, y);
        renderAction.accept(x, y + 1);
        renderAction.accept(x, y - 1);
        RenderSystem.defaultBlendFunc();
        renderAction.accept(x, y);
    }
}
