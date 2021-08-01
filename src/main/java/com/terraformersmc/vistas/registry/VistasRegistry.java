package com.terraformersmc.vistas.registry;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.Lists;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.SimpleRegistry;

public class VistasRegistry {

	public static final SimpleRegistry<PanoramaGroup> PANORAMA_REGISTRY = FabricRegistryBuilder.createSimple(PanoramaGroup.class, Vistas.id("panorama_registry")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	public static PanoramaGroup getRandomPanorama() {
		return getRandomPanorama(new Random());
	}

	public static PanoramaGroup getRandomPanorama(Random random) {
		List<PanoramaGroup> panoramaGroups = Lists.newArrayList();
		for (PanoramaGroup panoramaGroup : PANORAMA_REGISTRY) {
			for (int i = 0; i < panoramaGroup.weight; i++) {
				panoramaGroups.add(panoramaGroup);
			}
		}
		return panoramaGroups.get(random.nextInt(panoramaGroups.size()));
	}

	public static PanoramaGroup getCurrentPanorama() {
		return MinecraftClientAccess.get().getCurrentPanorama();
	}

	public static void setCurrentPanorama(PanoramaGroup panGroup) {
		MinecraftClientAccess.get().setCurrentPanorama(panGroup);
	}

	public static Optional<PanoramaGroup> getConfigPanorama() {
		return getPanorama(VistasConfig.getInstance().panorama);
	}

	public static Optional<PanoramaGroup> getPanorama(String panorama) {
		try {
			return Optional.ofNullable(PANORAMA_REGISTRY.get(new Identifier(panorama)));
		} catch (InvalidIdentifierException e) {
			return Optional.empty();
		}
	}

	public static PanoramaGroup getChosenPanorama() {
		if (VistasConfig.getInstance().forcePanorama) {
			return getConfigPanorama().orElse(PanoramaGroup.DEFAULT);
		}
		return getRandomPanorama();
	}

	public static MusicSound getMenuSound(SoundEvent soundEvent) {
		return new MusicSound(soundEvent, 20, 600, true);
	}

}
