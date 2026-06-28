package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.LogoRendererAccessor;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	@Shadow
	@Final
	private LogoRenderer logoRenderer;

	@Nullable
	@Shadow
	private SplashRenderer splash;

	protected TitleScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "<init>(Z)V", at = @At("TAIL"))
	private void vistas$init(boolean doBackgroundFade, CallbackInfo ci) {
		((LogoRendererAccessor)this.logoRenderer).vistas$setIsVistas(new Random().nextDouble() < 1.0E-4D && VistasTitle.CURRENT.get().equals(VistasTitle.PANORAMAS.get(Vistas.DEFAULT)));
	}

	@Inject(method = "init", at = @At("HEAD"))
	private void vistas$init(CallbackInfo ci) {
		if (PanoramaResourceReloader.isReady()) {
			VistasTitle.choose();
		}
		if (!VistasConfig.getInstance().forcePanorama && VistasConfig.getInstance().randomPerScreen) {
			((LogoRendererAccessor)this.logoRenderer).vistas$setIsVistas(new Random().nextDouble() < 1.0E-4D && VistasTitle.CURRENT.get().equals(VistasTitle.PANORAMAS.get(Vistas.DEFAULT)));
			this.splash = null;
		}
	}
}
