package com.terraformersmc.vistas.panorama;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class LogoControl {

	public static final LogoControl DEFAULT = new LogoControl();

	public static final Codec<LogoControl> CODEC = RecordCodecBuilder.create(
			(instance) -> 
			instance.group(
					Identifier.CODEC.optionalFieldOf("logoId")
						.forGetter((logoControl) -> Optional.of(logoControl.logoId)),
					Codec.DOUBLE.optionalFieldOf("logoX")
						.forGetter((logoControl) -> Optional.of(logoControl.logoX)),
					Codec.DOUBLE.optionalFieldOf("logoY")
						.forGetter((logoControl) -> Optional.of(logoControl.logoY)),
					Codec.DOUBLE.optionalFieldOf("logoRot")
						.forGetter((logoControl) -> Optional.of(logoControl.logoRot)),
					Codec.BOOL.optionalFieldOf("outlined")
						.forGetter((logoControl) -> Optional.of(logoControl.outlined)),
					Codec.DOUBLE.optionalFieldOf("splashX")
						.forGetter((logoControl) -> Optional.of(logoControl.splashX)),
					Codec.DOUBLE.optionalFieldOf("splashY")
						.forGetter((logoControl) -> Optional.of(logoControl.splashY)),
					Codec.DOUBLE.optionalFieldOf("splashRot")
						.forGetter((logoControl) -> Optional.of(logoControl.splashRot)),
					Codec.BOOL.optionalFieldOf("showEdition")
						.forGetter((logoControl) -> Optional.of(logoControl.showEdition))
					)
			.apply(instance, LogoControl::new));

	private final Identifier logoId;
	private final double logoX;
	private final double logoY;
	private final double logoRot;
	private final boolean outlined;

	private final double splashX;
	private final double splashY;
	private final double splashRot;

	private final boolean showEdition;

	public LogoControl() {
		this.logoId = new Identifier("textures/gui/title/minecraft.png");
		this.logoX = 0.0D;
		this.logoY = 0.0D;
		this.logoRot = 0.0D;
		this.outlined = true;

		this.splashX = 0.0D;
		this.splashY = 0.0D;
		this.splashRot = -20.0D;

		this.showEdition = true;
	}

	public LogoControl(Identifier logoId, double logoX, double logoY, double logoRot, boolean outlined, double splashX, double splashY, double splashRot, boolean showEdition) {
		this.logoId = logoId;
		this.logoX = logoX;
		this.logoY = logoY;
		this.logoRot = logoRot;
		this.outlined = outlined;
		this.splashX = splashX;
		this.splashY = splashY;
		this.splashRot = splashRot;
		this.showEdition = showEdition;
	}

	public LogoControl(Optional<Identifier> logoId, Optional<Double> logoX, Optional<Double> logoY, Optional<Double> logoRot, Optional<Boolean> outlined, Optional<Double> splashX, Optional<Double> splashY, Optional<Double> splashRot, Optional<Boolean> showEdition) {
		this.logoId = logoId.orElse(new Identifier("textures/gui/title/minecraft.png"));
		this.logoX = logoX.orElse(0.0D);
		this.logoY = logoY.orElse(0.0D);
		this.logoRot = logoRot.orElse(0.0D);
		this.outlined = outlined.orElse(true);
		this.splashX = splashX.orElse(0.0D);
		this.splashY = splashY.orElse(0.0D);
		this.splashRot = splashRot.orElse(-20.0D);
		this.showEdition = showEdition.orElse(true);
	}

	public Identifier getLogoId() {
		return logoId;
	}

	public double getLogoX() {
		return logoX;
	}

	public double getLogoY() {
		return logoY;
	}

	public double getLogoRot() {
		return logoRot;
	}

	public boolean isOutlined() {
		return outlined;
	}

	public double getSplashX() {
		return splashX;
	}

	public double getSplashY() {
		return splashY;
	}

	public double getSplashRot() {
		return splashRot;
	}

	public boolean doesShowEdition() {
		return showEdition;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LogoControl log) {
			return this.logoId == log.logoId && this.logoX == log.logoX && this.logoY == log.logoY && this.logoRot == log.logoRot && this.outlined == log.outlined && this.splashX == log.splashX && this.splashY == log.splashY && this.splashRot == log.splashRot && this.showEdition == log.showEdition;
		}
		return super.equals(obj);
	}

}
