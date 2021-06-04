package com.terraformersmc.vistas.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terraformersmc.vistas.api.panorama.Panoramas;
import com.terraformersmc.vistas.resource.PanoramaManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.sound.MusicSound;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Unique
	private PanoramaManager panoramaManager;

	@Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
	private void VISTAS_modifyMusic(CallbackInfoReturnable<MusicSound> ci) {
		if (this.player == null) {
			ci.setReturnValue(Panoramas.getCurrent().getMusic());
		}
	}

	@Inject(method = "<init>", at = @At(value = "NEW", target = "Lnet/minecraft/client/sound/MusicTracker;", shift = Shift.BEFORE))
	private void VISTAS_appendPanoramaManager(CallbackInfo ci) {
		this.panoramaManager = new PanoramaManager();
		this.resourceManager.registerReloader(panoramaManager);
	}

}
