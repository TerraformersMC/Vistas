package com.terraformersmc.vistas.title;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.vistas.panorama.Cubemap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public class VistasCubemapRenderer {
	protected static double time = 0.0D;

	private static final int FACES_COUNT = 6;
	private final Identifier[] faces = new Identifier[FACES_COUNT];
	private final VertexBuffer[] vertexBuffers = new VertexBuffer[FACES_COUNT];

	private final Cubemap cubemap;

	public VistasCubemapRenderer(Cubemap cubemap) {
		Identifier faces = cubemap.getCubemapId();
		for (int face = 0; face < FACES_COUNT; ++face) {
			this.faces[face] = faces.withSuffixedPath("_" + face + ".png");
		}

		this.cubemap = cubemap;
	}

	public void draw(MinecraftClient client, float alpha) {
		if (this.vertexBuffers[0] == null) {
			this.computeVertexBuffers(alpha);
		}

		Matrix4f matrix4f = new Matrix4f().setPerspective((float) Math.toRadians(this.cubemap.getVisualControl().getFov()), (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight(), 0.05F, 10.0F);
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.PERSPECTIVE);
		Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.pushMatrix();
		matrixStack.rotationX((float) Math.PI);

		matrixStack.pushMatrix();
		matrixStack.translate((float) this.cubemap.getVisualControl().getAddedX(), (float) this.cubemap.getVisualControl().getAddedY(), (float) this.cubemap.getVisualControl().getAddedZ());
		matrixStack.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) this.cubemap.getRotationControl().getPitch(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) this.cubemap.getRotationControl().getYaw(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.rotate(RotationAxis.POSITIVE_Z.rotationDegrees((float) this.cubemap.getRotationControl().getRoll(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));

		for (int pass = 0; pass < 4; ++pass) {
			matrixStack.pushMatrix();
			float xOffset = ((float)(pass % 2) / 2.0F - 0.5F) / 256.0F;
			float yOffset = ((float)(pass / 2) / 2.0F - 0.5F) / 256.0F;
			matrixStack.translate(xOffset, yOffset, 0.0F);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha / (float)(pass + 1));

			for (int face = 0; face < FACES_COUNT; ++face) {
				this.vertexBuffers[face].bind();
				this.vertexBuffers[face].draw(RenderLayer.getPanorama(this.faces[face]));
			}

			VertexBuffer.unbind();
			matrixStack.popMatrix();
			RenderSystem.colorMask(true, true, true, false);
		}

		matrixStack.popMatrix();

		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.restoreProjectionMatrix();
		matrixStack.popMatrix();
	}

	private void computeVertexBuffers(float alpha) {
		int r = Math.round((float) this.cubemap.getVisualControl().getColorR());
		int g = Math.round((float) this.cubemap.getVisualControl().getColorG());
		int b = Math.round((float) this.cubemap.getVisualControl().getColorB());
		int a = Math.round((float) this.cubemap.getVisualControl().getColorA() * alpha);

		float w = (float) this.cubemap.getVisualControl().getWidth() / 2.0f;
		float h = (float) this.cubemap.getVisualControl().getHeight() / 2.0f;
		float d = (float) this.cubemap.getVisualControl().getDepth() / 2.0f;

		try (BufferAllocator bufferAllocator = new BufferAllocator(VertexFormats.POSITION_TEXTURE_COLOR.getVertexSizeByte() * 4)) {
			for (int face = 0; face < FACES_COUNT; ++face) {
				BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

				if (face == 0) {
					bufferBuilder.vertex(-w, -h, d).texture(0.0F, 0.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, h, d).texture(0.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, h, d).texture(1.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, -h, d).texture(1.0F, 0.0F).color(r, g, b, a);
				}

				if (face == 1) {
					bufferBuilder.vertex(w, -h, d).texture(0.0F, 0.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, h, d).texture(0.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, h, -d).texture(1.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, a);
				}

				if (face == 2) {
					bufferBuilder.vertex(w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, h, -d).texture(0.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, h, -d).texture(1.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, a);
				}

				if (face == 3) {
					bufferBuilder.vertex(-w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, h, -d).texture(0.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, h, d).texture(1.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, -h, d).texture(1.0F, 0.0F).color(r, g, b, a);
				}

				if (face == 4) {
					bufferBuilder.vertex(-w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, -h, d).texture(0.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, -h, d).texture(1.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, a);
				}

				if (face == 5) {
					bufferBuilder.vertex(-w, h, d).texture(0.0F, 0.0F).color(r, g, b, a);
					bufferBuilder.vertex(-w, h, -d).texture(0.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, h, -d).texture(1.0F, 1.0F).color(r, g, b, a);
					bufferBuilder.vertex(w, h, d).texture(1.0F, 0.0F).color(r, g, b, a);
				}

				this.vertexBuffers[face] = new VertexBuffer(GlUsage.STATIC_WRITE);
				this.vertexBuffers[face].bind();
				this.vertexBuffers[face].upload(bufferBuilder.end());
				VertexBuffer.unbind();
			}
		}
	}

	public void registerTextures(TextureManager textureManager) {
		for (Identifier identifier : this.faces) {
			textureManager.registerTexture(identifier);
		}
	}

	public Cubemap getCubemap() {
		return cubemap;
	}
}
