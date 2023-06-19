package com.terraformersmc.vistas.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
@Mixin(LogoDrawer.class)
public abstract class LogoDrawerMixin implements LogoDrawerAccessor {
    @Unique
    private boolean isVistas = false;

    @Redirect(method = "draw(Lnet/minecraft/client/util/math/MatrixStack;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;drawWithOutline(IILjava/util/function/BiConsumer;)V"))
    private void vistas$render$drawOutline(int x, int y, BiConsumer<Integer, Integer> renderAction, MatrixStack matrices, int screenWidth, float alpha, int z) {
        Panorama panorama = VistasTitle.CURRENT.getValue();
        LogoControl logo = panorama.getLogoControl();

        matrices.push();
        matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

        matrices.translate((screenWidth / 2.0D), (y * 2.0D) - (y / 2.0D), 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) logo.getLogoRot()));
        matrices.translate(-(screenWidth / 2.0D), -(y * 2.0D) + (y / 2.0D), 0.0D);

        if (!logo.getLogoId().equals(new Identifier("textures/gui/title/minecraft.png")) || this.isVistas) {
            RenderSystem.setShaderTexture(0, this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId());
            int rx = (screenWidth / 2) - 256;
            int ry = 52 - 256;
            BiConsumer<Integer, Integer> render = (ix, iy) -> Screen.drawTexture(matrices, ix, iy, 0, 0, 0, 512, 512, 512, 512);
            if (logo.isOutlined()) {
                DrawableHelper.drawWithOutline(rx, ry, render);
            } else {
                render.accept(rx, ry);
            }
        } else {
            if (logo.isOutlined()) {
                DrawableHelper.drawWithOutline(x, y, renderAction);
            } else {
                renderAction.accept(x, y);
            }
        }

        matrices.pop();
    }

    @Redirect(method = "draw(Lnet/minecraft/client/util/math/MatrixStack;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V"))
    private void vistas$render(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, MatrixStack matrices1, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.getValue();
        LogoControl logo = panorama.getLogoControl();

        if (!logo.doesShowEdition()) {
            return;
        }

        matrices.push();
        matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

        matrices.translate((screenWidth / 2.0D), 45, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) logo.getLogoRot()));
        matrices.translate(-(screenWidth / 2.0D), -45, 0.0D);

        TitleScreen.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);

        matrices.pop();
    }

    @Override
    public void setIsVistas(boolean value) {
        this.isVistas = value;
    }
}
