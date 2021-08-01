package com.terraformersmc.vistas.util;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.terraformersmc.vistas.Vistas;

public class CodecJsonUtil {

	public static <R> Optional<DataResult<R>> getOptionalFromJsonCodec(Decoder<R> decoder, JsonElement jsonElement) {
		return Optional.of(decoder.parse(JsonOps.INSTANCE, jsonElement).setLifecycle(Lifecycle.stable()).map((object) -> {
			return object;
		}));
	}

	public static <R> R getFromJsonCodecOrNull(Decoder<R> decoder, JsonElement jsonElement, boolean log) {
		Optional<DataResult<R>> optional = getOptionalFromJsonCodec(decoder, jsonElement);
		if (optional.isPresent()) {
			R result = optional.get().result().orElse(null);
			if (result == null) {
				if (log) {
					Vistas.LOGGER.warn("Codec {} return {} from {}", decoder, result, jsonElement);
				}
			}
			return result;
		}
		if (log) {
			Vistas.LOGGER.warn("Codec {} return {} from {}", decoder, null, jsonElement);
		}
		return null;
	}

	public static <R> R getFromJsonCodecOrNull(Decoder<R> decoder, JsonElement jsonElement) {
		return getFromJsonCodecOrNull(decoder, jsonElement, false);
	}

	public static <R> R getFromCodecOrNull(Decoder<R> decoder, String jsonFlat) {
		return getFromJsonCodecOrNull(decoder, new JsonParser().parse(jsonFlat));
	}

}
