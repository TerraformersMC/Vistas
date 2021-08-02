package com.terraformersmc.vistas.mixin;

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

import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;
import com.terraformersmc.vistas.registry.resource.PanoramaResourceManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.sound.MusicSound;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientAccess {

	@Unique
	@Nullable
	private PanoramaGroup currentPanorama = null;

	@Unique
	private PanoramaResourceManager panoramaManager;

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;registerReloader(Lnet/minecraft/resource/ResourceReloader;)V", ordinal = 3, shift = Shift.AFTER))
	private void vistas$appendPanoramaManager(RunArgs args, CallbackInfo ci) {
		this.panoramaManager = new PanoramaResourceManager();
		this.resourceManager.registerReloader(panoramaManager);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void vistas$tick(CallbackInfo ci) {
		if (this.getCurrentPanorama() == null) {
			this.setCurrentPanorama(VistasRegistry.getChosenPanorama());
			VistasConfig.getInstance().panorama = VistasRegistry.PANORAMA_REGISTRY.getId(this.getCurrentPanorama()).toString();
		}
	}

	@Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
	private void vistas$getMusicType(CallbackInfoReturnable<MusicSound> ci) {
		if (this.player == null && this.getCurrentPanorama() != null) {
			ci.setReturnValue(this.getCurrentPanorama().music);
		}
	}

	@Override
	@Nullable
	public PanoramaGroup getCurrentPanorama() {
		return currentPanorama;
	}

	@Override
	public void setCurrentPanorama(@Nullable PanoramaGroup panoramaGroup) {
		this.currentPanorama = panoramaGroup;
	}

}
