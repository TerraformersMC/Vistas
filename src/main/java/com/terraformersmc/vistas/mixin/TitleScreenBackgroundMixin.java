package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terraformersmc.vistas.Panorama;
import com.terraformersmc.vistas.config.PanoramaConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenBackgroundMixin extends Screen {

	protected TitleScreenBackgroundMixin(Text title) {
		super(title);
	}

	@Shadow
	private RotatingCubeMapRenderer backgroundRenderer;

	@Inject(method = "init", at = @At("TAIL"))
	private void VISTAS_initPanoramaChange(CallbackInfo ci) {
		if (PanoramaConfig.INSTANCE().randomPerScreen) {
			Panorama.setRandomPanorama();
		}
		updateScreen();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void VISTAS_tickPanoramaChange(CallbackInfo ci) {

		if (PanoramaConfig.INSTANCE().hectic) {
			Panorama.setRandomPanorama();

			updateScreen();
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V", ordinal = 0), index = 0)
	private Identifier VISTAS_overlayMixin(Identifier defaultOverlay) {
		if (Panorama.getPanorama() != null) {
			Identifier overlayId = new Identifier(Panorama.getPanorama().getId().toString() + "_overlay.png");
			if (this.client.getResourceManager().containsResource(overlayId)) {
				return overlayId;
			}
		}
		return defaultOverlay;
	}

	private void updateScreen() {
		if (Panorama.getPanorama() != null) {
			this.backgroundRenderer = new RotatingCubeMapRenderer(new CubeMapRenderer(Panorama.getPanorama().getId()));
		} else {
			this.backgroundRenderer = new RotatingCubeMapRenderer(TitleScreen.PANORAMA_CUBE_MAP);
		}
	}

}
