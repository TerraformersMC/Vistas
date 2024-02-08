package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
	@Inject(method = "get", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
	private void vistas$get$4(CallbackInfoReturnable<SplashTextRenderer> ci) {
		vistas$get(ci);
	}

	@Inject(method = "get", at = @At(value = "RETURN", ordinal = 5), cancellable = true)
	private void vistas$get$5(CallbackInfoReturnable<SplashTextRenderer> ci) {
		vistas$get(ci);
	}

	private void vistas$get(CallbackInfoReturnable<SplashTextRenderer> ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		PanoramaResourceReloader resourceReloader = ((MinecraftClientAccess) client).getPanoramaResourceReloader();
		Identifier panoramaId = VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.getValue());

		if (resourceReloader != null && panoramaId != null) {
			ci.setReturnValue(new SplashTextRenderer(resourceReloader.get()));
		}
	}
}
