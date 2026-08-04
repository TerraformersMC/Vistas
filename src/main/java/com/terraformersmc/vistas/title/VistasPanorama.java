package com.terraformersmc.vistas.title;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.Cubemap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Panorama;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class VistasPanorama extends Panorama implements AutoCloseable {
	private final Minecraft client;

	private final Object2ObjectOpenHashMap<Cubemap, VistasCubemapRenderer> renderers = new Object2ObjectOpenHashMap<>();

	public VistasPanorama() {
		super();
		this.client = Minecraft.getInstance();
	}

	public void renderCubemaps() {
		VistasTitle.CURRENT.get().getCubemaps().forEach(cubemap -> {
			VistasCubemapRenderer renderer = renderers.get(cubemap);
			renderer.draw(this.client, 1.0F);
		});
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int width, int height) {
		client.gameRenderer.gameRenderState().guiRenderState.panoramaRenderState =
				new PanoramaRenderState(0.0F);

		VistasCubemapRenderer.time += this.client.getDeltaTracker().getRealtimeDeltaTicks();

		VistasTitle.CURRENT.get().getCubemaps().forEach(cubemap -> {
			VistasCubemapRenderer panoramaRenderer = renderers.get(cubemap);
			Identifier overlayId = panoramaRenderer.getCubemap().getCubemapId().withSuffix("_overlay.png");

			panoramaRenderer.draw(this.client, 1.0f);

			if (this.client.getResourceManager().getResource(overlayId).isPresent()) {
				context.blit(RenderPipelines.GUI_TEXTURED, overlayId, 0, 0, 0.0f, 0.0f, width, height, 16, 128, 16, 128);
			}
		});
	}


	public void registerTextures(TextureManager textureManager) {
		VistasTitle.PANORAMAS.values().forEach(panorama ->
			panorama.getCubemaps().forEach(cubemap -> {
				VistasCubemapRenderer renderer = new VistasCubemapRenderer(cubemap);
				renderer.registerTextures(textureManager);
				renderers.put(cubemap, renderer);

				Identifier identifier = panorama.getLogoControl().getLogoId();
				textureManager.registerForNextReload(identifier);
				AbstractTexture texture = textureManager.getTexture(identifier);
				if (texture instanceof ReloadableTexture reloadableTexture) {
					try {
						reloadableTexture.apply(reloadableTexture.loadContents(this.client.getResourceManager()));
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
