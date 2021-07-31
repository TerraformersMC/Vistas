package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.terraformersmc.vistas.registry.panorama.VisualSettings;
import com.terraformersmc.vistas.util.RotatingPanoramicRenderer.PanoramicRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;

@Mixin(CubeMapRenderer.class)
public class CubeMapRendererMixin {

	@Unique
	private VisualSettings settings = null;

	@Inject(method = "draw", at = @At("HEAD"))
	private void vistas$draw(MinecraftClient client, float x, float y, float alpha, CallbackInfo ci) {
		if (((CubeMapRenderer) (Object) this)instanceof PanoramicRenderer panoramicRenderer) {
			this.settings = panoramicRenderer.panorama.visualSettings;
		}
	}

	@ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Matrix4f;viewboxMatrix(DFFF)Lnet/minecraft/util/math/Matrix4f;"), index = 0)
	private double vistas$draw$fov(double in) {
		if (settings != null) {
			return settings.fov == -1.0 ? MinecraftClient.getInstance().options.fov : settings.fov;
		}
		return in;
	}

	@ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Matrix4f;viewboxMatrix(DFFF)Lnet/minecraft/util/math/Matrix4f;"), index = 2)
	private float vistas$draw$viewDepth(float in) {
		if (settings != null) {
			return (float) ((Math.max(Math.max(settings.xLength / 2.0D, settings.yLength / 2.0D), settings.zLength / 2.0D) * in) + Math.max(Math.max(settings.addedX / 2.0D, settings.addedY / 2.0D), settings.addedZ / 2.0D));
		}
		return in;
	}

	@ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Matrix4f;viewboxMatrix(DFFF)Lnet/minecraft/util/math/Matrix4f;"), index = 3)
	private float vistas$draw$viewLength(float in) {
		if (settings != null) {
			return (float) ((Math.max(Math.max(settings.xLength, settings.yLength), settings.zLength) * in) + Math.max(Math.max(settings.addedX, settings.addedY), settings.addedZ));
		}
		return in;
	}

	@ModifyArgs(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(DDD)Lnet/minecraft/client/render/VertexConsumer;"))
	private void vistas$draw$vertex(Args args) {
		if (settings != null) {
			args.set(0, ((double) args.get(0) * (settings.xLength / 2.0D)) + settings.addedX);
			args.set(1, ((double) args.get(1) * (settings.yLength / 2.0D)) + settings.addedY);
			args.set(2, ((double) args.get(2) * (settings.zLength / 2.0D)) + settings.addedZ);
		}
	}

	@ModifyArgs(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"))
	private void vistas$draw$color(Args args) {
		if (settings != null) {
			args.set(0, settings.colorR);
			args.set(1, settings.colorG);
			args.set(2, settings.colorB);
		}
	}

}
