package com.terraformersmc.archive.vistas.access;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Identifier;
@Deprecated
public interface TimeAccess {
	public float getTime();

	public void setTime(float time);

	public static <M extends RotatingCubeMapRenderer> float getTime(M map) {
		return ((TimeAccess) map).getTime();
	}

	public static <M extends RotatingCubeMapRenderer> M setTime(M map, float time) {
		((TimeAccess) map).setTime(time);
		return map;
	}

	public static RotatingCubeMapRenderer newWithTime(CubeMapRenderer cubeMap, float time) {
		return setTime(new RotatingCubeMapRenderer(cubeMap), time);
	}

	public static RotatingCubeMapRenderer newWithTime(Identifier id, float time) {
		return setTime(new RotatingCubeMapRenderer(new CubeMapRenderer(id)), time);
	}
}
