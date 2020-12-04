package com.terraformersmc.vistas.api.panorama;

import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class Panorama {

	private String name;
	private Identifier backgroundId;
	private MusicSound music;
	private int weight = 1;
	private MovementSettings movementSettings;

	private Panorama(String name, Identifier id, MusicSound music, MovementSettings movementSettings, int weight) {
		this.name = name;
		this.backgroundId = id;
		this.music = music;
		this.movementSettings = movementSettings;
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public Identifier getBackgroundId() {
		return backgroundId;
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

	public static class Builder {

		private String name;
		private Identifier backgroundId = new Identifier("textures/gui/title/background/panorama");
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

		public Builder setBackgroundId(Identifier id) {
			this.backgroundId = id;
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
			return new Panorama(name, backgroundId, music, new MovementSettings(frozen, addedX, addedY, speedMultiplier, woozy), weight);
		}

		public static MusicSound createMenuSound(SoundEvent event) {
			return new MusicSound(event, 20, 600, true);
		}

	}

}
