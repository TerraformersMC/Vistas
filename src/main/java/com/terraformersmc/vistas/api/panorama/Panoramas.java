package com.terraformersmc.vistas.api.panorama;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.config.PanoramaConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class Panoramas {
    public static Panorama getCurrent() {

        Panorama pickedPanorama = null;

        if (AutoConfig.getConfigHolder(PanoramaConfig.class) != null) {
            pickedPanorama = Vistas.panoramas.get(PanoramaConfig.getInstance().panorama);
        } else {
            Vistas.LOGGER.warn("Config not registered while trying for panorama");
        }

        if (pickedPanorama == null) {
            Vistas.LOGGER.warn("Config panorama null");
        }

        return pickedPanorama;
    }

    public static Panorama getRandom() {
        return Vistas.panoramas.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.panoramas.size())];
    }

    public static void setRandom() {
        if (!PanoramaConfig.getInstance().forcePanorama) {
            if (Vistas.panoramas.size() >= 1) {
                Panorama pan = getRandom();
                PanoramaConfig.getInstance().panorama = pan.getName();
                ((MinecraftClientAccess) MinecraftClient.getInstance()).setClientPanorama(pan);
            }
        }
    }

    public static void reload() {
        Vistas.panoramas.clear();
        Vistas.panoramas.putAll(Vistas.builtinPanoramas);
        Vistas.panoramas.putAll(Vistas.resourcePanoramas);
        setRandom();
    }

    public static void add(Panorama panorama) {
        Vistas.addBuiltInPanorama(panorama);
    }
}
