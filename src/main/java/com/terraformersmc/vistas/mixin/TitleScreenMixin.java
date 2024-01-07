package com.terraformersmc.vistas.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.BenignCubemapRenderer;
import com.terraformersmc.vistas.title.LogoDrawerAccessor;
import com.terraformersmc.vistas.title.PanoramaRenderer;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	@Shadow
	@Mutable
	@Final
	private RotatingCubeMapRenderer backgroundRenderer;

	@Shadow
	@Final
	private boolean doBackgroundFade;

	@Nullable
	@Shadow
	private SplashTextRenderer splashText;

	@Shadow @Final private LogoDrawer logoDrawer;

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "<init>(Z)V", at = @At("TAIL"))
	private void vistas$init(boolean doBackgroundFade, CallbackInfo ci) {
		this.backgroundRenderer = new BenignCubemapRenderer();
		((LogoDrawerAccessor)this.logoDrawer).vistas$setIsVistas(new Random().nextDouble() < 1.0E-4D && VistasTitle.CURRENT.getValue().equals(VistasTitle.PANORAMAS.get(Vistas.DEFAULT)));
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void vistas$init(CallbackInfo ci) {
		if (PanoramaResourceReloader.isReady()) {
			VistasTitle.choose();
		}
		if (!VistasConfig.getInstance().forcePanorama && VistasConfig.getInstance().randomPerScreen) {
			((LogoDrawerAccessor)this.logoDrawer).vistas$setIsVistas(new Random().nextDouble() < 1.0E-4D && VistasTitle.CURRENT.getValue().equals(VistasTitle.PANORAMAS.get(Vistas.DEFAULT)));
			this.splashText = null;
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void vistas$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, float f) {
		assert this.client != null;
		PanoramaRenderer.time += delta;
		VistasTitle.CURRENT.getValue().getCubemaps().forEach((cubemap) -> {
			PanoramaRenderer panoramaRenderer = new PanoramaRenderer(cubemap);
			panoramaRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
			Identifier overlayId = new Identifier(panoramaRenderer.getCubemap().getCubemapId() + "_overlay.png");
			if (this.client.getResourceManager().getResource(overlayId).isPresent()) {
				// TODO: Some of these functions may be redundant.
//				RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//				RenderSystem.setShaderTexture(0, overlayId);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
				context.drawTexture(overlayId, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
			}
		});
	}

//	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
//	private float vistas$render$changeAngle(float in) {
//		return (float) VistasTitle.CURRENT.getValue().getLogoControl().getSplashRot();
//	}
//
//	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V", shift = Shift.BEFORE))
//	private void vistas$render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//
//		Panorama panorama = VistasTitle.CURRENT.getValue();
//		LogoControl logo = panorama.getLogoControl();
//
//		matrices.translate(logo.getSplashX(), logo.getSplashY(), 0.0D);
//	}

}
