package com.terraformersmc.vistas.resource;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.terraformersmc.vistas.api.panorama.MovementSettings;
import com.terraformersmc.vistas.api.panorama.Panorama;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class PanoramaDeserializer implements JsonDeserializer<Panorama> {

	public Panorama deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
		String name = JsonHelper.getString(jsonObject, "name");
		String panoramaId = JsonHelper.getString(jsonObject, "panoramaId");

		MusicSound music = deserializeMusicSound(jsonObject);

		if (jsonObject.has("musicId")) {
			String musicId = JsonHelper.getString(jsonObject, "musicId", "music.menu");
			music = Panorama.Builder.createMenuSound(new SoundEvent(new Identifier(musicId)));
		}

		MovementSettings movementSettings = deserializeMovementSettings(jsonObject);
		int weight = JsonHelper.getInt(jsonObject, "weight", 1);

		return new Panorama.Builder(name)
				.setId(new Identifier(panoramaId))
				.setMusic(music)
				.setFrozen(movementSettings.isFrozen())
				.setAddedX(movementSettings.getAddedX())
				.setAddedY(movementSettings.getAddedY())
				.setSpeedMultiplier(movementSettings.getSpeedMultiplier())
				.setWoozy(movementSettings.isWoozy())
				.setWeight(weight)
				.build();
	}

	public MovementSettings deserializeMovementSettings(JsonObject json) {

		if (json.has("movementSettings")) {

			JsonObject movementJson = json.getAsJsonObject("movementSettings");

			boolean frozen = JsonHelper.getBoolean(movementJson, "frozen", false);
			float addedX = JsonHelper.getFloat(movementJson, "addedX", 0.0F);
			float addedY = JsonHelper.getFloat(movementJson, "addedY", 0.0F);
			float speedMultiplier = JsonHelper.getFloat(movementJson, "speedMultiplier", 1.0F);
			boolean woozy = JsonHelper.getBoolean(movementJson, "woozy", false);

			return new MovementSettings(frozen, addedX, addedY, speedMultiplier, woozy);
		}

		return MovementSettings.DEFAULT;
	}

	public MusicSound deserializeMusicSound(JsonObject json) {

		if (json.has("musicSound")) {

			JsonObject musicSoundJson = json.getAsJsonObject("musicSound");

			String panoramaId = JsonHelper.getString(musicSoundJson, "sound");
			int min_delay = JsonHelper.getInt(musicSoundJson, "min_delay", 20);
			int max_delay = JsonHelper.getInt(musicSoundJson, "max_delay", 600);
			boolean replace_current_music = JsonHelper.getBoolean(musicSoundJson, "replace_current_music", true);

			return new MusicSound(new SoundEvent(new Identifier(panoramaId)), min_delay, max_delay, replace_current_music);
		}

		return Panorama.Builder.createMenuSound(SoundEvents.MUSIC_MENU);
	}

}
