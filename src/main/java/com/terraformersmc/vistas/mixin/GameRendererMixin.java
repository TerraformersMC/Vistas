/*
 * This java file has been adapted from this file: https://github
 * .com/liachmodded/runorama/blob/93316ed7df7140786092140b2d757af12a0ac039/src/main/java/com/github/liachmodded
 * /runorama/mixin/GameRendererMixin.java
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.terraformersmc.vistas.mixin;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.util.Pair;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.resource.PanoramicScreenshots;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private boolean renderingPanorama;

    @SuppressWarnings("unused")
    @Shadow
    @Final
    private Camera camera;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;" +
            "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", shift = Shift.BEFORE))
    public void vistas$render(float delta, long startTime, boolean tick, CallbackInfo ci) {
        if (PanoramicScreenshots.timeSinceLastKeyPress >= 0.0D) {
            PanoramicScreenshots.timeSinceLastKeyPress -= delta;
        }
        if (PanoramicScreenshots.onShot != -1) {
            PanoramicScreenshots.time += delta;
        }
        if (PanoramicScreenshots.time > 375.0D) {
            if (PanoramicScreenshots.startingRotation.isPresent()) {
                assert client.player != null;
                client.player.setPitch(PanoramicScreenshots.startingRotation.get()
                        .getFirst());
                client.player.setYaw(PanoramicScreenshots.startingRotation.get()
                        .getSecond());
            }
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("vistas.panoramic_screenshot.broke"), false);
            }
            PanoramicScreenshots.onShot = -1;
            PanoramicScreenshots.startingRotation = Optional.empty();
            PanoramicScreenshots.currentScreenshotPath = Optional.empty();
            PanoramicScreenshots.time = 0.0D;
            PanoramicScreenshots.timeSinceLastKeyPress = 10.0D;
            PanoramicScreenshots.needsScreenshot = false;
        }
        if (PanoramicScreenshots.needsScreenshot) {
            Vistas.LOGGER.info("Taking screenshot");
            PanoramicScreenshots.needsScreenshot = false;

            Path root = PanoramicScreenshots.getPanoramicScreenshotFolder();
            File file = root.resolve("panorama_" + PanoramicScreenshots.onShot + ".png")
                    .toFile();
            if (PanoramicScreenshots.currentScreenshotPath.isEmpty()) {
                PanoramicScreenshots.currentScreenshotPath = Optional.of(root);
            }
            File rootFile = root.toFile();
            if (!rootFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                rootFile.mkdirs();
            }

            if (PanoramicScreenshots.startingRotation.isEmpty()) {
                PanoramicScreenshots.startingRotation =
                        Optional.of(Pair.of(VistasConfig.getInstance().lockScreenshotPitch ? 0.0F :
                                Objects.requireNonNull(client.player)
                        .getPitch(), VistasConfig.getInstance().lockScreenshotYaw ?
                                Objects.requireNonNull(client.player)
                        .getHorizontalFacing() == Direction.NORTH ? 180 :
                                        client.player.getHorizontalFacing() == Direction.EAST ? -90 :
                                                client.player.getHorizontalFacing() == Direction.SOUTH ? 0 : 90 :
                                Objects.requireNonNull(client.player)
                        .getYaw()));
            }

            // setup
            boolean wasRenderingPanorama = renderingPanorama;
            boolean culledBefore = client.chunkCullingEnabled;
            client.chunkCullingEnabled = false;
            renderingPanorama = true;
            Framebuffer framebuffer = new SimpleFramebuffer(this.client.getWindow()
                    .getFramebufferWidth(), this.client.getWindow()
                    .getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
            client.worldRenderer.reloadTransparencyShader();
            this.setBlockOutlineEnabled(false);
            this.setRenderHand(false);

            // take
            assert client.player != null;
            client.player.setPitch(PanoramicScreenshots.startingRotation.get()
                    .getFirst());
            client.player.setYaw(PanoramicScreenshots.startingRotation.get()
                    .getSecond());
            framebuffer.beginWrite(true);
            MatrixStack stack = new MatrixStack();
            stack.multiply(PanoramicScreenshots.ROTATIONS.get(PanoramicScreenshots.onShot));
            doRender(delta, startTime, stack);
            takeScreenshot(root, PanoramicScreenshots.onShot, framebuffer);

            // restore
            client.player.setPitch(PanoramicScreenshots.startingRotation.get()
                    .getFirst() + PanoramicScreenshots.PITCHES.get(PanoramicScreenshots.onShot));
            client.player.setYaw(PanoramicScreenshots.startingRotation.get()
                    .getSecond() + PanoramicScreenshots.YAWS.get(PanoramicScreenshots.onShot));
            renderingPanorama = wasRenderingPanorama;
            client.chunkCullingEnabled = culledBefore;
            this.setBlockOutlineEnabled(true);
            this.setRenderHand(true);
            client.worldRenderer.reloadTransparencyShader();
            framebuffer.delete();

            if (client.player != null && VistasConfig.getInstance().screenshotIndividually) {
                client.player.sendMessage(Text.translatable("vistas.panoramic_screenshot.taken",
                        Text.of(String.valueOf(PanoramicScreenshots.onShot)), Text.of(file.getName())
                        .copy()
                        .formatted(Formatting.UNDERLINE)
                        .styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE,
                                file.getAbsolutePath())))), false);
            }

            if (PanoramicScreenshots.onShot == 5) {
                PanoramicScreenshots.onShot = -1;
                client.player.setPitch(PanoramicScreenshots.startingRotation.get()
                        .getFirst());
                client.player.setYaw(PanoramicScreenshots.startingRotation.get()
                        .getSecond());
                PanoramicScreenshots.startingRotation = Optional.empty();
                PanoramicScreenshots.currentScreenshotPath = Optional.empty();
                PanoramicScreenshots.time = 0.0D;
                PanoramicScreenshots.timeSinceLastKeyPress = 10.0D;
                if (client.player != null) {
                    client.player.sendMessage(Text.translatable("vistas.panoramic_screenshot.saved",
                            Text.of(root.toAbsolutePath()
                                    .toString())
                            .copy()
                            .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE,
                                            root.toAbsolutePath()
                                            .toString()))
                                    .withUnderline(true))), false);
                }
            } else {
                // push
                client.player.setPitch(PanoramicScreenshots.startingRotation.get()
                        .getFirst() + PanoramicScreenshots.PITCHES.get(PanoramicScreenshots.onShot + 1));
                client.player.setYaw(PanoramicScreenshots.startingRotation.get()
                        .getSecond() + PanoramicScreenshots.YAWS.get(PanoramicScreenshots.onShot + 1));
                if (!VistasConfig.getInstance().screenshotIndividually) {
                    PanoramicScreenshots.needsScreenshot = true;
                    PanoramicScreenshots.onShot++;
                    vistas$render(delta, startTime, tick, ci);
                }
            }
        }
    }

    @Unique
    private void doRender(float tickDelta, long startTime, MatrixStack matrixStack) {
        this.renderWorld(tickDelta, Util.getMeasuringTimeNano() + startTime, matrixStack);
    }

    @Unique
    private void takeScreenshot(Path folder, int id, Framebuffer buffer) {
        NativeImage shot = ScreenshotRecorder.takeScreenshot(buffer);
        PanoramicScreenshots.saveScreenshot(shot, folder, id);
    }

    @Shadow
    public abstract void renderWorld(float delta, long startTime, MatrixStack matrices);

    @Shadow
    public abstract void setBlockOutlineEnabled(boolean blockOutlineEnabled);

    @Shadow
    public abstract void setRenderHand(boolean renderHand);

}
