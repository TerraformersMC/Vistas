package com.terraformersmc.vistas.title;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface LogoDrawerAccessor {
    void setIsVistas(boolean value);
}
