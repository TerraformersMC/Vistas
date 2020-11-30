package net.ludocrypt.vistas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.vistas.Vistas.Panorama;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.MusicSound;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Shadow
	public ClientPlayerEntity player;

	@Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
	private void VISTAS_getMusicType(CallbackInfoReturnable<MusicSound> ci) {
		if (this.player == null) {
			ci.setReturnValue(Panorama.getPanorama().getMusic());
		}
	}
}
