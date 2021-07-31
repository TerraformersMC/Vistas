package com.terraformersmc.vistas.util;

import com.terraformersmc.vistas.access.RotatingCubeMapRendererAccess;
import com.terraformersmc.vistas.registry.panorama.SinglePanorama;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

public class RotatingPanoramicRenderer extends RotatingCubeMapRenderer {

	public final SinglePanorama panorama;

	public RotatingPanoramicRenderer(PanoramicRenderer cubeMap, float time) {
		super(cubeMap);
		this.panorama = cubeMap.panorama;
		RotatingCubeMapRendererAccess.get(this).setTime(time);
	}

	public static class PanoramicRenderer extends CubeMapRenderer {

		public final SinglePanorama panorama;

		public PanoramicRenderer(SinglePanorama panorama) {
			super(panorama.backgroundId);
			this.panorama = panorama;
		}

	}

}
