package net.ludocrypt.vistas.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.vistas.Vistas;
import net.ludocrypt.vistas.Vistas.Panorama;
import net.ludocrypt.vistas.config.PanoramaConfig;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

	@Shadow
	private RotatingCubeMapRenderer backgroundRenderer;

	@Inject(method = "<init>*", at = @At("TAIL"))
	private void VISTAS_PanoramaChanger(CallbackInfo ci) {

		this.backgroundRenderer = new RotatingCubeMapRenderer(new CubeMapRenderer(Panorama.getPanorama().getId()));

	}

	@Inject(method = "initWidgetsNormal", at = @At(value = "RETURN"))
	private void VISTAS_initPanoramaChange(int y, int spacingY, CallbackInfo ci) {

		if (PanoramaConfig.INSTANCE().randomPerScreen) {
			if (!PanoramaConfig.INSTANCE().forcePanorama) {
				PanoramaConfig.INSTANCE().panorama = Vistas.panoramas.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.panoramas.size())].getName();
			}
		}

		this.backgroundRenderer = new RotatingCubeMapRenderer(new CubeMapRenderer(Panorama.getPanorama().getId()));

	}

}
