package com.alnorth.insolator;

public class OrbitalParametersOutput implements CalculatorOutput {

	private double eccentricity;
	private double perihelion;
	private double obliquity;
	
	public OrbitalParametersOutput(double eccentricity, double perihelion,
			double obliquity) {
		super();
		this.eccentricity = eccentricity;
		this.perihelion = perihelion;
		this.obliquity = obliquity;
	}

	public double getEccentricity() {
		return eccentricity;
	}

	public double getPerihelion() {
		return perihelion;
	}

	public double getObliquity() {
		return obliquity;
	}

}
