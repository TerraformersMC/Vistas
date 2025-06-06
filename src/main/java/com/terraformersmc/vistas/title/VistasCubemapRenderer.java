package com.terraformersmc.vistas.title;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.terraformersmc.vistas.panorama.Cubemap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.ProjectionMatrix3;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@Environment(EnvType.CLIENT)
public class VistasCubemapRenderer implements AutoCloseable {
	protected static double time = 0.0D;

	@Nullable
	private GpuBuffer buffer = null;
	private final ProjectionMatrix3 projectionMatrix;

	private final Cubemap cubemap;

	public VistasCubemapRenderer(Cubemap cubemap) {
		this.projectionMatrix = new ProjectionMatrix3("cubemap", 0.05f, 10.0f);

		this.cubemap = cubemap;
	}

	public void draw(MinecraftClient client, float alpha) {
		if (this.buffer == null) {
			this.upload(alpha);
		}

		RenderSystem.setProjectionMatrix(this.projectionMatrix.set(
				client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(),
				(float) this.cubemap.getVisualControl().getFov()), ProjectionType.PERSPECTIVE);

		RenderPipeline renderPipeline = RenderPipelines.POSITION_TEX_PANORAMA;
		Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
		GpuTextureView gpuTextureColor = framebuffer.getColorAttachmentView();
		GpuTextureView gpuTextureDepth = framebuffer.getDepthAttachmentView();
		RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
		GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);

		Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.pushMatrix();
		matrixStack.rotationX((float) Math.PI);
		matrixStack.translate((float) this.cubemap.getVisualControl().getAddedX(), (float) this.cubemap.getVisualControl().getAddedY(), (float) this.cubemap.getVisualControl().getAddedZ());
		matrixStack.rotate(RotationAxis.POSITIVE_X.rotationDegrees((float) this.cubemap.getRotationControl().getPitch(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.rotate(RotationAxis.POSITIVE_Y.rotationDegrees((float) this.cubemap.getRotationControl().getYaw(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.rotate(RotationAxis.POSITIVE_Z.rotationDegrees((float) this.cubemap.getRotationControl().getRoll(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write(new Matrix4f(matrixStack), new Vector4f(1.0f, 1.0f, 1.0f, alpha), new Vector3f(), new Matrix4f(), 0.0f);
		matrixStack.popMatrix();

		try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", gpuTextureColor, OptionalInt.empty(), gpuTextureDepth, OptionalDouble.empty())) {
			renderPass.setPipeline(renderPipeline);
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setVertexBuffer(0, this.buffer);
			renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
			renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
			renderPass.bindSampler("Sampler0", client.getTextureManager().getTexture(this.cubemap.getCubemapId()).getGlTextureView());
			renderPass.drawIndexed(0, 0, 36, 1);
		}
	}

	private void upload(float alpha) {
		int r = Math.round((float) this.cubemap.getVisualControl().getColorR());
		int g = Math.round((float) this.cubemap.getVisualControl().getColorG());
		int b = Math.round((float) this.cubemap.getVisualControl().getColorB());
		int a = Math.round((float) this.cubemap.getVisualControl().getColorA() * alpha);

		float w = (float) this.cubemap.getVisualControl().getWidth() / 2.0f;
		float h = (float) this.cubemap.getVisualControl().getHeight() / 2.0f;
		float d = (float) this.cubemap.getVisualControl().getDepth() / 2.0f;

		try (BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION.getVertexSize() * 4 * 6)) {
			BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

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

			BuiltBuffer builtBuffer = bufferBuilder.end();

			try {
				this.buffer =  RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, builtBuffer.getBuffer());
				builtBuffer.close();
			} catch (Throwable throwable) {
				if (builtBuffer != null) {
					try {
						builtBuffer.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}
				}
				throw throwable;
			}
		}
	}

	public void registerTextures(TextureManager textureManager) {
		textureManager.registerTexture(this.cubemap.getCubemapId(), new CubemapTexture(this.cubemap.getCubemapId()));
	}

	@Override
	public void close() {
		if (this.buffer != null) {
			this.buffer.close();
		}
		this.projectionMatrix.close();
	}

	public Cubemap getCubemap() {
		return cubemap;
	}
}
