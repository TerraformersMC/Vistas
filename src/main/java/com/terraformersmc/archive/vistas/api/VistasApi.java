package com.terraformersmc.archive.vistas.api;

import java.util.Set;

import com.terraformersmc.archive.vistas.api.panorama.Panorama;
@Deprecated
public interface VistasApi {
	void appendPanoramas(Set<Panorama> panoramas);
}
