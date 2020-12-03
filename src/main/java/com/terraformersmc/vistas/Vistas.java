package com.terraformersmc.vistas;

import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.config.PanoramaConfig;
import com.terraformersmc.vistas.screenshot.PanoramicScreenshots;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class Vistas implements ClientModInitializer {

    public static final String MOD_ID = "vistas";

    public static Map<String, Panorama> builtinPanoramas = new HashMap<String, Panorama>();
    public static Map<String, Panorama> resourcePanoramas = new HashMap<String, Panorama>();
    public static Map<String, Panorama> panoramas = new HashMap<String, Panorama>();

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        PanoramaConfig.init();
        PanoramicScreenshots.registerKeyBinding();
    }

    public static class Panorama {

        private String name;
        private Identifier id;
        private MusicSound music;
        private int weight = 1;
        private MovementSettings movementSettings;

        public Panorama(String name, Identifier id, MusicSound music, MovementSettings movementSettings, int weight) {
            this.name = name;
            this.id = id;
            this.music = music;
            this.movementSettings = movementSettings;
            this.weight = weight;
        }

        public Panorama(String name, Identifier id, SoundEvent music, MovementSettings movementSettings, int weight) {
            this(name, id, createMenuSound(music), movementSettings, weight);
        }

        public String getName() {
            return name;
        }

        public Identifier getId() {
            return id;
        }

        public MusicSound getMusic() {
            return music;
        }

        public MovementSettings getMovementSettings() {
            return movementSettings;
        }

        public int getWeight() {
            return weight;
        }

        public static void addBuiltInPanorama(Panorama pan) {
            for (int i = 0; i < pan.getWeight(); i++) {
                builtinPanoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
            }
        }

        public static void addResourcePanorama(Panorama pan) {
            for (int i = 0; i < pan.getWeight(); i++) {
                resourcePanoramas.put(i > 1 ? pan.getName() + "_" + i : pan.getName(), pan);
            }
        }

        public static Panorama getPanorama() {

            Panorama pickedPanorama = null;

            if (AutoConfig.getConfigHolder(PanoramaConfig.class) != null) {
                pickedPanorama = Vistas.panoramas.get(PanoramaConfig.INSTANCE().panorama);
            } else {
                LOGGER.warn("Config not registered while trying for panorama");
            }

            if (pickedPanorama == null) {
                LOGGER.warn("Config panorama null");
            }

            return pickedPanorama;
        }

        public static Panorama getRandomPanorama() {
            return Vistas.panoramas.values().toArray(new Panorama[0])[new Random().nextInt(Vistas.panoramas.size())];
        }

        public static void setRandomPanorama() {
            if (!PanoramaConfig.INSTANCE().forcePanorama) {
                if (Vistas.panoramas.size() >= 1) {
                    Panorama pan = Panorama.getRandomPanorama();
                    PanoramaConfig.INSTANCE().panorama = pan.getName();
                    ((MinecraftClientAccess) MinecraftClient.getInstance()).setClientPanorama(pan);
                }
            }
        }

        public static void relaodPanoramas() {
            panoramas.clear();
            panoramas.putAll(builtinPanoramas);
            panoramas.putAll(resourcePanoramas);
            setRandomPanorama();
        }

        public static class MovementSettings {

            private boolean frozen;
            private float addedX;
            private float addedY;
            private float speedMultiplier;
            private boolean woozy;

            public MovementSettings(boolean frozen, float addedX, float addedY, float speedMultiplier, boolean woozy) {
                this.frozen = frozen;
                this.addedX = addedX;
                this.addedY = addedY;
                this.speedMultiplier = speedMultiplier;
                this.woozy = woozy;
            }

            public boolean isFrozen() {
                return frozen;
            }

            public float getAddedX() {
                return addedX;
            }

            public float getAddedY() {
                return addedY;
            }

            public float getSpeedMultiplier() {
                return speedMultiplier;
            }

            public boolean isWoozy() {
                return woozy;
            }

        }

    }

    public static MusicSound createMenuSound(SoundEvent event) {
        return new MusicSound(event, 20, 600, true);
    }
}
