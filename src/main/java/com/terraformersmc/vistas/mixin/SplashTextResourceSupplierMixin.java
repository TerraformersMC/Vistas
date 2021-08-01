package com.terraformersmc.vistas.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.util.Session;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(SplashTextResourceSupplier.class)
public abstract class SplashTextResourceSupplierMixin {

	@Shadow
	@Final
	private Session session;

	@Shadow
	@Final
	private static Random RANDOM;

	@Unique
	private Identifier splashId = null;

	@Unique
	private boolean force = false;

	@ModifyArg(method = "prepare", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"), index = 0)
	private Identifier vistas$prepare(Identifier in) {
		return force ? splashId : splashId != null ? splashId : in;
	}

	@Unique
	private List<String> getSplashs(Identifier in, boolean force, ArrayList<PanoramaGroup> dontDo) {
		this.splashId = in;
		this.force = force;
		MinecraftClient client = MinecraftClient.getInstance();
		List<String> list = run(prepare(client.getResourceManager(), client.getProfiler()), dontDo);
		this.splashId = null;
		this.force = false;
		return list;
	}

	@Unique
	@SuppressWarnings("unchecked")
	private List<String> run(List<String> list, ArrayList<PanoramaGroup> dontDo) {
		List<String> unquantized = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			String text = list.get(i);
			if (text.startsWith("$vistas$other$")) {
				PanoramaGroup panGroup = VistasRegistry.getPanorama(text.substring(14)).get();
				if (panGroup != null) {
					if (!dontDo.contains(panGroup)) {
						ArrayList<PanoramaGroup> newDontDo = (ArrayList<PanoramaGroup>) dontDo.clone();
						newDontDo.add(panGroup);
						unquantized.addAll(getSplashs(panGroup.splashTexts, true, newDontDo));
					}
				} else {
					Vistas.LOGGER.warn("Splash text {} tried to load {} but wasnt registered", VistasRegistry.getCurrentPanorama(), text.substring(14));
				}
			} else if (text.contains("$vistas$p$") || text.contains("$vistas$P$") || text.contains("$vistas$pP$")) {
				unquantized.add(text.replace("$vistas$pP$", session.getUsername()).replace("$vistas$p$", session.getUsername().toLowerCase(Locale.ROOT)).replace("$vistas$P$", session.getUsername().toUpperCase(Locale.ROOT)));
			} else {
				unquantized.add(text);
			}
		}
		return unquantized;
	}

	@Inject(method = "get", at = @At(value = "RETURN", ordinal = 5), cancellable = true)
	private void vistas$get(CallbackInfoReturnable<String> ci) {
		if (VistasRegistry.getCurrentPanorama() != PanoramaGroup.DEFAULT) {
			List<String> list = getSplashs(VistasRegistry.getCurrentPanorama().splashTexts, true, Lists.newArrayList(VistasRegistry.getCurrentPanorama()));
			ci.setReturnValue(list.get(RANDOM.nextInt(list.size())));
		}
	}

	@Shadow
	protected abstract List<String> prepare(ResourceManager resourceManager, Profiler profiler);

}
