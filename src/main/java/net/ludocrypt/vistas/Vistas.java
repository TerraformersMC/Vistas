package net.ludocrypt.vistas;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.vistas.config.PanoramaConfig;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Vistas implements ClientModInitializer {

	public static Map<Identifier, Panorama> panoramas = new HashMap<Identifier, Panorama>();
	private static Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		// Config Registry
		PanoramaConfig.init();

		// Adding the default Panorama
		Panorama.addPanorama(new Identifier("minecraft"), new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")));

		// Chose random panorama
		if (!PanoramaConfig.INSTANCE().forcePanorama) {
			PanoramaConfig.INSTANCE().panorama = panoramas.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.panoramas.size())].getName();
		}

	}

	public static class Panorama {

		private Identifier name;
		private Identifier id;
		private MusicSound music;

		public Panorama(Identifier name, Identifier id, SoundEvent music) {
			this.name = name;
			this.id = id;
			this.music = createMenuSound(music);
		}

		public Panorama(Identifier name, Identifier id, MusicSound music) {
			this.name = name;
			this.id = id;
			this.music = music;
		}

		public String getName() {
			return name.toString();
		}

		public Identifier getId() {
			return id;
		}

		public MusicSound getMusic() {
			return music;
		}

		public static void addPanorama(Identifier name, Identifier id, SoundEvent music) {
			Panorama pan = new Panorama(name, id, music);
			panoramas.put(name, pan);
		}

		public static void addPanorama(Identifier name, Identifier id, MusicSound music) {
			Panorama pan = new Panorama(name, id, music);
			panoramas.put(name, pan);
		}

		public static Panorama getPanorama() {
			Vistas.Panorama pickedPanorama = Vistas.panoramas.get(new Identifier(PanoramaConfig.INSTANCE().panorama));

			if (pickedPanorama == null) {
				LOGGER.warn("No Panorama Registered! Throwing to default");
				pickedPanorama = Vistas.panoramas.get(new Identifier("minecraft"));
				PanoramaConfig.INSTANCE().panorama = new Identifier("minecraft").toString();
			}

			return pickedPanorama;
		}

	}

	private static MusicSound createMenuSound(SoundEvent event) {
		return new MusicSound(event, 20, 600, true);
	}
}
