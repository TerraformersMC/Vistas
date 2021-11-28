package com.terraformersmc.vistas.mixin;

import java.util.Random;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.panorama.LogoControl;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.BenignCubemapRenderer;
import com.terraformersmc.vistas.title.PanoramaRenderer;
import com.terraformersmc.vistas.title.VistasTitle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	@Unique
	private boolean isVistas = false;

	@Shadow
	@Mutable
	@Final
	private RotatingCubeMapRenderer backgroundRenderer;

	@Shadow
	@Final
	private boolean doBackgroundFade;

	@Nullable
	@Shadow
	private String splashText;

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "Lnet/minecraft/client/gui/screen/TitleScreen;<init>(Z)V", at = @At("TAIL"))
	private void vistas$init(boolean doBackgroundFade, CallbackInfo ci) {
		this.backgroundRenderer = new BenignCubemapRenderer();
		this.isVistas = new Random().nextDouble() < 1.0E-4D && VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.getValue()).equals(Vistas.DEFAULT);
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void vistas$init(CallbackInfo ci) {
		VistasTitle.choose();
		if (!VistasConfig.getInstance().forcePanorama && VistasConfig.getInstance().randomPerScreen) {
			this.isVistas = new Random().nextDouble() < 1.0E-4D && VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.getValue()).equals(Vistas.DEFAULT);
			this.splashText = null;
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void vistas$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f) {
		PanoramaRenderer.time += delta;
		VistasTitle.CURRENT.getValue().getCubemaps().forEach((cubemap) -> {
			PanoramaRenderer panoramaRenderer = new PanoramaRenderer(cubemap);
			panoramaRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
			Identifier overlayId = new Identifier(panoramaRenderer.getCubemap().getCubemapId() + "_overlay.png");
			if (this.client.getResourceManager().containsResource(overlayId)) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, overlayId);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
				drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
			}
		});
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawWithOutline(IILjava/util/function/BiConsumer;)V"))
	private void vistas$render$drawOutline(TitleScreen titleScreen, int x, int y, BiConsumer<Integer, Integer> consumer, MatrixStack matrices, int mouseX, int mouseY, float delta) {

		Panorama panorama = VistasTitle.CURRENT.getValue();
		LogoControl logo = panorama.getLogoControl();

		matrices.push();
		matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

		matrices.translate((this.width / 2.0D), (y * 2.0D) - (y / 2.0D), 0.0D);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) logo.getLogoRot()));
		matrices.translate(-(this.width / 2.0D), -(y * 2.0D) + (y / 2.0D), 0.0D);

		if (!logo.getLogoId().equals(new Identifier("textures/gui/title/minecraft.png")) || this.isVistas) {
			RenderSystem.setShaderTexture(0, this.isVistas ? Vistas.id("textures/vistas_logo.png") : logo.getLogoId());
			int rx = (this.width / 2) - 256;
			int ry = 52 - 256;
			BiConsumer<Integer, Integer> render = (ix, iy) -> Screen.drawTexture(matrices, ix, iy, this.getZOffset(), 0, 0, 512, 512, 512, 512);
			if (logo.isOutlined()) {
				this.drawWithOutline(rx, ry, render);
			} else {
				render.accept(rx, ry);
			}
		} else {
			if (logo.isOutlined()) {
				titleScreen.drawWithOutline(x, y, consumer);
			} else {
				consumer.accept(x, y);
			}
		}

		matrices.pop();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIII)V"))
	private void vistas$render(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		Panorama panorama = VistasTitle.CURRENT.getValue();
		LogoControl logo = panorama.getLogoControl();

		if (!logo.doesShowEdition()) {
			return;
		}

		matrices.push();
		matrices.translate(logo.getLogoX(), logo.getLogoY(), 0.0D);

		matrices.translate((this.width / 2.0D), 45, 0.0D);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) logo.getLogoRot()));
		matrices.translate(-(this.width / 2.0D), -45, 0.0D);

		TitleScreen.drawTexture(matrices, x, y, u, v, width, height, textureWidth, textureHeight);

		matrices.pop();
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3f;getDegreesQuaternion(F)Lnet/minecraft/util/math/Quaternion;"))
	private float vistas$render$changeAngle(float in) {
		return (float) VistasTitle.CURRENT.getValue().getLogoControl().getSplashRot();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V", shift = Shift.BEFORE))
	private void vistas$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

		Panorama panorama = VistasTitle.CURRENT.getValue();
		LogoControl logo = panorama.getLogoControl();

		matrices.translate(logo.getSplashX(), logo.getSplashY(), 0.0D);
	}

}
