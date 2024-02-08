/*
 * This java file has been adapted from this file: https://github.com/liachmodded/runorama/blob/93316ed7df7140786092140b2d757af12a0ac039/src/code/java/com/github/liachmodded/runorama/Runorama.java
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.terraformersmc.vistas.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.terraformersmc.vistas.Vistas;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//TODO: rewrite; i dont know what im doing!
public class PanoramicScreenshots {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static final List<Quaternionf> ROTATIONS = ImmutableList.of(RotationAxis.POSITIVE_Y.rotationDegrees(0), RotationAxis.POSITIVE_Y.rotationDegrees(90), RotationAxis.POSITIVE_Y.rotationDegrees(180), RotationAxis.POSITIVE_Y.rotationDegrees(270), RotationAxis.POSITIVE_X.rotationDegrees(-90), RotationAxis.POSITIVE_X.rotationDegrees(90));

	public static final List<Float> PITCHES = ImmutableList.of(0.0F, 0.0F, 0.0F, 0.0F, 90.0F, -90.0F);
	public static final List<Float> YAWS = ImmutableList.of(0.0F, 90.0F, 180.0F, -90.0F, 0.0F, 0.0F);

	public static double time = 0.0D;
	public static double timeSinceLastKeyPress = -1.0D;
	public static boolean needsScreenshot = false;
	public static int onShot = -1;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static Optional<Pair<Float, Float>> startingRotation = Optional.empty();
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static Optional<Path> currentScreenshotPath = Optional.empty();

	public static void registerKeyBinding() {
		KeyBinding screenshotKey = new KeyBinding("key.vistas.panoramic_screenshot", 'H', "key.categories.misc");
		KeyBindingHelper.registerKeyBinding(screenshotKey);
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.currentScreen == null && screenshotKey.isPressed() && timeSinceLastKeyPress <= 0.0D) {
				needsScreenshot = true;
				onShot++;
				timeSinceLastKeyPress = 5.0D;
			}
		});
	}

	public static void saveScreenshot(NativeImage screenshot, Path folder, int i) {
		Util.getIoWorkerExecutor().execute(() -> {
			try (screenshot) {
				int width = screenshot.getWidth();
				int height = screenshot.getHeight();
				int x = 0;
				int y = 0;
				if (width > height) {
					x = (width - height) / 2;
					//noinspection SuspiciousNameCombination
					width = height;
				} else {
					y = (height - width) / 2;
					//noinspection SuspiciousNameCombination
					height = width;
				}
				NativeImage saved = new NativeImage(width, height, false);
				screenshot.resizeSubRectTo(x, y, width, height, saved);
				saved.writeTo(folder.resolve("panorama_" + i + ".png"));
			} catch (IOException exception) {
				Vistas.LOGGER.warn("Couldn't save screenshot", exception);
			}
		});
	}

	public static Path getPanoramicScreenshotFolder() {
		if (currentScreenshotPath.isPresent()) {
			return currentScreenshotPath.get();
		}
		File rootFile = FabricLoader.getInstance().getGameDir().resolve("screenshots/panoramas/").toFile();
		if (!rootFile.exists()) {
			//noinspection ResultOfMethodCallIgnored
			rootFile.mkdirs();
		}
		String string = DATE_FORMAT.format(new Date());
		int i = 1;

		while (true) {
			File file = new File(rootFile, string + (i == 1 ? "" : "_" + i));
			if (!file.exists()) {
				//noinspection ResultOfMethodCallIgnored
				file.mkdir();
				return file.toPath();
			}

			++i;
		}
	}
}
