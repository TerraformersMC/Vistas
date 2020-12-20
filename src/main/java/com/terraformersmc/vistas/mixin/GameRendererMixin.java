/*
 * This java file has been adapted from this file: https://github.com/liachmodded/runorama/blob/93316ed7df7140786092140b2d757af12a0ac039/src/main/java/com/github/liachmodded/runorama/mixin/GameRendererMixin.java
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.config.PanoramaConfig;
import com.terraformersmc.vistas.screenshot.PanoramicScreenshots;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private boolean renderingPanorama;

    @Shadow
    public abstract void renderWorld(float float_1, long long_1, MatrixStack matrixStack_1);

    @Inject(method = "render", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"))
    public void runorama$renderPanorama(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int i1, int i2) {
        if (PanoramicScreenshots.needsScreenshot) {
            PanoramicScreenshots.LOGGER.info("Taking screenshot");
            PanoramicScreenshots.needsScreenshot = false;

            Path root = PanoramicScreenshots.getPanoramicScreenshotFolder();
            File rootFile = root.toFile();
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }

            // setup
            boolean oldFov90 = renderingPanorama;
            float oldPitch = client.player.pitch;
            float oldYaw = client.player.yaw;
            if (PanoramaConfig.getInstance().lockPanoramicScreenshotRotation) {
                client.player.pitch = 0;
                client.player.yaw = 0;
            }
            boolean oldCulling = client.chunkCullingEnabled;
            client.chunkCullingEnabled = false;
            renderingPanorama = true;
            List<Quaternion> rotations = PanoramicScreenshots.ROTATIONS;
            // take
            for (int i = 0; i < rotations.size(); i++) {
                MatrixStack stack = new MatrixStack(); // Adding a layer in the old one fails empty check
                stack.multiply(rotations.get(i));
                doRender(tickDelta, startTime, stack);
                takeScreenshot(root, i);
            }
            // restore
            renderingPanorama = oldFov90;
            client.chunkCullingEnabled = oldCulling;
            client.player.pitch = oldPitch;
            client.player.yaw = oldYaw;
            if (client.player != null) {
                client.player.sendMessage(new TranslatableText("vistas.panoramic_screenshot.saved", new LiteralText(root.toAbsolutePath().toString()).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, root.toAbsolutePath().toString())).withUnderline(true))), false);
            }
        }
    }

    @Unique
    private void doRender(float tickDelta, long startTime, MatrixStack matrixStack) {
        this.renderWorld(tickDelta, Util.getMeasuringTimeNano() + startTime, matrixStack);
    }

    @Unique
    private void takeScreenshot(Path folder, int id) {
        NativeImage shot = ScreenshotUtils.takeScreenshot(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(),
                client.getFramebuffer());
        PanoramicScreenshots.saveScreenshot(shot, folder, id);
    }
}
