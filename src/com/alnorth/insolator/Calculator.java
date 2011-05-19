package com.alnorth.insolator;

public interface Calculator<T extends CalculatorParameters, V extends CalculatorOutput> {

	V calculate(T params);
		
}
