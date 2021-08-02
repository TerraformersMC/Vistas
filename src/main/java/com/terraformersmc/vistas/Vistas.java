package com.terraformersmc.vistas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.terraformersmc.vistas.api.VistasApi;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;
import com.terraformersmc.vistas.util.PanoramicScreenshots;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Vistas implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("Vistas");

	@Override
	public void onInitializeClient() {
		VistasConfig.init();
		PanoramicScreenshots.registerKeyBinding();
		Registry.register(VistasRegistry.PANORAMA_REGISTRY, Vistas.id("default"), PanoramaGroup.DEFAULT);
		FabricLoader.getInstance().getEntrypointContainers("vistas", VistasApi.class).forEach(container -> container.getEntrypoint().registerPanoramas());
	}

	public static Identifier id(String id) {
		return new Identifier("vistas", id);
	}

}
