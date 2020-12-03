/*
 * This java file has been adapted from this file: https://github.com/liachmodded/runorama/blob/93316ed7df7140786092140b2d757af12a0ac039/src/code/java/com/github/liachmodded/runorama/Runorama.java
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.terraformersmc.vistas.screenshot;

import com.google.common.collect.ImmutableList;
import com.terraformersmc.vistas.Vistas;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class PanoramicScreenshots {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    public static final Logger LOGGER = LogManager.getLogger(Vistas.MOD_ID + "|PanoramicScreenshots");
    /**
     * The rotations for the 6 sides of the rendered panorama.
     */
    public static final List<Quaternion> ROTATIONS = ImmutableList.of(
            Vector3f.POSITIVE_Y.getDegreesQuaternion(0),
            Vector3f.POSITIVE_Y.getDegreesQuaternion(90),
            Vector3f.POSITIVE_Y.getDegreesQuaternion(180),
            Vector3f.POSITIVE_Y.getDegreesQuaternion(270),
            Vector3f.POSITIVE_X.getDegreesQuaternion(-90),
            Vector3f.POSITIVE_X.getDegreesQuaternion(90)
    );
    /**
     * Whether to take a screenshot the next time a frame is rendered.
     */
    public static boolean needsScreenshot = false;


    public static void registerKeyBinding() {
        KeyBinding screenshotKey = new KeyBinding("key.vistas.panoramic_screenshot", 'H', "key.categories.misc");
        KeyBindingHelper.registerKeyBinding(screenshotKey);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null && screenshotKey.isPressed()) {
                needsScreenshot = true;
            }
        });
    }

    /**
     * Save a partial screenshot for a panorama.
     *
     * @param screenshot the image
     * @param folder     the panorama folder
     * @param i          the face index
     */
    public static void saveScreenshot(NativeImage screenshot, Path folder, int i) {
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                int width = screenshot.getWidth();
                int height = screenshot.getHeight();
                int int_3 = 0;
                int int_4 = 0;
                if (width > height) {
                    int_3 = (width - height) / 2;
                    width = height;
                } else {
                    int_4 = (height - width) / 2;
                    height = width;
                }
                NativeImage saved = new NativeImage(width, height, false);
                screenshot.resizeSubRectTo(int_3, int_4, width, height, saved);
                saved.writeFile(folder.resolve("panorama_" + i + ".png"));
            } catch (IOException var27) {
                PanoramicScreenshots.LOGGER.warn("Couldn't save screenshot", var27);
            } finally {
                screenshot.close();
            }
        });
    }

    public static Path getPanoramicScreenshotFolder() {
        File rootFile = FabricLoader.getInstance().getGameDir().resolve("screenshots/panoramas/").toFile();
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        String string = DATE_FORMAT.format(new Date());
        int i = 1;

        while (true) {
            File file = new File(rootFile, string + (i == 1 ? "" : "_" + i));
            if (!file.exists()) {
                file.mkdir();
                return file.toPath();
            }

            ++i;
        }
    }

}
