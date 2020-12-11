package com.terraformersmc.vistas.api.panorama;

import java.util.Random;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.config.PanoramaConfig;

import net.minecraft.client.MinecraftClient;

public class Panoramas {

	public static Panorama getCurrent() {

		Panorama pickedPanorama = Vistas.panoramas.get(PanoramaConfig.getInstance().panorama + "_0");

		if (pickedPanorama == null) {
			Vistas.LOGGER.warn("Config panorama null, trying client");
			pickedPanorama = ((MinecraftClientAccess) MinecraftClient.getInstance()).getClientPanorama();
		}

		if (pickedPanorama == null) {
			Vistas.LOGGER.warn("Client panorama null");
		}

		return pickedPanorama;
	}

	public static Panorama getRandom() {
		return Vistas.panoramas.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.panoramas.size())];
	}

	public static void setRandom() {
		if (!PanoramaConfig.getInstance().forcePanorama) {
			if (Vistas.panoramas.size() >= 1) {
				Panorama pan = getRandom();
				PanoramaConfig.getInstance().panorama = pan.getName();
				((MinecraftClientAccess) MinecraftClient.getInstance()).setClientPanorama(pan);
			}
		}
	}

	public static void reload() {
		Vistas.panoramas.clear();
		Vistas.panoramas.putAll(Vistas.builtinPanoramas);
		Vistas.panoramas.putAll(Vistas.resourcePanoramas);
		setRandom();
	}

	public static void add(Panorama panorama) {
		Vistas.addBuiltInPanorama(panorama);
	}
}
