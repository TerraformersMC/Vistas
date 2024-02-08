package com.terraformersmc.vistas.mixin;

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
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(LogoDrawer.class)
public abstract class LogoDrawerMixin implements LogoDrawerAccessor {
    @Shadow @Final public static Identifier LOGO_TEXTURE;
    @Unique
    private boolean isVistas = true;

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 0))
    private void vistas$render$drawOutline(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, DrawContext _context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.getValue();
        LogoControl logo = panorama.getLogoControl();
        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

        matrices.translate((screenWidth / 2.0D), (y * 2.0D) - (y / 2.0D), 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) logo.getLogoRot()));
        matrices.translate(-(screenWidth / 2.0D), -(y * 2.0D) + (y / 2.0D), 0.0D);

        // TODO: Outline rendering no longer works
        if (!logo.getLogoId().equals(LOGO_TEXTURE) || this.isVistas) {
            //RenderSystem.setShaderTexture(0, this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId());
            int rx = (screenWidth / 2) - 256;
            int ry = 52 - 256;
//            BiConsumer<Integer, Integer> render = (ix, iy) -> Screen.drawTexture(matrices, ix, iy, 0, 0, 0, 512, 512, 512, 512);
//            if (logo.isOutlined()) {
//                DrawableHelper.drawWithOutline(rx, ry, render);
//            } else {
//                render.accept(rx, ry);
//            }
             Identifier logoTexture = this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId();
             context.drawTexture(logoTexture, rx, ry, 0, 0, 512, 512, 512, 512, 512);
        } else {
//            if (logo.isOutlined()) {
//                DrawableHelper.drawWithOutline(x, y, renderAction);
//            } else {
//                renderAction.accept(x, y);
//            }

            context.drawTexture(logo.getLogoId(), x, y, u, v, width, height, textureWidth, textureHeight);
        }



        matrices.pop();
    }

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 1))
    private void vistas$render(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, DrawContext _context, int screenWidth) {
        Panorama panorama = VistasTitle.CURRENT.getValue();
        LogoControl logo = panorama.getLogoControl();

        if (!logo.doesShowEdition()) {
            return;
        }

        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

        matrices.translate((screenWidth / 2.0D), 45, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) logo.getLogoRot()));
        matrices.translate(-(screenWidth / 2.0D), -45, 0.0D);

        context.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);

        matrices.pop();
    }

    @Override
    public void vistas$setIsVistas(boolean value) {
        this.isVistas = value;
    }
}
