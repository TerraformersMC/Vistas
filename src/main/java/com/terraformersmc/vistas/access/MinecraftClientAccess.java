package com.terraformersmc.vistas.access;

import com.terraformersmc.vistas.api.panorama.Panorama;

public interface MinecraftClientAccess {
    void setClientPanorama(Panorama pan);
    Panorama getClientPanorama();
}
