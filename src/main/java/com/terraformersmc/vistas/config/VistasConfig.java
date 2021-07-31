package com.terraformersmc.vistas.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "vistas")
public class VistasConfig implements ConfigData {

	public String panorama = "vistas:default";
	public boolean forcePanorama = false;
	public boolean randomPerScreen = false;
	public boolean lockScreenshotPitch = true;
	public boolean lockScreenshotYaw = false;

	public static void init() {
		AutoConfig.register(VistasConfig.class, GsonConfigSerializer::new);
	}

	public static VistasConfig getInstance() {
		return AutoConfig.getConfigHolder(VistasConfig.class).getConfig();
	}

}
