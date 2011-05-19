package com.alnorth.insolator;

public class InsolationOutput implements CalculatorOutput {

	private double insolation;

	public InsolationOutput(double insolation) {
		this.insolation = insolation;
	}

	public double getInsolation() {
		return insolation;
	}
	
}
