package com.terraformersmc.vistas.api;

import com.terraformersmc.vistas.panorama.Panorama;
import net.minecraft.resources.Identifier;

import java.util.Map;

public interface VistasApi {
	void appendPanoramas(Map<Identifier, Panorama> set);
}
