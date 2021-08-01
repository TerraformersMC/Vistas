package com.terraformersmc.vistas.mixin;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Lists;
import com.terraformersmc.vistas.access.RotatingCubeMapRendererAccess;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;
import com.terraformersmc.vistas.registry.panorama.SinglePanorama;
import com.terraformersmc.vistas.util.RotatingPanoramicRenderer;
import com.terraformersmc.vistas.util.RotatingPanoramicRenderer.PanoramicRenderer;

import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Unique
	private ArrayList<RotatingPanoramicRenderer> backgroundRenderers = Lists.newArrayList();

	@Unique
	private PanoramaGroup panorama = null;

	@Unique
	private int initCount = 0;

	@Shadow
	@Nullable
	private String splashText;

	@SuppressWarnings("unchecked")
	@Inject(method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void vistas$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f) {
		if (panorama != VistasRegistry.getCurrentPanorama()) {
			ArrayList<RotatingPanoramicRenderer> before = (ArrayList<RotatingPanoramicRenderer>) this.backgroundRenderers.clone();
			this.panorama = VistasRegistry.getCurrentPanorama();
			backgroundRenderers.clear();
			for (SinglePanorama panorama : this.panorama.panoramas) {
				backgroundRenderers.add(new RotatingPanoramicRenderer(new PanoramicRenderer(panorama), before.isEmpty() ? 0.0F : RotatingCubeMapRendererAccess.get(before.get(0)).getTime()));
			}
		}
		backgroundRenderers.forEach((rcmr) -> rcmr.render(delta, MathHelper.clamp(MathHelper.clamp(f, 0.0F, 1.0F) * ((float) rcmr.panorama.visualSettings.alpha / 255.0F), 0.0F, 1.0F)));
	}

	@Redirect(method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"))
	private void vistas$overrider$render(RotatingCubeMapRenderer rcmr, float delta, float alpha) {
		// Remove completely
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void vistas$init(CallbackInfo ci) {
		if (VistasConfig.getInstance().randomPerScreen && initCount > 1) {
			VistasRegistry.setCurrentPanorama(VistasRegistry.getChosenPanorama());
			this.splashText = this.client.getSplashTextLoader().get();
		}
		initCount++;
	}

}
