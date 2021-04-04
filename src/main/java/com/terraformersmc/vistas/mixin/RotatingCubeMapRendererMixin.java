package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.terraformersmc.vistas.access.TimeAccess;
import com.terraformersmc.vistas.api.panorama.MovementSettings;
import com.terraformersmc.vistas.api.panorama.Panorama;
import com.terraformersmc.vistas.api.panorama.Panoramas;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

@Environment(EnvType.CLIENT)
@Mixin(value = RotatingCubeMapRenderer.class, priority = 69)
public abstract class RotatingCubeMapRendererMixin implements TimeAccess {

	@Shadow
	private float time;

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V"))
	public void VISTAS_modifyMovement(Args args, float delta, float alpha) {
		Panorama pan = Panoramas.getCurrent();
		MovementSettings settings = pan.getMovementSettings();

		float newX = -settings.getAddedX();
		float newY = -settings.getAddedY();

		if (!settings.isFrozen()) {

			if (!settings.isUsingYEquation()) {
				if (settings.isWoozy()) {
					newY += this.time * 0.1F;
				} else {
					newY += (float) args.get(1);
				}
			} else {
				newY = settings.getYEquation().apply(time);
			}

			if (!settings.isUsingXEquation()) {
				newX += (float) args.get(2);
			} else {
				newX = settings.getYEquation().apply(time);
			}

			newX *= settings.getSpeedMultiplier();
			newY *= settings.getSpeedMultiplier();
		}

		args.set(2, newX);
		args.set(1, newY);
	}

	@Override
	public float getTime() {
		return time;
	}

	@Override
	public void setTime(float time) {
		this.time = time;
	}

}
