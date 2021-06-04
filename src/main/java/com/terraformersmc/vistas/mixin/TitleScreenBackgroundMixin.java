package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terraformersmc.vistas.access.TimeAccess;
import com.terraformersmc.vistas.api.panorama.Panoramas;
import com.terraformersmc.vistas.api.panorama.Panoramas.PanoramasInternals;
import com.terraformersmc.vistas.config.PanoramaConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenBackgroundMixin extends Screen {

	@Shadow
	@Final
	@Mutable
	private RotatingCubeMapRenderer backgroundRenderer;

	@Shadow
	private String splashText;

	protected TitleScreenBackgroundMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void VISTAS_updateScreen(CallbackInfo ci) {
		if (PanoramaConfig.getInstance().randomPerScreen) {
			Panoramas.setRandom();
			PanoramasInternals.shouldRedoSplash = true;
		}
		if (Panoramas.getCurrent() != null) {
			this.backgroundRenderer = TimeAccess.newWithTime(Panoramas.getCurrent().getBackgroundId(), TimeAccess.getTime(backgroundRenderer));
		} else {
			this.backgroundRenderer = TimeAccess.newWithTime(TitleScreen.PANORAMA_CUBE_MAP, TimeAccess.getTime(backgroundRenderer));
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 0), index = 1)
	private Identifier VISTAS_modifyOverlay(Identifier defaultOverlay) {
		if (Panoramas.getCurrent() != null) {
			Identifier overlayId = new Identifier(Panoramas.getCurrent().getBackgroundId().toString() + "_overlay.png");
			if (this.client.getResourceManager().containsResource(overlayId)) {
				return overlayId;
			}
		}
		return defaultOverlay;
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void VISTAS_modifySplashOnRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (PanoramasInternals.shouldRedoSplash) {
			this.splashText = this.client.getSplashTextLoader().get();
			PanoramasInternals.shouldRedoSplash = false;
		}
	}

}
