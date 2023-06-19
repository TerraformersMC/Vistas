package com.terraformersmc.vistas.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.LogoDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(LogoDrawer.class)
public interface LogoDrawerAccessor {
    @Accessor("ignoreAlpha")
    void setIsVistas(boolean value);
}
