package com.terraformersmc.vistas.title;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.Cubemap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class VistasRotatingCubemapRenderer extends RotatingCubeMapRenderer implements AutoCloseable {
	private final MinecraftClient client;

	private final Object2ObjectOpenHashMap<Cubemap, VistasCubemapRenderer> renderers = new Object2ObjectOpenHashMap<>();

	public VistasRotatingCubemapRenderer(CubeMapRenderer defaultRenderer) {
		super(defaultRenderer);

		this.client = MinecraftClient.getInstance();
	}

	@Override
	public void render(DrawContext context, int width, int height, boolean rotate) {
		VistasCubemapRenderer.time += this.client.getRenderTickCounter().getFixedDeltaTicks();

		VistasTitle.CURRENT.getValue().getCubemaps().forEach(cubemap -> {
			VistasCubemapRenderer panoramaRenderer = renderers.get(cubemap);
			Identifier overlayId = panoramaRenderer.getCubemap().getCubemapId().withSuffixedPath("_overlay.png");

			panoramaRenderer.draw(this.client, 1.0f);

			if (this.client.getResourceManager().getResource(overlayId).isPresent()) {
				context.drawTexture(RenderPipelines.GUI_TEXTURED, overlayId, 0, 0, 0.0f, 0.0f, width, height, 16, 128, 16, 128);
			}
		});
	}

	@Override
	public void registerTextures(TextureManager textureManager) {
		VistasTitle.PANORAMAS.values().forEach(panorama ->
			panorama.getCubemaps().forEach(cubemap -> {
				VistasCubemapRenderer renderer = new VistasCubemapRenderer(cubemap);
				renderer.registerTextures(textureManager);
				renderers.put(cubemap, renderer);

				Identifier identifier = panorama.getLogoControl().getLogoId();
				textureManager.registerTexture(identifier);
				AbstractTexture texture = textureManager.getTexture(identifier);
				if (texture instanceof ReloadableTexture reloadableTexture) {
					try {
						reloadableTexture.reload(reloadableTexture.loadContents(this.client.getResourceManager()));
					} catch (IOException e) {
						Vistas.LOGGER.warn("Failed to load texture: {}", identifier);
					}
				}
			})
		);
	}

	@Override
	public void close() {
		for (Cubemap cubemap : renderers.keySet()) {
			renderers.remove(cubemap).close();
		}
	}
}
