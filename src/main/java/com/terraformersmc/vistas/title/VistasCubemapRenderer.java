package com.terraformersmc.vistas.title;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.terraformersmc.vistas.panorama.Cubemap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.client.renderer.texture.TextureManager;
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
	private final Projection projection;
	private final ProjectionMatrixBuffer projectionMatrix;

	private final Cubemap cubemap;

	public VistasCubemapRenderer(Cubemap cubemap) {
		this.projection = new Projection();
		this.projectionMatrix = new ProjectionMatrixBuffer("cubemap");

		this.cubemap = cubemap;
	}

	public void draw(Minecraft client, float alpha) {
		if (this.buffer == null) {
			this.upload(alpha);
		}

		WindowRenderState windowState = client.gameRenderer.getGameRenderState().windowRenderState;
		this.projection.setupPerspective(0.05F, 10.0F, 85.0F, windowState.width, windowState.height);

		RenderSystem.setProjectionMatrix(this.projectionMatrix.getBuffer(this.projection), ProjectionType.PERSPECTIVE);

		RenderPipeline renderPipeline = RenderPipelines.PANORAMA;
		RenderTarget framebuffer = Minecraft.getInstance().getMainRenderTarget();
		GpuTextureView gpuTextureColor = framebuffer.getColorTextureView();
		GpuTextureView gpuTextureDepth = framebuffer.getDepthTextureView();
		RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
		GpuBuffer gpuBuffer = shapeIndexBuffer.getBuffer(36);

		Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.pushMatrix();
		matrixStack.rotationX((float) Math.PI);
		matrixStack.translate((float) this.cubemap.getVisualControl().getAddedX(), (float) this.cubemap.getVisualControl().getAddedY(), (float) this.cubemap.getVisualControl().getAddedZ());
		matrixStack.rotate(Axis.XP.rotationDegrees((float) this.cubemap.getRotationControl().getPitch(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.rotate(Axis.YP.rotationDegrees((float) this.cubemap.getRotationControl().getYaw(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.rotate(Axis.ZP.rotationDegrees((float) this.cubemap.getRotationControl().getRoll(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(matrixStack), new Vector4f(1.0f, 1.0f, 1.0f, alpha), new Vector3f(), new Matrix4f());
		matrixStack.popMatrix();

		try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", gpuTextureColor, OptionalInt.empty(), gpuTextureDepth, OptionalDouble.empty())) {
			renderPass.setPipeline(renderPipeline);
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setVertexBuffer(0, this.buffer);
			renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.type());
			renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
			AbstractTexture texture = client.getTextureManager().getTexture(this.cubemap.getCubemapId());
			renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
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

		try (ByteBufferBuilder bufferAllocator = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 4 * 6)) {
			BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

			// face 0
			bufferBuilder.addVertex(-w, -h, d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, h, d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, h, d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, -h, d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

			// face 1
			bufferBuilder.addVertex(w, -h, d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, h, d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, h, -d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, -h, -d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

			// face 2
			bufferBuilder.addVertex(w, -h, -d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, h, -d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, h, -d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, -h, -d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

			// face 3
			bufferBuilder.addVertex(-w, -h, -d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, h, -d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, h, d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, -h, d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

			// face 4
			bufferBuilder.addVertex(-w, -h, -d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, -h, d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, -h, d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, -h, -d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

			// face 5
			bufferBuilder.addVertex(-w, h, d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(-w, h, -d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, h, -d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(w, h, d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

			MeshData builtBuffer = bufferBuilder.buildOrThrow();

			try {
				this.buffer =  RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, builtBuffer.vertexBuffer());
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
		textureManager.registerAndLoad(this.cubemap.getCubemapId(), new CubeMapTexture(this.cubemap.getCubemapId()));
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
