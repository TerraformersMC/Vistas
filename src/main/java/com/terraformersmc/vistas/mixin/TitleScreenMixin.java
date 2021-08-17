package com.terraformersmc.vistas.mixin;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.vistas.Vistas;
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
import net.minecraft.util.Identifier;
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

	@Unique
	private boolean isVistas = false;

	@Unique
	private boolean isVistasReadyToChange = true;

	@Shadow
	@Nullable
	private String splashText;

	@Shadow
	@Final
	private static Identifier MINECRAFT_TITLE_TEXTURE;

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

			if (this.isVistasReadyToChange == true) {
				this.isVistasReadyToChange = false;
				if (VistasRegistry.PANORAMA_REGISTRY.getId(panorama) != null) {
					this.isVistas = VistasRegistry.PANORAMA_REGISTRY.getId(panorama).equals(Vistas.id("default")) && new Random().nextDouble() < 1.0E-4D;
				} else {
					this.isVistas = false;
				}
			}
		}
		backgroundRenderers.forEach((rcmr) -> rcmr.render(delta, MathHelper.clamp(MathHelper.clamp(f, 0.0F, 1.0F) * ((float) rcmr.panorama.visualSettings.alpha / 255.0F), 0.0F, 1.0F)));
	}

	@Redirect(method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"))
	private void vistas$overrider$render(RotatingCubeMapRenderer rcmr, float delta, float alpha) {
		// Remove completely
	}

	@Redirect(method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawWithOutline(IILjava/util/function/BiConsumer;)V"))
	private void vistas$overrider$render(TitleScreen titleScreen, int x, int y, BiConsumer<Integer, Integer> consumer, MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.panorama != null && this.panorama.titleSettings != null) {
			if ((this.isVistas && this.panorama.titleSettings.titleId.equals(MINECRAFT_TITLE_TEXTURE)) || (this.panorama.titleSettings.titleId != null && !this.panorama.titleSettings.titleId.equals(MINECRAFT_TITLE_TEXTURE))) {
				RenderSystem.setShaderTexture(0, this.isVistas ? Vistas.id("textures/vistas_logo.png") : this.panorama.titleSettings.titleId);
				int rx = (this.width / 2) + this.panorama.titleSettings.addedX;
				int ry = y + 22 + this.panorama.titleSettings.addedY;
				BiConsumer<Integer, Integer> render = (ix, iy) -> {
					TitleScreen.drawTexture(matrices, ix - 256, iy - 256, this.getZOffset(), 0, 0, 512, 512, 512, 512);
				};
				if (this.panorama.titleSettings.outlined) {
					this.drawWithOutline(rx, ry, render);
				} else {
					render.accept(rx, ry);
				}
			} else {
				titleScreen.drawWithOutline(x - this.panorama.titleSettings.addedX, y - this.panorama.titleSettings.addedY, consumer);
			}
			return;
		}
		titleScreen.drawWithOutline(x, y, consumer);
	}

	@Redirect(method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V"))
	private void vistas$overrider$render(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		if (this.panorama != null && this.panorama.titleSettings != null && !this.panorama.titleSettings.showEdition) {
			return;
		}
		TitleScreen.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);
	}

	@ModifyArgs(method = "Lnet/minecraft/client/gui/screen/TitleScreen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
	private void vistas$overrider$render(Args args) {
		if (this.panorama != null && this.panorama.titleSettings != null) {
			args.set(3, (int) args.get(3) + this.panorama.titleSettings.addedSplashX);
			args.set(4, (int) args.get(4) + this.panorama.titleSettings.addedSplashY);
		}
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void vistas$init(CallbackInfo ci) {
		if (VistasConfig.getInstance().randomPerScreen && initCount > 1) {
			VistasRegistry.setCurrentPanorama(VistasRegistry.getChosenPanorama());
			this.splashText = this.client.getSplashTextLoader().get();
			this.isVistasReadyToChange = true;
		}
		initCount++;
	}

}
