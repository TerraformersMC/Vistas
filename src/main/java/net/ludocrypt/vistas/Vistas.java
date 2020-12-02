package net.ludocrypt.vistas;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.vistas.config.PanoramaConfig;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Vistas implements ClientModInitializer {

	public static Map<String, Panorama> builtinPanoramas = new HashMap<String, Panorama>();
	public static Map<String, Panorama> resourcePanoramas = new HashMap<String, Panorama>();
	public static Map<String, Panorama> panoramas = new HashMap<String, Panorama>();

	public static Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		PanoramaConfig.init();
	}

	public static class Panorama {

		private String name;
		private Identifier id;
		private MusicSound music;
		private int weight = 1;

		public Panorama(String name, Identifier id, Identifier music) {
			this.name = name;
			this.id = id;
			this.music = createMenuSound(new SoundEvent(music));
			this.weight = 1;
		}

		public Panorama(String name, Identifier id, SoundEvent music) {
			this.name = name;
			this.id = id;
			this.music = createMenuSound(music);
			this.weight = 1;
		}

		public Panorama(String name, Identifier id, MusicSound music) {
			this.name = name;
			this.id = id;
			this.music = music;
			this.weight = 1;
		}

		public Panorama(String name, Identifier id, Identifier music, int weight) {
			this(name, id, music);
			this.weight = weight;
		}

		public Panorama(String name, Identifier id, SoundEvent music, int weight) {
			this(name, id, music);
			this.weight = weight;
		}

		public Panorama(String name, Identifier id, MusicSound music, int weight) {
			this(name, id, music);
			this.weight = weight;
		}

		public String getName() {
			return name;
		}

		public Identifier getId() {
			return id;
		}

		public MusicSound getMusic() {
			return music;
		}

		public int getWeight() {
			return weight;
		}

		public static void addBuiltInPanorama(Panorama pan) {
			for (int i = 0; i < pan.getWeight(); i++) {
				builtinPanoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
			}
		}

		public static void addResourcePanorama(Panorama pan) {
			for (int i = 0; i < pan.getWeight(); i++) {
				resourcePanoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
			}
		}

		public static void addPanorama(Panorama pan) {
			for (int i = 0; i < pan.getWeight(); i++) {
				panoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
			}
		}

		public static Panorama getPanorama() {

			Panorama pickedPanorama = null;

			if (AutoConfig.getConfigHolder(PanoramaConfig.class) != null) {
				pickedPanorama = Vistas.panoramas.get(PanoramaConfig.INSTANCE().panorama);
			}

			return pickedPanorama;
		}

		public static Panorama getRandomPanorama() {
			return Vistas.panoramas.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.panoramas.size())];
		}

		public static void setRandomPanorama() {
			if (!PanoramaConfig.INSTANCE().forcePanorama) {
				if (Vistas.panoramas.size() >= 1) {
					Panorama pan = Panorama.getRandomPanorama();
					PanoramaConfig.INSTANCE().panorama = pan.getName();
				}
			}
		}

		public static void relaodPanoramas() {
			panoramas.clear();
			panoramas.putAll(builtinPanoramas);
			panoramas.putAll(resourcePanoramas);
			setRandomPanorama();
		}

	}

	private static MusicSound createMenuSound(SoundEvent event) {
		return new MusicSound(event, 20, 600, true);
	}
}
