package uk.co.alasdairnorth.insolator;

public interface Calculator<T extends CalculatorParameters, V extends CalculatorOutput> {

	V calculate(T params);
		
}
