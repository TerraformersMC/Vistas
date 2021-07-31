package com.terraformersmc.vistas.registry.panorama;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class VisualSettings {
	public static final Codec<VisualSettings> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.DOUBLE.optionalFieldOf("fov").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.fov);
		}), Codec.DOUBLE.optionalFieldOf("xLength").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.xLength);
		}), Codec.DOUBLE.optionalFieldOf("yLength").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.yLength);
		}), Codec.DOUBLE.optionalFieldOf("zLength").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.zLength);
		}), Codec.DOUBLE.optionalFieldOf("addedX").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.addedX);
		}), Codec.DOUBLE.optionalFieldOf("addedY").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.addedY);
		}), Codec.DOUBLE.optionalFieldOf("addedZ").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.addedZ);
		}), Codec.INT.optionalFieldOf("colorR").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.colorR);
		}), Codec.INT.optionalFieldOf("colorG").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.colorG);
		}), Codec.INT.optionalFieldOf("colorB").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.colorB);
		}), Codec.INT.optionalFieldOf("alpha").forGetter((panoramaGroup) -> {
			return Optional.of(panoramaGroup.alpha);
		})).apply(instance, (fov, xLength, yLength, zLength, addedX, addedY, addedZ, colorR, colorG, colorB, alpha) -> new VisualSettings(fov.orElse(85.0), xLength.orElse(2.0), yLength.orElse(2.0), zLength.orElse(2.0), addedX.orElse(0.0), addedY.orElse(0.0), addedZ.orElse(0.0), colorR.orElse(255), colorG.orElse(255), colorB.orElse(255), alpha.orElse(255)));
	});

	public static final VisualSettings DEFAULT = new VisualSettings();

	public final double fov;

	public final double xLength;
	public final double yLength;
	public final double zLength;

	public final double addedX;
	public final double addedY;
	public final double addedZ;

	public final int colorR;
	public final int colorG;
	public final int colorB;

	public final int alpha;

	public VisualSettings() {
		this(85);
	}

	public VisualSettings(double fov) {
		this(fov, 2.0, 2.0, 2.0);
	}

	public VisualSettings(double xLength, double yLength, double zLength) {
		this(85, xLength, yLength, zLength);
	}

	public VisualSettings(double fov, double xLength, double yLength, double zLength) {
		this(fov, xLength, yLength, zLength, 0.0D, 0.0D, 0.0D);
	}

	public VisualSettings(double fov, double xLength, double yLength, double zLength, double addedX, double addedY, double addedZ) {
		this(fov, xLength, yLength, zLength, addedX, addedY, addedZ, 255, 255, 255);
	}

	public VisualSettings(double fov, double xLength, double yLength, double zLength, double addedX, double addedY, double addedZ, int colorR, int colorG, int colorB) {
		this(fov, xLength, yLength, zLength, addedX, addedY, addedZ, colorR, colorB, colorG, 255);
	}

	public VisualSettings(double fov, double xLength, double yLength, double zLength, double addedX, double addedY, double addedZ, int colorR, int colorG, int colorB, int alpha) {
		this.fov = fov;
		this.xLength = xLength;
		this.yLength = yLength;
		this.zLength = zLength;
		this.addedX = addedX;
		this.addedY = addedY;
		this.addedZ = addedZ;
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
		this.alpha = alpha;
	}

}
