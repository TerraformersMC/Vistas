package com.terraformersmc.vistas.api.panorama;

public class MovementSettings {

	public static final MovementSettings DEFAULT = new MovementSettings(false, 0.0F, 0.0F, 1.0F, false);

	private boolean frozen;
	private float addedX;
	private float addedY;
	private float speedMultiplier;
	private boolean woozy;

	public MovementSettings(boolean frozen, float addedX, float addedY, float speedMultiplier, boolean woozy) {
		this.frozen = frozen;
		this.addedX = addedX;
		this.addedY = addedY;
		this.speedMultiplier = speedMultiplier;
		this.woozy = woozy;
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

}
