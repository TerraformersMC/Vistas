package com.terraformersmc.vistas.registry.panorama;

import java.util.ArrayList;
import java.util.Optional;

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

	public static final PanoramaGroup DEFAULT = new PanoramaGroup(SinglePanorama.DEFAULT);

	public final ArrayList<SinglePanorama> panoramas;
	public final MusicSound music;
	public final Identifier splashTexts;
	public final int weight;

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

	public PanoramaGroup(int weight, MusicSound music, Identifier splashTexts, ArrayList<SinglePanorama> panoramas) {
		this.panoramas = panoramas;
		this.music = music;
		this.splashTexts = splashTexts;
		this.weight = weight;
	}

	public void add(SinglePanorama panorama) {
		this.panoramas.add(panorama);
	}

}
