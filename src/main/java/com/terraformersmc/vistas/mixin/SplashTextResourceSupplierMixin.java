package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import org.spongepowered.asm.mixin.injection.Slice;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
	@ModifyReturnValue(
			method = "get",
			at = @At(value = "RETURN"),
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
					to = @At(value = "TAIL")
			)
	)
	@SuppressWarnings("unused")
	private SplashTextRenderer vistas$getRenderer(SplashTextRenderer original) {
		MinecraftClient client = MinecraftClient.getInstance();
		PanoramaResourceReloader resourceReloader = ((MinecraftClientAccess) client).getPanoramaResourceReloader();
		Identifier panoramaId = VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.getValue());

		if (resourceReloader != null && panoramaId != null) {
			return new SplashTextRenderer(resourceReloader.get());
		}

		return original;
	}
}
