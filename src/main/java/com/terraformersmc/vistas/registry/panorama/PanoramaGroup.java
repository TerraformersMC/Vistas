package com.terraformersmc.vistas.registry.panorama;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.terraformersmc.vistas.registry.VistasRegistry;

import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class PanoramaGroup {
	public static final Codec<PanoramaGroup> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.optionalFieldOf("weight").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.weight);
		}), Codec.either(MusicSound.CODEC, SoundEvent.CODEC).optionalFieldOf("music").forGetter((panoramaGroup) -> {
			return Optional.of(Either.left(panoramaGroup.music));
		}), Identifier.CODEC.optionalFieldOf("splashTexts").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.splashTexts);
		}), Codec.list(SinglePanorama.CODEC).fieldOf("panoramas").forGetter((panoramaGroup) -> {
			return panoramaGroup.panoramas;
		})).apply(instance, (weight, music, splashTexts, array) -> new PanoramaGroup(weight.orElse(1), music.isPresent() ? music.get().left().orElse(VistasRegistry.getMenuSound(music.get().right().orElse(SoundEvents.MUSIC_MENU))) : MusicType.MENU, splashTexts.orElse(new Identifier("texts/splashes.txt")), array.toArray(new SinglePanorama[0])));
	});

	@Deprecated
	public static final Codec<PanoramaGroup> OLD_CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.optionalFieldOf("weight").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.weight);
		}), Identifier.CODEC.optionalFieldOf("splashTexts").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.splashTexts);
		}), SoundEvent.CODEC.optionalFieldOf("musicId").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.music.getSound());
		}), MusicSound.CODEC.optionalFieldOf("music").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.music);
		}), MovementSettings.CODEC.optionalFieldOf("movementSettings").forGetter((panoramaGroup) -> {
			if (panoramaGroup.panoramas.isEmpty()) {
				return Optional.of(MovementSettings.DEFAULT);
			}
			return Optional.of(panoramaGroup.panoramas.get(0).movementSettings);
		}), Codec.STRING.fieldOf("name").forGetter((panoramaGroup) -> {
			return panoramaGroup.name;
		}), Identifier.CODEC.fieldOf("panoramaId").forGetter((panoramaGroup) -> {
			if (panoramaGroup.panoramas.isEmpty()) {
				return new Identifier("textures/gui/title/background/panorama");
			}
			return panoramaGroup.panoramas.get(0).backgroundId;
		})).apply(instance, (weight, splashTexts, musicId, music, movementSettings, name, panoramaId) -> new PanoramaGroup(weight.orElse(1), musicId.isPresent() ? VistasRegistry.getMenuSound(musicId.get()) : music.orElse(MusicType.MENU), splashTexts.orElse(new Identifier("texts/splashes.txt")), name, new SinglePanorama(panoramaId, movementSettings.orElse(MovementSettings.DEFAULT))));
	});

	public static final PanoramaGroup DEFAULT = new PanoramaGroup(SinglePanorama.DEFAULT);

	public final ArrayList<SinglePanorama> panoramas;
	public final MusicSound music;
	public final Identifier splashTexts;
	public final int weight;
	@Nullable
	@Deprecated
	public final String name;

	public PanoramaGroup(SinglePanorama... panoramas) {
		this(1, panoramas);
	}

	public PanoramaGroup(int weight, SinglePanorama... panoramas) {
		this(weight, MusicType.MENU, panoramas);
	}

	public PanoramaGroup(int weight, MusicSound music, SinglePanorama... panoramas) {
		this(weight, music, new Identifier("texts/splashes.txt"), panoramas);
	}

	public PanoramaGroup(int weight, MusicSound music, Identifier splashTexts, SinglePanorama... panoramas) {
		this(weight, music, splashTexts, Lists.newArrayList(panoramas));
	}

	public PanoramaGroup(int weight, MusicSound music, Identifier splashTexts, String name, SinglePanorama... panoramas) {
		this(weight, music, splashTexts, Lists.newArrayList(panoramas), name);
	}

	public PanoramaGroup(int weight, MusicSound music, Identifier splashTexts, ArrayList<SinglePanorama> panoramas) {
		this(weight, music, splashTexts, panoramas, null);
	}

	public PanoramaGroup(int weight, MusicSound music, Identifier splashTexts, ArrayList<SinglePanorama> panoramas, String name) {
		this.panoramas = panoramas;
		this.music = music;
		this.splashTexts = splashTexts;
		this.weight = weight;
		this.name = name;
	}

	public void add(SinglePanorama panorama) {
		this.panoramas.add(panorama);
	}

	@Deprecated
	public boolean isDeprecated() {
		return this.name != null;
	}

}
