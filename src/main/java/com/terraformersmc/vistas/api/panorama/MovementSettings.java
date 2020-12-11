package com.terraformersmc.vistas.api.panorama;

import com.google.common.base.Function;

public class MovementSettings {

	public static final MovementSettings DEFAULT = new MovementSettings(false, 0.0F, 0.0F, 1.0F, false);

	private boolean frozen;
	private float addedX;
	private float addedY;
	private float speedMultiplier;
	private boolean woozy;

	private boolean useXEquation;
	private Function<Float, Float> XEquation;
	private boolean useYEquation;
	private Function<Float, Float> YEquation;

	public MovementSettings(boolean frozen, float addedX, float addedY, float speedMultiplier, boolean woozy, boolean useXEquation, boolean useYEquation, Function<Float, Float> XEquation, Function<Float, Float> YEquation) {
		this.frozen = frozen;
		this.addedX = addedX;
		this.addedY = addedY;
		this.speedMultiplier = speedMultiplier;
		this.woozy = woozy;
		this.useXEquation = useXEquation;
		this.useYEquation = useYEquation;
		this.XEquation = XEquation;
		this.YEquation = YEquation;
	}

	public MovementSettings(boolean frozen, float addedX, float addedY, float speedMultiplier, boolean woozy) {
		this.frozen = frozen;
		this.addedX = addedX;
		this.addedY = addedY;
		this.speedMultiplier = speedMultiplier;
		this.woozy = woozy;
		this.useXEquation = false;
		this.useYEquation = false;
		this.XEquation = (time) -> {
			return 0.0F;
		};
		this.YEquation = (time) -> {
			return 0.0F;
		};
	}

	public boolean isFrozen() {
		return frozen;
	}

	public float getAddedX() {
		return addedX;
	}

	public float getAddedY() {
		return addedY;
	}

	public float getSpeedMultiplier() {
		return speedMultiplier;
	}

	public boolean isWoozy() {
		return woozy;
	}

	public boolean isUsingXEquation() {
		return useXEquation;
	}

	public Function<Float, Float> getXEquation() {
		return XEquation;
	}

	public boolean isUsingYEquation() {
		return useYEquation;
	}

	public Function<Float, Float> getYEquation() {
		return YEquation;
	}

}
