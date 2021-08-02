package com.terraformersmc.vistas.registry.panorama;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class TitleSettings {
	public static final Codec<TitleSettings> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Identifier.CODEC.optionalFieldOf("titleId").forGetter((titleSettings) -> {
			return Optional.ofNullable(titleSettings.titleId);
		}), Codec.BOOL.optionalFieldOf("showEdition").forGetter((titleSettings) -> {
			return Optional.of(titleSettings.showEdition);
		}), Codec.INT.optionalFieldOf("addedX").forGetter((titleSettings) -> {
			return Optional.of(titleSettings.addedX);
		}), Codec.INT.optionalFieldOf("addedY").forGetter((titleSettings) -> {
			return Optional.of(titleSettings.addedY);
		}), Codec.INT.optionalFieldOf("addedSplashX").forGetter((titleSettings) -> {
			return Optional.of(titleSettings.addedSplashX);
		}), Codec.INT.optionalFieldOf("addedSplashY").forGetter((titleSettings) -> {
			return Optional.of(titleSettings.addedSplashY);
		}), Codec.BOOL.optionalFieldOf("outlined").forGetter((titleSettings) -> {
			return Optional.of(titleSettings.outlined);
		})).apply(instance, (titleId, showEdition, addedX, addedY, addedSplashX, addedSplashY, outlined) -> new TitleSettings(titleId.orElse(null), showEdition.orElse(true), addedX.orElse(0), addedY.orElse(0), addedSplashX.orElse(0), addedSplashY.orElse(0), outlined.orElse(true)));
	});

	@Nullable
	public final Identifier titleId;

	public final boolean showEdition;

	public final int addedX;
	public final int addedY;

	public final int addedSplashX;
	public final int addedSplashY;

	public final boolean outlined;

	public TitleSettings() {
		this(null);
	}

	public TitleSettings(Identifier titleId) {
		this(titleId, true);
	}

	public TitleSettings(Identifier titleId, boolean showEdition) {
		this(titleId, showEdition, 0, 0);
	}

	public TitleSettings(Identifier titleId, boolean showEdition, int addedX, int addedY) {
		this(titleId, showEdition, addedX, addedY, 0, 0);
	}

	public TitleSettings(Identifier titleId, boolean showEdition, int addedX, int addedY, int addedSplashX, int addedSplashY) {
		this(titleId, showEdition, addedX, addedY, addedSplashX, addedSplashY, true);
	}

	public TitleSettings(Identifier titleId, boolean showEdition, int addedX, int addedY, int addedSplashX, int addedSplashY, boolean outlined) {
		this.titleId = titleId;
		this.showEdition = showEdition;
		this.addedX = addedX;
		this.addedY = addedY;
		this.addedSplashX = addedSplashX;
		this.addedSplashY = addedSplashY;
		this.outlined = outlined;
	}

}
