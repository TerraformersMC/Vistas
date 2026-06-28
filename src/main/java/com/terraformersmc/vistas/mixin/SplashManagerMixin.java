package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.terraformersmc.vistas.access.MinecraftAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Environment(EnvType.CLIENT)
@Mixin(SplashManager.class)
public class SplashManagerMixin {
	@Shadow
	private static Component literalSplash(String string) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@ModifyReturnValue(
			method = "getSplash",
			at = @At(value = "RETURN"),
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
					to = @At(value = "TAIL")
			)
	)
	@SuppressWarnings("unused")
	private SplashRenderer vistas$getRenderer(SplashRenderer original) {
		Minecraft client = Minecraft.getInstance();
		PanoramaResourceReloader resourceReloader = ((MinecraftAccess) client).getPanoramaResourceReloader();
		Identifier panoramaId = VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.get());

		if (resourceReloader != null && panoramaId != null) {
			return new SplashRenderer(literalSplash(resourceReloader.get()));
		}

		return original;
	}
}
