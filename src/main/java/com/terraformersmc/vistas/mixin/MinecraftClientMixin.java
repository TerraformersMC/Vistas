package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.resource.PanoramaManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.sound.MusicSound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientAccess {

    @Shadow
    public ClientPlayerEntity player;

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Unique
    private PanoramaManager panoramaManager;

    @Unique
    private Panorama clientPanorama;

    @Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
    private void VISTAS_getMusicType(CallbackInfoReturnable<MusicSound> ci) {
        if (this.player == null) {
            if (clientPanorama != null) {
                ci.setReturnValue(Panorama.getPanorama().getMusic());
            }
        }
    }

    @Inject(method = "<init>", at = @At(value = "NEW", target = "Lnet/minecraft/client/texture/TextureManager;"))
    private void VISTAS_PanoramaManagerMixin(CallbackInfo ci) {
        this.panoramaManager = new PanoramaManager();
        this.resourceManager.registerListener(panoramaManager);
        clientPanorama = null;
    }

    @Override
    public void setClientPanorama(Panorama pan) {
        clientPanorama = pan;
    }
}
