package com.terraformersmc.vistas.access;

public interface RotatingCubeMapRendererAccess {
	public float getTime();

	public void setTime(float time);

	public static RotatingCubeMapRendererAccess get(Object e) {
		return (RotatingCubeMapRendererAccess) e;
	}
}
