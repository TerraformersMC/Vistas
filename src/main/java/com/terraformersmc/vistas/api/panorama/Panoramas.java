package com.terraformersmc.vistas.api.panorama;

import java.util.Random;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.PanoramaConfig;

public class Panoramas {

	public static Panorama getCurrent() {
		return Vistas.PANORAMAS.getOrDefault(PanoramaConfig.getInstance().panorama + "_0", Panorama.DEFAULT);
	}

	public static Panorama getRandom() {
		return Vistas.PANORAMAS.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.PANORAMAS.size())];
	}

	public static void setRandom() {
		if (!PanoramaConfig.getInstance().forcePanorama) {
			if (Vistas.PANORAMAS.size() > 0) {
				PanoramaConfig.getInstance().panorama = getRandom().getName();
			}
		}
	}

	public static void reload() {
		Vistas.PANORAMAS.clear();
		Vistas.PANORAMAS.putAll(Vistas.BUILTIN_PANORAMAS);
		Vistas.PANORAMAS.putAll(Vistas.RESOURCE_PANORAMAS);
		setRandom();
	}

	public static void add(Panorama panorama) {
		Vistas.addBuiltInPanorama(panorama);
	}
}
