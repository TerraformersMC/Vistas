package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextRenderer.class)
public abstract class SplashTextRendererMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
	private float vistas$render$changeAngle(float in) {
		return (float) VistasTitle.CURRENT.getValue().getLogoControl().getSplashRot();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V", shift = At.Shift.BEFORE))
	private void vistas$render(DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha, CallbackInfo ci) {
		Panorama panorama = VistasTitle.CURRENT.getValue();
		LogoControl logo = panorama.getLogoControl();

		context.getMatrices().translate(logo.getSplashX(), logo.getSplashY(), 0.0D);
	}
}
