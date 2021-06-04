package com.terraformersmc.vistas.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.api.panorama.Panorama;
import com.terraformersmc.vistas.api.panorama.Panoramas;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

public class PanoramaManager extends SinglePreparationResourceReloader<HashMap<String, Panorama>> {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson PANORAMA_GSON = new GsonBuilder().registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter(Panorama.class, new PanoramaDeserializer()).create();
	private static final TypeToken<HashMap<String, Panorama>> PANORAMA_TOKEN = new TypeToken<HashMap<String, Panorama>>() {
		// Nothing
	};

	@Override
	protected HashMap<String, Panorama> prepare(ResourceManager resourceManager, Profiler profiler) {
		HashMap<String, Panorama> loadedPanoramas = Maps.newHashMap();
		profiler.startTick();

		for (Iterator<String> namespaceIterator = resourceManager.getAllNamespaces().iterator(); namespaceIterator.hasNext(); profiler.pop()) {
			String loadedNamespace = namespaceIterator.next();
			profiler.push(loadedNamespace);
			try {
				List<Resource> panoramasJson = resourceManager.getAllResources(new Identifier(loadedNamespace, "panoramas.json"));
				for (Iterator<Resource> panoramas = panoramasJson.iterator(); panoramas.hasNext(); profiler.pop()) {
					Resource resourcePack = panoramas.next();
					profiler.push(resourcePack.getResourcePackName());
					try {
						InputStream resourcePackFiles = resourcePack.getInputStream();
						Throwable throwable = null;
						try {
							Reader resourcePackReader = new InputStreamReader(resourcePackFiles, StandardCharsets.UTF_8);
							Throwable badParse = null;
							try {
								profiler.push("parse");
								HashMap<String, Panorama> panoramasMap = JsonHelper.deserialize(PANORAMA_GSON, resourcePackReader, PANORAMA_TOKEN);
								profiler.swap("register");
								Iterator<Entry<String, Panorama>> panoramasIterator = panoramasMap.entrySet().iterator();

								while (panoramasIterator.hasNext()) {
									Entry<String, Panorama> entryPanorama = panoramasIterator.next();
									loadedPanoramas.put(entryPanorama.getValue().getName(), entryPanorama.getValue());
								}
								profiler.pop();
							} catch (Throwable caughtParsing) {
								badParse = caughtParsing;
								throw caughtParsing;
							} finally {
								if (resourcePackReader != null) {
									if (badParse != null) {
										try {
											resourcePackReader.close();
										} catch (Throwable badClose) {
											badParse.addSuppressed(badClose);
										}
									} else {
										resourcePackReader.close();
									}
								}
							}
						} catch (Throwable inputThrown) {
							throwable = inputThrown;
							throw inputThrown;
						} finally {
							if (resourcePackFiles != null) {
								if (throwable != null) {
									try {
										resourcePackFiles.close();
									} catch (Throwable var39) {
										throwable.addSuppressed(var39);
									}
								} else {
									resourcePackFiles.close();
								}
							}
						}
					} catch (RuntimeException runtimeFail) {
						LOGGER.warn("Invalid panoramas.json in resourcepack: '{}'", resourcePack.getResourcePackName(), runtimeFail);
					}
				}
			} catch (IOException resourcesEmpty) {
			}
		}

		profiler.endTick();
		return loadedPanoramas;
	}

	@Override
	protected void apply(HashMap<String, Panorama> loadedPanoramas, ResourceManager manager, Profiler profiler) {
		Vistas.RESOURCE_PANORAMAS.clear();
		loadedPanoramas.forEach((string, pan) -> {
			Vistas.addResourcePanorama(pan);
		});
		Panoramas.reload();
	}

}
