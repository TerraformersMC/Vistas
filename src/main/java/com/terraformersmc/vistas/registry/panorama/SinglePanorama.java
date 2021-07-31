package com.terraformersmc.vistas.registry.panorama;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class SinglePanorama {
	public static final Codec<SinglePanorama> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Identifier.CODEC.optionalFieldOf("backgroundId").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.backgroundId);
		}), MovementSettings.CODEC.optionalFieldOf("movementSettings").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.movementSettings);
		}), VisualSettings.CODEC.optionalFieldOf("visualSettings").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.visualSettings);
		})).apply(instance, (background, movement, visual) -> new SinglePanorama(background.orElse(new Identifier("textures/gui/title/background/panorama")), movement.orElse(MovementSettings.DEFAULT), visual.orElse(VisualSettings.DEFAULT)));
	});

	public static final SinglePanorama DEFAULT = new SinglePanorama();

	public final Identifier backgroundId;
	public final MovementSettings movementSettings;
	public final VisualSettings visualSettings;

	public SinglePanorama() {
		this(new Identifier("textures/gui/title/background/panorama"));
	}

	public SinglePanorama(Identifier backgroundId) {
		this(backgroundId, MovementSettings.DEFAULT);
	}

	public SinglePanorama(Identifier backgroundId, MovementSettings movementSettings) {
		this(backgroundId, movementSettings, VisualSettings.DEFAULT);
	}

	public SinglePanorama(Identifier backgroundId, MovementSettings movementSettings, VisualSettings visualSettings) {
		this.backgroundId = backgroundId;
		this.movementSettings = movementSettings;
		this.visualSettings = visualSettings;
	}

}
