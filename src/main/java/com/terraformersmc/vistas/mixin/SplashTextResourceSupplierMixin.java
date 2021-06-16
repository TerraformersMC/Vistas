package com.terraformersmc.vistas.mixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.api.panorama.Panorama;
import com.terraformersmc.vistas.api.panorama.Panoramas;
import com.terraformersmc.vistas.resource.InvalidPanoramaException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.util.Session;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(SplashTextResourceSupplier.class)
public abstract class SplashTextResourceSupplierMixin {

	@Shadow
	@Final
	private static Identifier RESOURCE_ID;

	@Shadow
	@Final
	private Session session;

	@Unique
	private ArrayList<String> storedSplashList = new ArrayList<String>();

	@Unique
	private ArrayList<Panorama> THIS_PANORAMA = new ArrayList<Panorama>();

	@Unique
	private ArrayList<Panorama> READ_SPLASHES = new ArrayList<Panorama>();

	@Unique
	private boolean isUpFirst = true;

	@Unique
	private static final String panoramaRegex = "\\B\\$.*(\\$Splash)(\\n|$)";

	@ModifyVariable(method = "Lnet/minecraft/client/resource/SplashTextResourceSupplier;prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"), ordinal = 0)
	private Resource VISTAS_redirectSplashReading(Resource in) {
		if (isUpFirst) {
			THIS_PANORAMA.add(Panoramas.getCurrent());
		}
		try {
			return MinecraftClient.getInstance().getResourceManager().getResource(THIS_PANORAMA.get(THIS_PANORAMA.size() - 1).getSplashTexts());
		} catch (IOException var36) {
			return in;
		}
	}

	@ModifyVariable(method = "Lnet/minecraft/client/resource/SplashTextResourceSupplier;prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;", at = @At(value = "RETURN", ordinal = 0, shift = Shift.BEFORE), name = "var5")
	private List<String> VISTAS_setList(List<String> in) {
		this.storedSplashList = new ArrayList<String>(in);
		return in;
	}

	@Inject(method = "Lnet/minecraft/client/resource/SplashTextResourceSupplier;prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;", at = @At(value = "RETURN", ordinal = 0, shift = Shift.BEFORE), cancellable = true)
	private void VISTAS_addOtherSplash(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> ci) {
		isUpFirst = false;
		VISTAS_addOtherSplash(resourceManager, profiler, ci, THIS_PANORAMA.get(THIS_PANORAMA.size() - 1));
		THIS_PANORAMA.clear();
		THIS_PANORAMA.add(Panoramas.getCurrent());
		READ_SPLASHES.clear();
	}

	@Unique
	private void VISTAS_addOtherSplash(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> ci, Panorama thisPanorama) {
		if (!READ_SPLASHES.contains(thisPanorama)) {
			READ_SPLASHES.add(thisPanorama);
			ArrayList<String> splashTexts = new ArrayList<String>(storedSplashList);
			ArrayList<String> newSplashTexts = new ArrayList<String>();
			ArrayList<Panorama> needToAdd = new ArrayList<Panorama>();
			Pattern pattern = Pattern.compile(panoramaRegex);
			for (String splash : splashTexts) {
				Matcher matcher = pattern.matcher(splash);
				if (matcher.find()) {
					try {
						needToAdd.add(Panoramas.getOrThrow(splash.substring(1, splash.lastIndexOf("$Splash")) + "_0"));
					} catch (InvalidPanoramaException ex) {
						Vistas.LOGGER.warn("\"" + thisPanorama.getSplashTexts().toString() + "\" calls \"" + splash + "\" which leads to no registered Panorama!");
					}
				} else {
					newSplashTexts.add(splash.replace("$P$Splash", session.getUsername().toUpperCase(Locale.ROOT)).replace("$p$Splash", session.getUsername()));
				}
			}

			for (Panorama panorama : needToAdd) {
				if (!READ_SPLASHES.contains(panorama)) {
					THIS_PANORAMA.clear();
					THIS_PANORAMA.add(panorama);
					newSplashTexts.addAll(prepare(resourceManager, profiler));
					READ_SPLASHES.clear();
				}
			}

			ci.setReturnValue(newSplashTexts);
		}
	}

	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	private void VISTAS_reloadBeforeGet(CallbackInfoReturnable<String> ci) {
		apply(prepare(MinecraftClient.getInstance().getResourceManager(), MinecraftClient.getInstance().getProfiler()), MinecraftClient.getInstance().getResourceManager(), MinecraftClient.getInstance().getProfiler());
	}

	@Shadow
	protected abstract List<String> prepare(ResourceManager resourceManager, Profiler profiler);

	@Shadow
	protected abstract void apply(List<String> list, ResourceManager resourceManager, Profiler profiler);
}
