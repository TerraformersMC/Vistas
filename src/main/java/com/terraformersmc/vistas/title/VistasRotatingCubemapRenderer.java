package com.terraformersmc.vistas.title;

import com.terraformersmc.vistas.Vistas;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class VistasRotatingCubemapRenderer extends RotatingCubeMapRenderer {
	private final MinecraftClient client;

	public VistasRotatingCubemapRenderer(CubeMapRenderer defaultRenderer) {
		super(defaultRenderer);

		this.client = MinecraftClient.getInstance();
	}

	@Override
	public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
		VistasCubemapRenderer.time += tickDelta;

		VistasTitle.CURRENT.getValue().getCubemaps().forEach((cubemap) -> {
			VistasCubemapRenderer panoramaRenderer = new VistasCubemapRenderer(cubemap);
			Identifier overlayId = panoramaRenderer.getCubemap().getCubemapId().withSuffixedPath("_overlay.png");

			context.draw();
			panoramaRenderer.draw(this.client, alpha);

			if (this.client.getResourceManager().getResource(overlayId).isPresent()) {
				context.draw();
				context.drawTexture(RenderLayer::getGuiTextured, overlayId, 0, 0, 0.0f, 0.0f, width, height, 16, 128, 16, 128, ColorHelper.getWhite(alpha));
			}
		});
	}

	public static void registerTextures(TextureManager textureManager, ResourceManager resourceManager) {
		VistasTitle.PANORAMAS.values().forEach(panorama ->
			panorama.getCubemaps().forEach(cubemap -> {
				new VistasCubemapRenderer(cubemap).registerTextures(textureManager, resourceManager);

				Identifier identifier = panorama.getLogoControl().getLogoId();
				textureManager.registerTexture(identifier);
				AbstractTexture texture = textureManager.getTexture(identifier);
				if (texture instanceof ReloadableTexture reloadableTexture) {
					try {
						reloadableTexture.reload(reloadableTexture.loadContents(resourceManager));
					} catch (IOException e) {
						Vistas.LOGGER.warn("Failed to load texture: {}", identifier);
					}
				}
			})
		);
	}
}
