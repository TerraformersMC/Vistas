package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.access.MinecraftAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasPanorama;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.GameLoadCookie;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin implements MinecraftAccess {
	@Unique
	private PanoramaResourceReloader panoramaResourceReloader;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	@Final
	private TextureManager textureManager;

	@Inject(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;registerReloadListener(Lnet/minecraft/server/packs/resources/PreparableReloadListener;)V",
					ordinal = 2,
					shift = Shift.AFTER
			)
	)
	private void vistas$init$registerPanoramaReloader(GameConfig args, CallbackInfo ci) {
		this.panoramaResourceReloader = new PanoramaResourceReloader();
		this.resourceManager.registerReloadListener(panoramaResourceReloader);
	}

	@Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
	private void vistas$getMusicInstance(CallbackInfoReturnable<Music> ci) {
		if (this.player == null) {
			ci.setReturnValue(VistasTitle.CURRENT.get().getMusicSound());
		}
	}

	@Inject(method = "onResourceLoadFinished", at = @At("HEAD"))
	private void vistas$registerTextures(GameLoadCookie loadCookie, CallbackInfo ci) {
		if (Minecraft.getInstance().gameRenderer.panorama() instanceof VistasPanorama renderer) {
			renderer.registerTextures(textureManager);
		}
	}

	@Override
	public PanoramaResourceReloader getPanoramaResourceReloader() {
		return panoramaResourceReloader;
	}
}
