package com.terraformersmc.vistas.title;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.panorama.Panorama;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.profiler.Profiler;

public class VistasTitle {

	public static final ConcurrentMap<Identifier, Panorama> BUILTIN_PANORAMAS = Maps.newConcurrentMap();
	public static final ConcurrentMap<Identifier, Panorama> PANORAMAS = Maps.newConcurrentMap();
	public static final ConcurrentMap<Panorama, Identifier> PANORAMAS_INVERT = Maps.newConcurrentMap();
	public static final List<Panorama> DISTRIBUTION = Lists.newArrayList();
	public static final MutableObject<Panorama> CURRENT = new MutableObject<Panorama>(Panorama.DEFAULT);

	public static void choose() {
		choose(MinecraftClient.getInstance().getProfiler());
	}

	public static void choose(Profiler profiler) {
		profiler.startTick();
		profiler.push("set");
		if (VistasConfig.getInstance().forcePanorama) {
			profiler.push("force");
			try {
				Panorama panorama = VistasTitle.PANORAMAS.get(new Identifier(VistasConfig.getInstance().panorama));
				if (panorama == null) {
					throw new NullPointerException();
				}
				VistasTitle.CURRENT.setValue(panorama);
			} catch (InvalidIdentifierException badId) {
				Vistas.LOGGER.warn("String: '{}' is an invalid Identifier in config; resetting...", VistasConfig.getInstance().panorama);
				VistasConfig.getInstance().panorama = Vistas.DEFAULT.toString();
				VistasTitle.CURRENT.setValue(Panorama.DEFAULT);
			} catch (NullPointerException nullPanorama) {
				Vistas.LOGGER.warn("String: '{}' is an unregistered Panorama in config; resetting...", VistasConfig.getInstance().panorama);
				VistasConfig.getInstance().panorama = Vistas.DEFAULT.toString();
				VistasTitle.CURRENT.setValue(Panorama.DEFAULT);
			}
			profiler.pop();
		} else if (VistasConfig.getInstance().randomPerScreen) {
			profiler.push("random");
			VistasTitle.CURRENT.setValue(VistasTitle.getRandom());
			profiler.pop();
		}
		profiler.pop();
		profiler.endTick();

	}

	// Pseudo-random
	public static Panorama getRandom() {
		if (!PANORAMAS.isEmpty()) {
			Random rand = new Random();

			int total = 0;
			for (Panorama panorama : DISTRIBUTION) {
				total += panorama.getWeight();
			}

			List<Panorama> panoramas = Lists.newArrayList(DISTRIBUTION);
			Collections.shuffle(panoramas, rand);

			for (Panorama panorama : DISTRIBUTION) {
				if (rand.nextInt(total) < panorama.getWeight()) {
					return panorama;
				} else {
					total -= panorama.getWeight();
				}
			}

			return DISTRIBUTION.get(rand.nextInt(DISTRIBUTION.size()));
		}
		return Panorama.DEFAULT;
	}

	public static void register(Identifier id, Panorama panorama) {
		PANORAMAS.put(id, panorama);
		PANORAMAS_INVERT.put(panorama, id);
		DISTRIBUTION.add(panorama);
	}

	public static void deRegister(Identifier id) {
		Panorama panorama = PANORAMAS.get(id);
		PANORAMAS.remove(id);
		PANORAMAS_INVERT.remove(panorama);
		DISTRIBUTION.remove(panorama);
	}

	public static void clear() {
		PANORAMAS.clear();
		PANORAMAS_INVERT.clear();
		DISTRIBUTION.clear();
	}

}
