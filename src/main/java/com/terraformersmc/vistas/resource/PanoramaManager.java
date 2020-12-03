package com.terraformersmc.vistas.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.Vistas.Panorama;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PanoramaManager extends SinglePreparationResourceReloadListener<PanoramaManager.PanoramaList> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeAdapter(Panorama.class, new PanoramaDeserializer()).create();
    private static final TypeToken<Map<String, Panorama>> TYPE = new TypeToken<Map<String, Panorama>>() {
    };

    public PanoramaManager() {
    }

    @Override
    protected PanoramaManager.PanoramaList prepare(ResourceManager resourceManager, Profiler profiler) {
        PanoramaManager.PanoramaList panList = new PanoramaManager.PanoramaList();
        profiler.startTick();

        for (Iterator<String> var4 = resourceManager.getAllNamespaces().iterator(); var4.hasNext(); profiler.pop()) {
            String string = (String) var4.next();
            profiler.push(string);
            try {
                List<Resource> list = resourceManager.getAllResources(new Identifier(string, "panoramas.json"));
                for (Iterator<Resource> var7 = list.iterator(); var7.hasNext(); profiler.pop()) {
                    Resource resource = (Resource) var7.next();
                    profiler.push(resource.getResourcePackName());
                    try {
                        InputStream inputStream = resource.getInputStream();
                        Throwable var10 = null;
                        try {
                            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                            Throwable var12 = null;
                            try {
                                profiler.push("parse");
                                Map<String, Panorama> map = (Map<String, Panorama>) JsonHelper.deserialize(GSON, (Reader) reader, (TypeToken<Map<String, Panorama>>) TYPE);
                                profiler.swap("register");
                                Iterator<Entry<String, Panorama>> var14 = map.entrySet().iterator();

                                while (var14.hasNext()) {
                                    Entry<String, Panorama> entry = (Entry<String, Panorama>) var14.next();
                                    panList.register((Panorama) entry.getValue());
                                }
                                profiler.pop();
                            } catch (Throwable var41) {
                                var12 = var41;
                                throw var41;
                            } finally {
                                if (reader != null) {
                                    if (var12 != null) {
                                        try {
                                            reader.close();
                                        } catch (Throwable var40) {
                                            var12.addSuppressed(var40);
                                        }
                                    } else {
                                        reader.close();
                                    }
                                }
                            }
                        } catch (Throwable var43) {
                            var10 = var43;
                            throw var43;
                        } finally {
                            if (inputStream != null) {
                                if (var10 != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable var39) {
                                        var10.addSuppressed(var39);
                                    }
                                } else {
                                    inputStream.close();
                                }
                            }
                        }
                    } catch (RuntimeException var45) {
                        LOGGER.warn("Invalid panoramas.json in resourcepack: '{}'", resource.getResourcePackName(), var45);
                    }
                }
            } catch (IOException var46) {
            }
        }

        profiler.endTick();
        return panList;
    }

    @Override
    protected void apply(PanoramaManager.PanoramaList loader, ResourceManager manager, Profiler profiler) {
        Vistas.resourcePanoramas.clear();
        loader.loadedPanoramas.forEach((string, pan) -> {
            Panorama.addResourcePanorama(pan);
        });
        Panorama.relaodPanoramas();
    }

    @Environment(EnvType.CLIENT)
    public static class PanoramaList {
        private final Map<String, Panorama> loadedPanoramas = Maps.newHashMap();

        protected PanoramaList() {
        }

        private void register(Panorama entry) {
            loadedPanoramas.put(entry.getName(), entry);
        }
    }

}
