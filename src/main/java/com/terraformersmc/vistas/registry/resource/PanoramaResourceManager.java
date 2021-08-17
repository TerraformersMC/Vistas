package com.terraformersmc.vistas.registry.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.access.SimpleRegistryAccess;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;
import com.terraformersmc.vistas.util.CodecJsonUtil;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class PanoramaResourceManager extends SinglePreparationResourceReloader<HashMap<Identifier, PanoramaGroup>> {
	public final HashMap<Identifier, PanoramaGroup> panoramas = Maps.newHashMap();

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected HashMap<Identifier, PanoramaGroup> prepare(ResourceManager manager, Profiler profiler) {
		profiler.startTick();
		panoramas.clear();
		for (Iterator<String> var4 = manager.getAllNamespaces().iterator(); var4.hasNext(); profiler.pop()) {
			String string = var4.next();
			profiler.push(string);
			try {
				List<Resource> list = manager.getAllResources(new Identifier(string, "panoramas.json"));
				for (Iterator<Resource> var7 = list.iterator(); var7.hasNext(); profiler.pop()) {
					Resource resource = var7.next();
					profiler.push(resource.getResourcePackName());
					try {
						InputStream inputStream = resource.getInputStream();
						try {
							InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
							try {
								profiler.push("parse");

								JsonElement jsonElement = new JsonParser().parse(reader);
								jsonElement.getAsJsonObject().entrySet().forEach((pair) -> {
									Identifier id = new Identifier(string, pair.getKey());
									PanoramaGroup panGroup = CodecJsonUtil.getFromJsonCodecOrNull(PanoramaGroup.CODEC, pair.getValue());
									if (panGroup != null) {
										add(id, panGroup);
									} else {
										Vistas.LOGGER.warn("ResourcePack {} is using outdated panoramas.json at {}, this will be unsupported in later versions, updated quickly!", resource.getResourcePackName(), id);
										PanoramaGroup deprecatedPanGroup = CodecJsonUtil.getFromJsonCodecOrNull(PanoramaGroup.OLD_CODEC, pair.getValue());
										add(new Identifier(deprecatedPanGroup.name), deprecatedPanGroup);
									}
								});

								profiler.pop();
							} catch (Throwable var16) {
								try {
									reader.close();
								} catch (Throwable var15) {
									var16.addSuppressed(var15);
								}
								throw var16;
							}
							reader.close();
						} catch (Throwable var17) {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (Throwable var14) {
									var17.addSuppressed(var14);
								}
							}
							throw var17;
						}
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (RuntimeException var18) {
						Vistas.LOGGER.warn("Invalid panoramas.json in resourcepack: '{}'", resource.getResourcePackName(), var18);
					}
				}
			} catch (IOException var19) {
			}
		}
		HashMap<Identifier, PanoramaGroup> clonedPanoramas = (HashMap<Identifier, PanoramaGroup>) panoramas.clone();
		this.apply(clonedPanoramas, manager, profiler);
		profiler.endTick();
		return clonedPanoramas;
	}

	@Override
	protected void apply(HashMap<Identifier, PanoramaGroup> prepared, ResourceManager manager, Profiler profiler) {
		SimpleRegistryAccess.get(VistasRegistry.PANORAMA_REGISTRY).clearEntries();
		this.panoramas.clear();
		this.panoramas.putAll(prepared);
		VistasRegistry.registerApiPanoramas();
		this.panoramas.forEach((id, group) -> {
			if (VistasRegistry.PANORAMA_REGISTRY.containsId(id)) {
				Registry.register(VistasRegistry.PANORAMA_REGISTRY, VistasRegistry.PANORAMA_REGISTRY.getRawId(VistasRegistry.PANORAMA_REGISTRY.get(id)), id.toString(), group);
			} else {
				Registry.register(VistasRegistry.PANORAMA_REGISTRY, id, group);
			}
		});
		VistasRegistry.setCurrentPanorama(VistasRegistry.getChosenPanorama());
	}

	protected void add(Identifier id, PanoramaGroup panGroup) {
		if (!panoramas.containsKey(id)) {
			panoramas.put(id, panGroup);
		} else {
			panoramas.remove(id);
			panoramas.put(id, panGroup);
		}
	}

}
