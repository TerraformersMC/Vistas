package net.ludocrypt.vistas.resource;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.vistas.Vistas.Panorama;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class PanoramaDeserializer implements JsonDeserializer<Panorama> {

	public Panorama deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

		JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
		String name = JsonHelper.getString(jsonObject, "name");
		String panoramaId = JsonHelper.getString(jsonObject, "panoramaId");
		String musicId = JsonHelper.getString(jsonObject, "musicId", "music.menu");
		int weight = JsonHelper.getInt(jsonObject, "weight", 1);

		return new Panorama(name, new Identifier(panoramaId), new Identifier(musicId), weight);
	}

}
