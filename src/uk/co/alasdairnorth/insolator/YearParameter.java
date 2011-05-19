package uk.co.alasdairnorth.insolator;

public class YearParameter implements CalculatorParameters {
	
	//AD is positive, BC negative
	private int year;
	
	public YearParameter(int year) {
		this.year = year;
	}
	
	public int getYear() {
		return year;
	}

	@Override
	public String toCacheKey() {
		return Integer.toString(year);
	}

}
