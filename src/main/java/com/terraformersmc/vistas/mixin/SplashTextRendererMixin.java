package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextRenderer.class)
public abstract class SplashTextRendererMixin {
	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lorg/joml/Matrix3x2fStack;rotate(F)Lorg/joml/Matrix3x2f;"
			)
	)
	@SuppressWarnings("unused")
	private Matrix3x2f vistas$render(Matrix3x2fStack instance, float rotation, Operation<Matrix3x2f> operation) {
		Panorama panorama = VistasTitle.CURRENT.getValue();
		LogoControl logo = panorama.getLogoControl();

		rotation = (float) VistasTitle.CURRENT.getValue().getLogoControl().getSplashRot();

		instance.translate((float) logo.getSplashX(), (float) logo.getSplashY());

		return operation.call(instance, (float) Math.toRadians(rotation));
	}
}
