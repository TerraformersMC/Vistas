package com.terraformersmc.vistas.title;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.panorama.Cubemap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.io.IOException;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@Environment(EnvType.CLIENT)
public class VistasCubemapRenderer {
	protected static double time = 0.0D;

	private static final int FACES_COUNT = 6;
	private final Identifier[] faces = new Identifier[FACES_COUNT];

	@Nullable
	private GpuBuffer buffer = null;

	private final Cubemap cubemap;

	public VistasCubemapRenderer(Cubemap cubemap) {
		Identifier faces = cubemap.getCubemapId();
		for (int face = 0; face < FACES_COUNT; ++face) {
			this.faces[face] = faces.withSuffixedPath("_" + face + ".png");
		}

		this.cubemap = cubemap;
	}

	public void draw(MinecraftClient client, float alpha) {
		if (this.buffer == null) {
			this.upload(alpha);
		}

		Matrix4f matrix4f = new Matrix4f().setPerspective(
				(float) Math.toRadians(this.cubemap.getVisualControl().getFov()),
				(float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight(),
				0.05F, 10.0F);
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

		RenderPipeline renderPipeline = RenderPipelines.POSITION_TEX_PANORAMA;
		Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
		GpuTexture gpuTextureColor = framebuffer.getColorAttachment();
		GpuTexture gpuTextureDepth = framebuffer.getDepthAttachment();
		RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
		GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);

		try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder()
				.createRenderPass(gpuTextureColor, OptionalInt.empty(), gpuTextureDepth, OptionalDouble.empty())) {
			renderPass.setPipeline(renderPipeline);
			renderPass.setVertexBuffer(0, this.buffer);
			renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());

			for (int pass = 0; pass < 4; ++pass) {
				matrixStack.pushMatrix();
				float xOffset = ((float)(pass % 2) / 2.0F - 0.5F) / 256.0F;
				float yOffset = ((float)(pass / 2) / 2.0F - 0.5F) / 256.0F;
				matrixStack.translate(xOffset, yOffset, 0.0F);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha / (float)(pass + 1));

				for (int face = 0; face < FACES_COUNT; ++face) {
					renderPass.bindSampler("Sampler0", client.getTextureManager().getTexture(this.faces[face]).getGlTexture());
					renderPass.drawIndexed(6 * face, FACES_COUNT);
				}

				matrixStack.popMatrix();
			}
		}

		matrixStack.popMatrix();

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.restoreProjectionMatrix();
		matrixStack.popMatrix();
	}

	private void upload(float alpha) {
		int r = Math.round((float) this.cubemap.getVisualControl().getColorR());
		int g = Math.round((float) this.cubemap.getVisualControl().getColorG());
		int b = Math.round((float) this.cubemap.getVisualControl().getColorB());
		int a = Math.round((float) this.cubemap.getVisualControl().getColorA() * alpha);

		float w = (float) this.cubemap.getVisualControl().getWidth() / 2.0f;
		float h = (float) this.cubemap.getVisualControl().getHeight() / 2.0f;
		float d = (float) this.cubemap.getVisualControl().getDepth() / 2.0f;

		this.buffer = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, 24 * VertexFormats.POSITION_TEXTURE.getVertexSize());

		try (BufferAllocator bufferAllocator = new BufferAllocator(VertexFormats.POSITION_TEXTURE.getVertexSize() * 4)) {
			BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

			// face 0
			bufferBuilder.vertex(-w, -h, d).texture(0.0F, 0.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, h, d).texture(0.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, h, d).texture(1.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, -h, d).texture(1.0F, 0.0F).color(r, g, b, a);

			// face 1
			bufferBuilder.vertex(w, -h, d).texture(0.0F, 0.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, h, d).texture(0.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, h, -d).texture(1.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, a);

			// face 2
			bufferBuilder.vertex(w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, h, -d).texture(0.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, h, -d).texture(1.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, a);

			// face 3
			bufferBuilder.vertex(-w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, h, -d).texture(0.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, h, d).texture(1.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, -h, d).texture(1.0F, 0.0F).color(r, g, b, a);

			// face 4
			bufferBuilder.vertex(-w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, -h, d).texture(0.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, -h, d).texture(1.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, a);

			// face 5
			bufferBuilder.vertex(-w, h, d).texture(0.0F, 0.0F).color(r, g, b, a);
			bufferBuilder.vertex(-w, h, -d).texture(0.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, h, -d).texture(1.0F, 1.0F).color(r, g, b, a);
			bufferBuilder.vertex(w, h, d).texture(1.0F, 0.0F).color(r, g, b, a);

			try (BuiltBuffer builtBuffer = bufferBuilder.end()) {
				CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
				commandEncoder.writeToBuffer(this.buffer, builtBuffer.getBuffer(), 0);
			}
		}
	}

	public void registerTextures(TextureManager textureManager, ResourceManager resourceManager) {
		for (Identifier identifier : this.faces) {
			textureManager.registerTexture(identifier);
			AbstractTexture texture = textureManager.getTexture(identifier);
			if (texture instanceof ReloadableTexture reloadableTexture) {
				try {
					reloadableTexture.reload(reloadableTexture.loadContents(resourceManager));
				} catch (IOException e) {
					Vistas.LOGGER.warn("Failed to load texture: {}", identifier);
				}
			}
		}
	}

	public Cubemap getCubemap() {
		return cubemap;
	}
}
