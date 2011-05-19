package uk.co.alasdairnorth.insolator;

public class OrbitDataOutput implements CalculatorOutput {

	private double sineOfDeclinationAngle;
	private double cosineOfDeclinationAngle;
	private double distanceToSun;
	private double lonOfPointOnEarthUnderSun;
	private double latOfPointOnEarthUnderSun;
	private double equationOfTime;
	
	public OrbitDataOutput(double sineOfDeclinationAngle,
			double cosineOfDeclinationAngle, double distanceToSun,
			double lonOfPointOnEarthUnderSun, double latOfPointOnEarthUnderSun,
			double equationOfTime) {
		this.sineOfDeclinationAngle = sineOfDeclinationAngle;
		this.cosineOfDeclinationAngle = cosineOfDeclinationAngle;
		this.distanceToSun = distanceToSun;
		this.lonOfPointOnEarthUnderSun = lonOfPointOnEarthUnderSun;
		this.latOfPointOnEarthUnderSun = latOfPointOnEarthUnderSun;
		this.equationOfTime = equationOfTime;
	}

	public double getSineOfDeclinationAngle() {
		return sineOfDeclinationAngle;
	}

	public double getCosineOfDeclinationAngle() {
		return cosineOfDeclinationAngle;
	}

	public double getDistanceToSun() {
		return distanceToSun;
	}

	public double getLonOfPointOnEarthUnderSun() {
		return lonOfPointOnEarthUnderSun;
	}

	public double getLatOfPointOnEarthUnderSun() {
		return latOfPointOnEarthUnderSun;
	}

	public double getEquationOfTime() {
		return equationOfTime;
	}
	
	
	
}
