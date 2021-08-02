package com.terraformersmc.vistas.access;

import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;

import net.minecraft.client.MinecraftClient;

public interface MinecraftClientAccess {

	public PanoramaGroup getCurrentPanorama();

	public void setCurrentPanorama(PanoramaGroup panoramaGroup);

	public static MinecraftClientAccess get(Object obj) {
		return (MinecraftClientAccess) obj;
	}

	public static MinecraftClientAccess get() {
		return get(MinecraftClient.getInstance());
	}

}
