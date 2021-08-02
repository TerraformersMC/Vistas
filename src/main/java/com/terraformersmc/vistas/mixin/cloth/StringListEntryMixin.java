package com.terraformersmc.vistas.mixin.cloth;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terraformersmc.vistas.access.MinecraftClientAccess;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;

import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Pseudo
@Mixin(StringListEntry.class)
public abstract class StringListEntryMixin extends TextFieldListEntry<String> {

	@SuppressWarnings("deprecation")
	protected StringListEntryMixin(Text fieldName, String original, Text resetButtonKey, Supplier<String> defaultValue) {
		super(fieldName, original, resetButtonKey, defaultValue);
	}

	@Inject(method = "save", at = @At("TAIL"))
	private void vistas$save(CallbackInfo ci) {
		if (this.getFieldName()instanceof TranslatableText text && text.getKey().equals("text.autoconfig.vistas.option.panorama")) {
			MinecraftClientAccess.get().setCurrentPanorama(VistasRegistry.getPanorama(this.getValue()).orElse(PanoramaGroup.DEFAULT));
		}
	}

}
