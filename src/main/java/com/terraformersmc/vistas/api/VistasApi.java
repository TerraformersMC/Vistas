package com.terraformersmc.vistas.api;

import java.util.Map;

import com.terraformersmc.vistas.panorama.Panorama;

import net.minecraft.util.Identifier;

public interface VistasApi {

	void appendPanoramas(Map<Identifier, Panorama> set);

}
