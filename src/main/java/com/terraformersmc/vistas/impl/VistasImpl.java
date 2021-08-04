package com.terraformersmc.vistas.impl;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.api.VistasApi;
import com.terraformersmc.vistas.registry.VistasRegistry;
import com.terraformersmc.vistas.registry.panorama.PanoramaGroup;

import net.minecraft.util.registry.Registry;

public class VistasImpl implements VistasApi {

	@Override
	public void registerPanoramas() {
		Registry.register(VistasRegistry.PANORAMA_REGISTRY, Vistas.id("default"), PanoramaGroup.DEFAULT);
	}

}
