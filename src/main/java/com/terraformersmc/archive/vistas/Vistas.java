package com.terraformersmc.archive.vistas;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.terraformersmc.archive.vistas.api.VistasApi;
import com.terraformersmc.archive.vistas.api.panorama.Panorama;
import com.terraformersmc.archive.vistas.api.panorama.Panoramas;
import com.terraformersmc.archive.vistas.config.PanoramaConfig;
import com.terraformersmc.archive.vistas.screenshot.PanoramicScreenshots;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Deprecated
@Environment(EnvType.CLIENT)
public class Vistas implements ClientModInitializer {

	public static final String MOD_NAME = "Vistas";
	public static final String MOD_ID = "vistas";

	public static HashMap<String, Panorama> BUILTIN_PANORAMAS = new HashMap<String, Panorama>();
	public static HashMap<String, Panorama> RESOURCE_PANORAMAS = new HashMap<String, Panorama>();
	public static HashMap<String, Panorama> PANORAMAS = new HashMap<String, Panorama>();

	public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Override
	public void onInitializeClient() {
		PanoramaConfig.init();
		PanoramicScreenshots.registerKeyBinding();
		FabricLoader.getInstance().getEntrypointContainers(MOD_ID, VistasApi.class).forEach(container -> {
			VistasApi impl = container.getEntrypoint();
			HashSet<Panorama> builtInPanoramas = Sets.newHashSet();
			impl.appendPanoramas(builtInPanoramas);
			builtInPanoramas.forEach(Vistas::addBuiltInPanorama);
		});
	}

	public static void addBuiltInPanorama(Panorama pan) {
		for (int i = 0; i < pan.getWeight(); i++) {
			BUILTIN_PANORAMAS.put(pan.getName() + '_' + i, pan);
		}
		Panoramas.reload();
	}

	public static void addResourcePanorama(Panorama pan) {
		for (int i = 0; i < pan.getWeight(); i++) {
			RESOURCE_PANORAMAS.put(pan.getName() + '_' + i, pan);
		}
		Panoramas.reload();
	}

}
