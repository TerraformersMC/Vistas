package com.terraformersmc.vistas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.util.PanoramicScreenshots;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

public class Vistas implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("Vistas");

	@Override
	public void onInitializeClient() {
		VistasConfig.init();
		PanoramicScreenshots.registerKeyBinding();
	}

	public static Identifier id(String id) {
		return new Identifier("vistas", id);
	}

}
