package net.ludocrypt.vistas.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

@Config(name = "vistas")
public class PanoramaConfig implements ConfigData {

	public String panorama = "minecraft";
	public boolean forcePanorama = false;
	public boolean randomPerScreen = false;
	public boolean hectic = false;

	public static void init() {
		AutoConfig.register(PanoramaConfig.class, GsonConfigSerializer::new);
	}

	public static PanoramaConfig INSTANCE() {
		return AutoConfig.getConfigHolder(PanoramaConfig.class).getConfig();
	}

}
