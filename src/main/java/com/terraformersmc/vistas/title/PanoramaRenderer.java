package com.terraformersmc.vistas.title;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import com.terraformersmc.vistas.panorama.Cubemap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class PanoramaRenderer {

	public static double time = 0.0D;

	private final MinecraftClient client;
	private final Cubemap cubemap;

	public PanoramaRenderer(Cubemap cubemap) {
		this.cubemap = cubemap;
		this.client = MinecraftClient.getInstance();
	}

	@SuppressWarnings("unused")
	public void render(float delta, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Matrix4f matrix4f = new Matrix4f().perspective((float) Math.toRadians(this.cubemap.getVisualControl().getFov()), (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight(), 0.05F, 100.0F);
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.loadIdentity();
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.disableCull();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();

		int r = Math.round((float) this.cubemap.getVisualControl().getColorR());
		int g = Math.round((float) this.cubemap.getVisualControl().getColorG());
		int b = Math.round((float) this.cubemap.getVisualControl().getColorB());
		int l = Math.round((float) this.cubemap.getVisualControl().getColorA() * alpha);

		double w = this.cubemap.getVisualControl().getWidth() / 2.0D;
		double h = this.cubemap.getVisualControl().getHeight() / 2.0D;
		double d = this.cubemap.getVisualControl().getDepth() / 2.0D;

		matrixStack.push();
		matrixStack.translate(this.cubemap.getVisualControl().getAddedX(), this.cubemap.getVisualControl().getAddedY(), this.cubemap.getVisualControl().getAddedZ());
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) this.cubemap.getRotationControl().getPitch(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) this.cubemap.getRotationControl().getYaw(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) this.cubemap.getRotationControl().getRoll(cubemap.getRotationControl().isFrozen() ? 0.0D : time)));
		RenderSystem.applyModelViewMatrix();

		for (int k = 0; k < 6; ++k) {
			RenderSystem.setShaderTexture(0, new Identifier(this.cubemap.getCubemapId().toString() + "_" + k + ".png"));
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

			if (k == 0) {
				bufferBuilder.vertex(-w, -h, d).texture(0.0F, 0.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, h, d).texture(0.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, h, d).texture(1.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, -h, d).texture(1.0F, 0.0F).color(r, g, b, l).next();
			}

			if (k == 1) {
				bufferBuilder.vertex(w, -h, d).texture(0.0F, 0.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, h, d).texture(0.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, h, -d).texture(1.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, l).next();
			}

			if (k == 2) {
				bufferBuilder.vertex(w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, h, -d).texture(0.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, h, -d).texture(1.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, l).next();
			}

			if (k == 3) {
				bufferBuilder.vertex(-w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, h, -d).texture(0.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, h, d).texture(1.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, -h, d).texture(1.0F, 0.0F).color(r, g, b, l).next();
			}

			if (k == 4) {
				bufferBuilder.vertex(-w, -h, -d).texture(0.0F, 0.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, -h, d).texture(0.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, -h, d).texture(1.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, -h, -d).texture(1.0F, 0.0F).color(r, g, b, l).next();
			}

			if (k == 5) {
				bufferBuilder.vertex(-w, h, d).texture(0.0F, 0.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(-w, h, -d).texture(0.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, h, -d).texture(1.0F, 1.0F).color(r, g, b, l).next();
				bufferBuilder.vertex(w, h, d).texture(1.0F, 0.0F).color(r, g, b, l).next();
			}

			tessellator.draw();
		}

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.restoreProjectionMatrix();
		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.depthMask(true);
		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
	}

	public Cubemap getCubemap() {
		return cubemap;
	}

}
