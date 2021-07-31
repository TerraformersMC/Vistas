package com.terraformersmc.vistas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Vistas implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("Vistas");

	@Override
	public void onInitializeClient() {
		VistasConfig.init();
		Registry.register(VistasRegistry.PANORAMA_REGISTRY, Vistas.id("default"), PanoramaGroup.DEFAULT);
	}

	public static Identifier id(String id) {
		return new Identifier("vistas", id);
	}

}
