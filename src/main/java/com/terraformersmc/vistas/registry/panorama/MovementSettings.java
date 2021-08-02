package com.terraformersmc.vistas.registry.panorama;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class MovementSettings {
	public static final Codec<MovementSettings> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.BOOL.optionalFieldOf("frozen").forGetter((movementSettings) -> {
			return Optional.of(movementSettings.frozen);
		}), Codec.FLOAT.optionalFieldOf("addedX").forGetter((movementSettings) -> {
			return Optional.of(movementSettings.addedX);
		}), Codec.FLOAT.optionalFieldOf("addedY").forGetter((movementSettings) -> {
			return Optional.of(movementSettings.addedY);
		}), Codec.FLOAT.optionalFieldOf("speedMultiplier").forGetter((movementSettings) -> {
			return Optional.of(movementSettings.speedMultiplier);
		}), Codec.BOOL.optionalFieldOf("woozy").forGetter((movementSettings) -> {
			return Optional.of(movementSettings.woozy);
		})).apply(instance, (frozen, addedX, addedY, speedMultiplier, woozy) -> new MovementSettings(frozen.orElse(false), addedX.orElse(0.0F), addedY.orElse(0.0F), speedMultiplier.orElse(1.0F), woozy.orElse(false)));
	});

	public static final MovementSettings DEFAULT = new MovementSettings();

	public final boolean frozen;
	public final float addedX;
	public final float addedY;
	public final float speedMultiplier;
	public final boolean woozy;

	@Nullable
	public final Function<Float, Float> xEquation;

	@Nullable
	public final Function<Float, Float> yEquation;

	public MovementSettings() {
		this(false, 0.0F, 0.0F, false);
	}

	public MovementSettings(boolean frozen, float addedX, float addedY, boolean woozy) {
		this(frozen, addedX, addedY, 1.0F, woozy);
	}

	public MovementSettings(boolean frozen, float addedX, float addedY, float speedMultiplier, boolean woozy) {
		this(frozen, addedX, addedY, speedMultiplier, woozy, null, null);
	}

	public MovementSettings(boolean frozen, float addedX, float addedY, float speedMultiplier, boolean woozy, Function<Float, Float> xEquation, Function<Float, Float> yEquation) {
		this.frozen = frozen;
		this.addedX = addedX;
		this.addedY = addedY;
		this.speedMultiplier = speedMultiplier;
		this.woozy = woozy;
		this.xEquation = xEquation;
		this.yEquation = yEquation;
	}

	public boolean isUsingXEquation() {
		return this.xEquation != null;
	}

	public boolean isUsingYEquation() {
		return this.yEquation != null;
	}

}
