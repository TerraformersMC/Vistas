package com.terraformersmc.vistas.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.Panorama;
import com.terraformersmc.vistas.title.VistasTitle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.profiler.Profiler;

public class PanoramaResourceReloader extends SinglePreparationResourceReloader<HashMap<Identifier, Pair<Panorama, List<String>>>> {

	private final ConcurrentMap<Identifier, Pair<List<String>, List<Identifier>>> web = Maps.newConcurrentMap();
	private final ConcurrentMap<Identifier, Pair<List<String>, List<Identifier>>> parsedSplashWeb = Maps.newConcurrentMap();
	private final ConcurrentMap<Identifier, List<String>> splashTexts = Maps.newConcurrentMap();

	@Override
	protected HashMap<Identifier, Pair<Panorama, List<String>>> prepare(ResourceManager manager, Profiler profiler) {
		profiler.startTick();
		HashMap<Identifier, Panorama> panoramas = Maps.newHashMap();
		for (String namespace : manager.getAllNamespaces()) {
			profiler.push(namespace);
			try {
				for (Resource resource : manager.getAllResources(new Identifier(namespace, "panoramas.json"))) {
					profiler.push(resource.getResourcePackName());
					try {
						InputStream inputStream = resource.getInputStream();
						try {
							InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
							try {
								profiler.push("parse");

								JsonElement jsonElement = JsonParser.parseReader(reader);
								jsonElement.getAsJsonObject().entrySet().forEach((pair) -> {
									Identifier panoramaId = new Identifier(namespace, pair.getKey());
									Panorama panorama = get(Panorama.CODEC, pair.getValue());
									if (panorama != null) {
										panoramas.put(panoramaId, panorama);
										Pair<List<String>, List<Identifier>> splashes = prepare(panorama.getSplashText(), manager, profiler);
										web.put(panoramaId, splashes);
										if (!parsedSplashWeb.containsKey(panorama.getSplashText())) {
											parsedSplashWeb.put(panorama.getSplashText(), splashes);
										}
									} else {
										throw new RuntimeException();
									}
								});

								profiler.pop();
							} catch (Throwable throwable) {
								try {
									reader.close();
								} catch (Throwable closeBreak) {
									throwable.addSuppressed(closeBreak);
								}
								throw throwable;
							}
							reader.close();
						} catch (Throwable throwable) {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (Throwable closeBreak) {
									throwable.addSuppressed(closeBreak);
								}
							}
							throw throwable;
						}
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (RuntimeException runtimeBreak) {
						Vistas.LOGGER.warn("Invalid panoramas.json in resourcepack: '{}'", resource.getResourcePackName(), runtimeBreak);
					}
					profiler.pop();
				}
			} catch (IOException exception) {
				// No panoramas
			}
			profiler.pop();
		}
		prepareSplash(manager, profiler);
		HashMap<Identifier, Pair<Panorama, List<String>>> panoramaMap = Maps.newHashMap();
		panoramas.forEach((panoramaId, panorama) -> panoramaMap.put(panoramaId, Pair.of(panorama, this.splashTexts.get(panoramaId))));
		profiler.endTick();
		return panoramaMap;
	}

	protected Pair<List<String>, List<Identifier>> prepare(Identifier splashId, ResourceManager manager, Profiler profiler) {
		if (this.parsedSplashWeb.containsKey(splashId)) {
			return this.parsedSplashWeb.get(splashId);
		}
		List<String> splashTexts = Lists.newArrayList();
		List<Identifier> imports = Lists.newArrayList();

		profiler.push(splashId.toString());
		try {
			profiler.push("parse");
			Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(splashId);
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
				try {
					splashTexts = Lists.newArrayList(bufferedReader.lines().map(String::trim).map((splash) -> {
						if (splash.startsWith("$vistas$import$")) {
							try {
								imports.add(new Identifier(splash.substring(15)));
							} catch (InvalidIdentifierException badId) {
								Vistas.LOGGER.error("Splash: '{}' imports invalid Identifier: '{}'", splashId, splash.substring(15));
							}
						}
						Session session = MinecraftClient.getInstance().getSession();
						splash = splash.replace("$vistas$name$", session.getUsername().toLowerCase(Locale.ROOT));
						splash = splash.replace("$vistas$Name$", session.getUsername());
						splash = splash.replace("$vistas$NAME$", session.getUsername().toUpperCase(Locale.ROOT));
						return splash;
					}).filter((splash) -> splash.hashCode() != 125780783 && !splash.startsWith("$vistas$import$")).toList());
				} catch (Throwable throwable) {
					try {
						bufferedReader.close();
					} catch (Throwable closeable) {
						throwable.addSuppressed(closeable);
					}

					throw throwable;
				}

				bufferedReader.close();
			} catch (Throwable throwable) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable closeable) {
						throwable.addSuppressed(closeable);
					}
				}

				throw throwable;
			}

			if (resource != null) {
				resource.close();
			}
			profiler.pop();
		} catch (IOException exception) {
			Vistas.LOGGER.error("Splash: '{}' doesn't exist!", splashId);
		}
		profiler.pop();

		return Pair.of(splashTexts, imports);
	}

	protected void prepareSplash(ResourceManager manager, Profiler profiler) {
		profiler.push("splash");

		this.web.forEach((panoramaId, pair) -> {
			List<String> definedSplashes = Lists.newArrayList(pair.getFirst());
			List<Identifier> seenImports = Lists.newArrayList(panoramaId);
			iterateImports(panoramaId, pair.getSecond(), seenImports, definedSplashes);
			this.splashTexts.put(panoramaId, definedSplashes);
		});

		profiler.pop();
	}

	protected void iterateImports(Identifier panoramaId, List<Identifier> imports, List<Identifier> seenImports, List<String> addTo) {
		imports.forEach((importId) -> {
			if (!seenImports.contains(importId)) {
				seenImports.add(importId);
				Pair<List<String>, List<Identifier>> importPair = this.web.get(importId);
				if (importPair != null) {
					addTo.addAll(importPair.getFirst());
					iterateImports(panoramaId, importPair.getSecond(), seenImports, addTo);
				} else {
					Vistas.LOGGER.error("Panorama: '{}' imports unregistered Panorama: '{}'", panoramaId, importId);
				}
			}
		});
	}

	@Override
	protected void apply(HashMap<Identifier, Pair<Panorama, List<String>>> prepared, ResourceManager manager, Profiler profiler) {
		profiler.startTick();

		profiler.push("clear");
		VistasTitle.clear();
		this.web.clear();
		this.parsedSplashWeb.clear();
		this.splashTexts.clear();
		profiler.pop();

		profiler.push("register");
		prepared.forEach((panoramaId, pair) -> {
			VistasTitle.register(panoramaId, pair.getFirst());
			this.splashTexts.put(panoramaId, pair.getSecond());
		});

		profiler.push("builtin");
		VistasTitle.BUILTIN_PANORAMAS.forEach(VistasTitle::register);
		profiler.pop();

		profiler.pop();

		VistasTitle.choose(profiler);

		profiler.endTick();
	}

	public String get() {
		Identifier panoramaId = VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.getValue());

		if (panoramaId != null) {
			List<String> list = this.splashTexts.get(panoramaId);
			if (list != null && !list.isEmpty()) {
				return list.get(new Random().nextInt(list.size()));
			}
		}

		return "missingno";
	}

	public static <R> R get(Decoder<R> decoder, JsonElement jsonElement) throws NullPointerException {
		return Optional.of(decoder.parse(JsonOps.INSTANCE, jsonElement)).get().result().get();
	}

}
