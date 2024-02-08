package com.terraformersmc.vistas.panorama;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public class VisualControl {
	public static final VisualControl DEFAULT = new VisualControl();

	public static final Codec<VisualControl> CODEC = RecordCodecBuilder.create(
			(instance) -> 
			instance.group(
					Codec.DOUBLE.optionalFieldOf("fov")
						.forGetter((visualControl) -> Optional.of(visualControl.fov)),
					Codec.DOUBLE.optionalFieldOf("width")
						.forGetter((visualControl) -> Optional.of(visualControl.width)),
					Codec.DOUBLE.optionalFieldOf("height")
						.forGetter((visualControl) -> Optional.of(visualControl.height)),
					Codec.DOUBLE.optionalFieldOf("depth")
						.forGetter((visualControl) -> Optional.of(visualControl.depth)),
					Codec.DOUBLE.optionalFieldOf("addedX")
						.forGetter((visualControl) -> Optional.of(visualControl.addedX)),
					Codec.DOUBLE.optionalFieldOf("addedY")
						.forGetter((visualControl) -> Optional.of(visualControl.addedY)),
					Codec.DOUBLE.optionalFieldOf("addedZ")
						.forGetter((visualControl) -> Optional.of(visualControl.addedZ)),
					Codec.DOUBLE.optionalFieldOf("colorR")
						.forGetter((visualControl) -> Optional.of(visualControl.colorR)),
					Codec.DOUBLE.optionalFieldOf("colorG")
						.forGetter((visualControl) -> Optional.of(visualControl.colorG)),
					Codec.DOUBLE.optionalFieldOf("colorB")
						.forGetter((visualControl) -> Optional.of(visualControl.colorB)),
					Codec.DOUBLE.optionalFieldOf("colorA")
						.forGetter((visualControl) -> Optional.of(visualControl.colorA))
					)
			.apply(instance, VisualControl::new));

	private final double fov;

	private final double width;
	private final double height;
	private final double depth;

	private final double addedX;
	private final double addedY;
	private final double addedZ;

	private final double colorR;
	private final double colorG;
	private final double colorB;
	private final double colorA;

	public VisualControl() {
		this.fov = 85.0D;
		this.width = 2.0D;
		this.height = 2.0D;
		this.depth = 2.0D;
		this.addedX = 0.0D;
		this.addedY = 0.0D;
		this.addedZ = 0.0D;
		this.colorR = 255.0D;
		this.colorG = 255.0D;
		this.colorB = 255.0D;
		this.colorA = 255.0D;
	}

	@SuppressWarnings("unused")
	public VisualControl(double fov, double width, double height, double depth, double addedX, double addedY, double addedZ, double colorR, double colorG, double colorB, double colorA) {
		this.fov = fov;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.addedX = addedX;
		this.addedY = addedY;
		this.addedZ = addedZ;
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
		this.colorA = colorA;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public VisualControl(Optional<Double> fov, Optional<Double> width, Optional<Double> height, Optional<Double> depth, Optional<Double> addedX, Optional<Double> addedY, Optional<Double> addedZ, Optional<Double> colorR, Optional<Double> colorG, Optional<Double> colorB, Optional<Double> colorA) {
		this.fov = fov.orElse(85.0D);
		this.width = width.orElse(2.0D);
		this.height = height.orElse(2.0D);
		this.depth = depth.orElse(2.0D);
		this.addedX = addedX.orElse(0.0D);
		this.addedY = addedY.orElse(0.0D);
		this.addedZ = addedZ.orElse(0.0D);
		this.colorR = colorR.orElse(255.0D);
		this.colorG = colorG.orElse(255.0D);
		this.colorB = colorB.orElse(255.0D);
		this.colorA = colorA.orElse(255.0D);
	}

	public double getFov() {
		return fov;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public double getDepth() {
		return depth;
	}

	public double getAddedX() {
		return addedX;
	}

	public double getAddedY() {
		return addedY;
	}

	public double getAddedZ() {
		return addedZ;
	}

	public double getColorR() {
		return colorR;
	}

	public double getColorG() {
		return colorG;
	}

	public double getColorB() {
		return colorB;
	}

	public double getColorA() {
		return colorA;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VisualControl vis) {
			return this.fov == vis.fov && this.width == vis.width && this.height == vis.height && this.depth == vis.depth && this.addedX == vis.addedX && this.addedY == vis.addedY && this.addedZ == vis.addedZ && this.colorR == vis.colorR && this.colorG == vis.colorG && this.colorB == vis.colorB && this.colorA == vis.colorA;
		}
		return super.equals(obj);
	}
}
