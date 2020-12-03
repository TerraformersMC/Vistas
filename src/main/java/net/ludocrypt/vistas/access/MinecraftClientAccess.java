package net.ludocrypt.vistas.access;

import net.ludocrypt.vistas.Vistas.Panorama;
import net.ludocrypt.vistas.resource.PanoramaManager;

public interface MinecraftClientAccess {
	public PanoramaManager getPanoramaManager();

	public Panorama getClientPanorama();

	public void setClientPanorama(Panorama pan);
}
