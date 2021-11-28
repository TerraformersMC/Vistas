package com.terraformersmc.vistas.panorama;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RotationControl {

	public static final RotationControl DEFAULT = new RotationControl();

	public static final Codec<RotationControl> CODEC = RecordCodecBuilder.create(
			(instance) -> 
			instance.group(
					Codec.BOOL.optionalFieldOf("frozen")
						.forGetter((movementControl) -> Optional.of(movementControl.frozen)),
					Codec.BOOL.optionalFieldOf("woozy")
						.forGetter((movementControl) -> Optional.of(movementControl.woozy)),
					Codec.DOUBLE.optionalFieldOf("addedPitch")
						.forGetter((movementControl) -> Optional.of(movementControl.addedPitch)),
					Codec.DOUBLE.optionalFieldOf("addedYaw")
						.forGetter((movementControl) -> Optional.of(movementControl.addedYaw)),
					Codec.DOUBLE.optionalFieldOf("addedRoll")
						.forGetter((movementControl) -> Optional.of(movementControl.addedRoll)),
					Codec.DOUBLE.optionalFieldOf("speedMultiplier")
						.forGetter((movementControl) -> Optional.of(movementControl.speedMultiplier))
					)
			.apply(instance, RotationControl::new));

	private final boolean frozen;
	private final boolean woozy;
	private final double addedPitch;
	private final double addedYaw;
	private final double addedRoll;
	private final double speedMultiplier;

	public RotationControl() {
		this.frozen = false;
		this.woozy = false;
		this.addedPitch = 0.0D;
		this.addedYaw = 0.0D;
		this.addedRoll = 0.0D;
		this.speedMultiplier = 1.0D;
	}

	public RotationControl(boolean frozen, boolean woozy, double addedPitch, double addedYaw, double addedRoll, double speedMultiplier) {
		this.frozen = frozen;
		this.woozy = woozy;
		this.addedPitch = addedPitch;
		this.addedYaw = addedYaw;
		this.addedRoll = addedRoll;
		this.speedMultiplier = speedMultiplier;
	}

	public RotationControl(Optional<Boolean> frozen, Optional<Boolean> woozy, Optional<Double> addedPitch, Optional<Double> addedYaw, Optional<Double> addedRoll, Optional<Double> speedMultiplier) {
		this.frozen = frozen.orElse(false);
		this.woozy = woozy.orElse(false);
		this.addedPitch = addedPitch.orElse(0.0D);
		this.addedYaw = addedYaw.orElse(0.0D);
		this.addedRoll = addedRoll.orElse(0.0D);
		this.speedMultiplier = speedMultiplier.orElse(1.0D);
	}

	public boolean isFrozen() {
		return frozen;
	}

	public boolean isWoozy() {
		return woozy;
	}

	public double getAddedPitch() {
		return addedPitch;
	}

	public double getAddedYaw() {
		return addedYaw;
	}

	public double getAddedRoll() {
		return addedRoll;
	}

	public double getSpeedMultiplier() {
		return speedMultiplier;
	}

	public double getPitch(double time) {
		return ((this.isWoozy() ? -time * 0.1D : Math.sin(time * 0.001D) * 5.0D + 25.0D) + this.getAddedPitch()) * this.getSpeedMultiplier();
	}

	public double getYaw(double time) {
		return ((-time * 0.1D) + this.getAddedYaw()) * this.getSpeedMultiplier();
	}

	public double getRoll(double time) {
		return (this.getAddedRoll()) * this.getSpeedMultiplier();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RotationControl mov) {
			return this.frozen == mov.frozen && this.woozy == mov.woozy && this.addedPitch == mov.addedPitch && this.addedYaw == mov.addedYaw && this.addedRoll == mov.addedRoll && this.speedMultiplier == mov.speedMultiplier;
		}
		return super.equals(obj);
	}

}
