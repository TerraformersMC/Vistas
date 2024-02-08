package com.terraformersmc.vistas.api;

import com.terraformersmc.vistas.panorama.Panorama;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface VistasApi {
	void appendPanoramas(Map<Identifier, Panorama> set);
}
