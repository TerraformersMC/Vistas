package com.terraformersmc.vistas.mixin;

import net.minecraft.resource.ReloadableResourceManagerImpl;
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
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasTitle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.MusicSound;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientAccess {

    @Unique
    private PanoramaResourceReloader panoramaResourceReloader;

    @Shadow
    @Final
    private ReloadableResourceManagerImpl resourceManager;

    @Nullable
    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource" +
            "/ReloadableResourceManagerImpl;registerReloader(Lnet/minecraft/resource/ResourceReloader;)V", ordinal =
            2, shift = Shift.AFTER))
    private void vistas$init$registerPanoramaReloader(RunArgs args, CallbackInfo ci) {
        this.panoramaResourceReloader = new PanoramaResourceReloader();
        this.resourceManager.registerReloader(panoramaResourceReloader);
    }

    @Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
    private void vistas$getMusicType(CallbackInfoReturnable<MusicSound> ci) {
        if (this.player == null) {
            ci.setReturnValue(VistasTitle.CURRENT.getValue()
                    .getMusicSound());
        }
    }

    @Override
    public PanoramaResourceReloader getPanoramaResourceReloader() {
        return panoramaResourceReloader;
    }
}
