package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextRenderer.class)
public abstract class SplashTextRendererMixin {
	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"
			)
	)
	@SuppressWarnings("unused")
	private void vistas$render(MatrixStack instance, Quaternionf quaternion, Operation<Void> operation) {
		Panorama panorama = VistasTitle.CURRENT.getValue();
		LogoControl logo = panorama.getLogoControl();

		float rotation = (float) VistasTitle.CURRENT.getValue().getLogoControl().getSplashRot();

		instance.translate(logo.getSplashX(), logo.getSplashY(), 0.0D);
		operation.call(instance, RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
	}
}
