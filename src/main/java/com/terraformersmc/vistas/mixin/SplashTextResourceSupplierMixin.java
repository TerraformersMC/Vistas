package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

	@Inject(method = "get", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
	private void vistas$get$4(CallbackInfoReturnable<String> ci) {
		vistas$get(ci);
	}

	@Inject(method = "get", at = @At(value = "RETURN", ordinal = 5), cancellable = true)
	private void vistas$get$5(CallbackInfoReturnable<String> ci) {
		vistas$get(ci);
	}

	private void vistas$get(CallbackInfoReturnable<String> ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		PanoramaResourceReloader resourceReloader = ((MinecraftClientAccess) client).getPanoramaResourceReloader();
		if (resourceReloader != null) {
			ci.setReturnValue(resourceReloader.get());
		}
	}

}
