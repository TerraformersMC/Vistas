package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.terraformersmc.vistas.access.RotatingCubeMapRendererAccess;
import com.terraformersmc.vistas.registry.panorama.MovementSettings;
import com.terraformersmc.vistas.registry.panorama.SinglePanorama;
import com.terraformersmc.vistas.util.RotatingPanoramicRenderer;

import net.minecraft.client.gui.RotatingCubeMapRenderer;

@Mixin(RotatingCubeMapRenderer.class)
public class RotatingCubeMapRendererMixin implements RotatingCubeMapRendererAccess {

	@Shadow
	private float time;

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/CubeMapRenderer;draw(Lnet/minecraft/client/MinecraftClient;FFF)V"))
	private void vistas$render(Args args, float delta, float alpha) {
		if (((RotatingCubeMapRenderer) (Object) this)instanceof RotatingPanoramicRenderer panoramicRenderer) {
			SinglePanorama panorama = panoramicRenderer.panorama;
			MovementSettings movementSettings = panorama.movementSettings;

			float newX = -movementSettings.addedX;
			float newY = -movementSettings.addedY;

			if (!movementSettings.frozen) {
				if (!movementSettings.isUsingYEquation()) {
					if (movementSettings.woozy) {
						newY += this.time * 0.1F;
					} else {
						newY += (float) args.get(1);
					}
				} else {
					newY = movementSettings.yEquation.apply(time);
				}

				if (!movementSettings.isUsingXEquation()) {
					newX += (float) args.get(2);
				} else {
					newX = movementSettings.yEquation.apply(time);
				}

				newX *= movementSettings.speedMultiplier;
				newY *= movementSettings.speedMultiplier;
			}

			args.set(1, newY);
			args.set(2, newX);
		}
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
