package com.terraformersmc.vistas.title;

import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;

public class BenignCubemapRenderer extends RotatingCubeMapRenderer {
	public BenignCubemapRenderer() {
		super(TitleScreen.PANORAMA_CUBE_MAP);
	}

	@Override
	public void render(float delta, float alpha) {

	}
}
