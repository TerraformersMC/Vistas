package com.terraformersmc.archive.vistas.api.panorama;

import com.google.common.base.Function;

import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
@Deprecated
public class Panorama {

	public static final Panorama DEFAULT = new Panorama.Builder("minecraft").build();

	private String name;
	private Identifier backgroundId;
	private MusicSound music;
	private int weight = 1;
	private MovementSettings movementSettings;
	private Identifier splashTexts;

	private Panorama(String name, Identifier id, MusicSound music, MovementSettings movementSettings, int weight, Identifier splashTexts) {
		this.name = name;
		this.backgroundId = id;
		this.music = music;
		this.movementSettings = movementSettings;
		this.weight = weight;
		this.splashTexts = splashTexts;
	}

	private Panorama(String name, Identifier id, MusicSound music, MovementSettings movementSettings, int weight) {
		this(name, id, music, movementSettings, weight, new Identifier("texts/splashes.txt"));
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

	public Identifier getSplashTexts() {
		return splashTexts;
	}

	public static class Builder {

		private String name;
		private Identifier backgroundId = new Identifier("textures/gui/title/background/panorama");
		private MusicSound music = createMenuSound(SoundEvents.MUSIC_MENU);
		private int weight = 1;
		private Identifier splashTexts = new Identifier("texts/splashes.txt");

		private boolean frozen = false;
		private float addedX = 0.0F;
		private float addedY = 0.0F;
		private float speedMultiplier = 1.0F;
		private boolean woozy = false;

		private boolean useXEquation = false;
		private Function<Float, Float> XEquation = (time) -> {
			return 0.0F;
		};
		private boolean useYEquation = false;
		private Function<Float, Float> YEquation = (time) -> {
			return 0.0F;
		};

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

		public Builder setUseXEquation(boolean useXEquation) {
			this.useXEquation = useXEquation;
			return this;
		}

		public Builder setUseYEquation(boolean useYEquation) {
			this.useYEquation = useYEquation;
			return this;
		}

		public Builder setXEquation(Function<Float, Float> xEquation) {
			XEquation = xEquation;
			useXEquation = true;
			return this;
		}

		public Builder setYEquation(Function<Float, Float> yEquation) {
			YEquation = yEquation;
			useYEquation = true;
			return this;
		}

		public Builder setSplashTexts(Identifier splashTexts) {
			this.splashTexts = splashTexts;
			return this;
		}

		public Panorama build() {
			return new Panorama(name, backgroundId, music, new MovementSettings(frozen, addedX, addedY, speedMultiplier, woozy, useXEquation, useYEquation, XEquation, YEquation), weight, splashTexts);
		}

		public static MusicSound createMenuSound(SoundEvent event) {
			return new MusicSound(event, 20, 600, true);
		}

	}

}
