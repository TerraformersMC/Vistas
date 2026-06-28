package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraformersmc.vistas.resource.PanoramicScreenshots;
import net.minecraft.client.Screenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;

@Mixin(Screenshot.class)
public class ScreenshotMixin {
    @WrapOperation(method = "lambda$grab$0",
            at = @At(value = "NEW", args = "class=java/io/File", ordinal = 0)
    )
    @SuppressWarnings("unused")
    private static File vistas$panoramaPathOverride(File path, String file, Operation<File> original) {
        if (path.toString().contains(PanoramicScreenshots.PANORAMAS_PATH)) {
            return path;
        }

        return original.call(path, file);
    }
}
