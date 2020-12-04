package com.terraformersmc.vistas.panorama;

import java.util.Random;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.config.PanoramaConfig;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class Panorama {

	private String name;
	private Identifier id;
	private MusicSound music;
	private int weight = 1;
	private MovementSettings movementSettings;

	public Panorama(String name, Identifier id, MusicSound music, MovementSettings movementSettings, int weight) {
		this.name = name;
		this.id = id;
		this.music = music;
		this.movementSettings = movementSettings;
		this.weight = weight;
	}

	public Panorama(String name, Identifier id, SoundEvent music, MovementSettings movementSettings, int weight) {
		this(name, id, Panorama.Builder.createMenuSound(music), movementSettings, weight);
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

	public MovementSettings getMovementSettings() {
		return movementSettings;
	}

	public int getWeight() {
		return weight;
	}

	public static Panorama getPanorama() {

		Panorama pickedPanorama = null;

		if (AutoConfig.getConfigHolder(PanoramaConfig.class) != null) {
			pickedPanorama = Vistas.panoramas.get(PanoramaConfig.INSTANCE().panorama);
		} else {
			Vistas.LOGGER.warn("Config not registered while trying for panorama");
		}

		if (pickedPanorama == null) {
			Vistas.LOGGER.warn("Config panorama null");
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
				((MinecraftClientAccess) MinecraftClient.getInstance()).setClientPanorama(pan);
			}
		}
	}

	public static void relaodPanoramas() {
		Vistas.panoramas.clear();
		Vistas.panoramas.putAll(Vistas.builtinPanoramas);
		Vistas.panoramas.putAll(Vistas.resourcePanoramas);
		setRandomPanorama();
	}

	public static class Builder {

		private String name;
		private Identifier id = new Identifier("textures/gui/title/background/panorama");
		private MusicSound music = createMenuSound(SoundEvents.MUSIC_MENU);
		private int weight = 1;

		private boolean frozen = false;
		private float addedX = 0.0F;
		private float addedY = 0.0F;
		private float speedMultiplier = 1.0F;
		private boolean woozy = false;

		public Builder(String name) {
			this.name = name;
		}

		public Builder setId(Identifier id) {
			this.id = id;
			return this;
		}

		public Builder setMusic(MusicSound music) {
			this.music = music;
			return this;
		}

		public Builder setWeight(int weight) {
			this.weight = weight;
			return this;
		}

		public Builder setFrozen(boolean frozen) {
			this.frozen = frozen;
			return this;
		}

		public Builder setAddedX(float addedX) {
			this.addedX = addedX;
			return this;
		}

		public Builder setAddedY(float addedY) {
			this.addedY = addedY;
			return this;
		}

		public Builder setSpeedMultiplier(float speedMultiplier) {
			this.speedMultiplier = speedMultiplier;
			return this;
		}

		public Builder setWoozy(boolean woozy) {
			this.woozy = woozy;
			return this;
		}

		public Panorama build() {
			return new Panorama(name, id, music, new MovementSettings(frozen, addedX, addedY, speedMultiplier, woozy), weight);
		}

		public static MusicSound createMenuSound(SoundEvent event) {
			return new MusicSound(event, 20, 600, true);
		}

	}

}
