package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

	@Shadow
	@Final
	private LogoDrawer logoDrawer;

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

	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"
			)
	)
	@SuppressWarnings("unused")
	private void vistas$render(RotatingCubeMapRenderer instance, float delta, float fade, Operation<Void> operation, DrawContext context) {
		assert this.client != null;
		PanoramaRenderer.time += delta;
		VistasTitle.CURRENT.getValue().getCubemaps().forEach((cubemap) -> {
			PanoramaRenderer panoramaRenderer = new PanoramaRenderer(cubemap);
			panoramaRenderer.render(delta, fade);
			Identifier overlayId = new Identifier(panoramaRenderer.getCubemap().getCubemapId() + "_overlay.png");
			if (this.client.getResourceManager().getResource(overlayId).isPresent()) {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(fade) : 1.0F);
				context.drawTexture(overlayId, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
			}
		});

		operation.call(instance, delta, fade);
	}
}
