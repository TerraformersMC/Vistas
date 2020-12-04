package com.terraformersmc.vistas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.terraformersmc.vistas.api.VistasApi;
import com.terraformersmc.vistas.config.PanoramaConfig;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.screenshot.PanoramicScreenshots;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public class Vistas implements ClientModInitializer {

	public static final String MOD_ID = "vistas";

	public static Map<String, Panorama> builtinPanoramas = new HashMap<>();
	public static Map<String, Panorama> resourcePanoramas = new HashMap<>();
	public static Map<String, Panorama> panoramas = new HashMap<>();

	public static Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		PanoramaConfig.init();
		PanoramicScreenshots.registerKeyBinding();
		FabricLoader.getInstance().getEntrypointContainers("vistas", VistasApi.class).forEach(container -> {
			VistasApi impl = container.getEntrypoint();
			HashSet<Panorama> panoramas = new HashSet<>();
			impl.appendPanoramas(panoramas);
			panoramas.forEach(Vistas::addBuiltInPanorama);
		});
	}

	private static void addBuiltInPanorama(Panorama pan) {
		for (int i = 0; i < pan.getWeight(); i++) {
			builtinPanoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
		}
	}

	public static void addResourcePanorama(Panorama pan) {
		for (int i = 0; i < pan.getWeight(); i++) {
			resourcePanoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
		}
	}
}
