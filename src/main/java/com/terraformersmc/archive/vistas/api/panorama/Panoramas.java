package com.terraformersmc.archive.vistas.api.panorama;

import java.util.Random;

import com.terraformersmc.archive.vistas.Vistas;
import com.terraformersmc.archive.vistas.config.PanoramaConfig;
import com.terraformersmc.archive.vistas.resource.InvalidPanoramaException;
@Deprecated
public class Panoramas {

	public static Panorama getCurrent() {
		return get(PanoramaConfig.getInstance().panorama);
	}

	public static Panorama getRandom() {
		return Vistas.PANORAMAS.size() > 0 ? Vistas.PANORAMAS.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.PANORAMAS.size())] : Panorama.DEFAULT;
	}

	public static Panorama get(String id) {
		return PanoramasInternals.getForced(id + "_0");
	}

	public static Panorama getOrThrow(String id) throws InvalidPanoramaException {
		if (Vistas.PANORAMAS.get(id) == null) {
			throw new InvalidPanoramaException(id);
		} else {
			return Vistas.PANORAMAS.get(id);
		}
	}

	public static void set(String id) {
		PanoramasInternals.setForced(id + "_0");
	}

	public static void setRandom() {
		PanoramasInternals.setForced(getRandom().getName());
	}

	public static void reload() {
		Vistas.PANORAMAS.clear();
		Vistas.PANORAMAS.putAll(Vistas.BUILTIN_PANORAMAS);
		Vistas.PANORAMAS.putAll(Vistas.RESOURCE_PANORAMAS);
		setRandom();
	}

	@Deprecated
	public static void add(Panorama panorama) {
		Vistas.addBuiltInPanorama(panorama);
	}

	public static class PanoramasInternals {
		public static boolean shouldRedoSplash = true;

		public static void setForced(String id) {
			if (!PanoramaConfig.getInstance().forcePanorama) {
				if (Vistas.PANORAMAS.size() > 0) {
					PanoramaConfig.getInstance().panorama = id;
				}
			}
		}

		public static Panorama getForced(String id) {
			return Vistas.PANORAMAS.getOrDefault(id, Panorama.DEFAULT);
		}
	}
}
