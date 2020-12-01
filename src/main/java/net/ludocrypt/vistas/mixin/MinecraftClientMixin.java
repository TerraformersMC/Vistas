package net.ludocrypt.vistas.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.vistas.Vistas.Panorama;
import net.ludocrypt.vistas.access.PanoramaManagerAccess;
import net.ludocrypt.vistas.resource.PanoramaManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.sound.MusicSound;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements PanoramaManagerAccess {

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	@Final
	private ReloadableResourceManager resourceManager;

	@Unique
	private PanoramaManager panoramaManager;

	@Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
	private void VISTAS_getMusicType(CallbackInfoReturnable<MusicSound> ci) {
		if (this.player == null) {
			ci.setReturnValue(Panorama.getPanorama().getMusic());
		}
	}

	@Inject(method = "<init>*", at = @At(value = "NEW", target = "Lnet/minecraft/client/sound/SoundManager;"))
	private void VISTAS_PanoramaManagerMixin(CallbackInfo ci) {
		this.panoramaManager = new PanoramaManager();
		this.resourceManager.registerListener(panoramaManager);
	}

	@Override
	public PanoramaManager getPanoramaManager() {
		return panoramaManager;
	}
}
