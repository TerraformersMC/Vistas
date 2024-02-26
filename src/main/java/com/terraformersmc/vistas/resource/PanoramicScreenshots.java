/*
 * This java file has been adapted from this file: https://github.com/liachmodded/runorama/blob/93316ed7df7140786092140b2d757af12a0ac039/src/code/java/com/github/liachmodded/runorama/Runorama.java
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.terraformersmc.vistas.resource;

import com.terraformersmc.vistas.Vistas;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanoramicScreenshots {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	public static final String PANORAMAS_PATH = "screenshots/panoramas";

	public static int cooldown = 0;

	public static void registerKeyBinding() {
		KeyBinding screenshotKey = new KeyBinding("key.vistas.panoramic_screenshot", 'H', "key.categories.misc");
		KeyBindingHelper.registerKeyBinding(screenshotKey);
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (cooldown > 0) {
				// 100 client tick cooldown between panoramas.
				--cooldown;
				return;
			}
			if (client.currentScreen == null && screenshotKey.isPressed()) {
				cooldown = 100;

				// Capture the largest square view.
				int size = Math.min(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());

				Text result = client.takePanorama(getPanoramicScreenshotFolder().toFile(), size, size);
				Vistas.LOGGER.info("Panorama capture with result: {}", result.getString());
				client.getMessageHandler().onGameMessage(result, false);
			}
		});
	}

	public static Path getPanoramicScreenshotFolder() {
		File rootFile = FabricLoader.getInstance().getGameDir().resolve(PANORAMAS_PATH).toFile();
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
