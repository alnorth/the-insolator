package uk.co.alasdairnorth.insolator;

public class LatLonDateParameter implements CalculatorParameters {
	
	private Date date;
	private double lat;
	private double lon;
	
	private YearParameter yearParameter; //Keep a copy of this to avoid creating a new one all the time
	private double daysFrom2000 = 0; //Cache this once it's generated
	
	public LatLonDateParameter(Date date, double lat, double lon) {
		this.date = date;
		this.yearParameter = new YearParameter(date.getYear());
		
		if(Math.abs(lat) > 90) throw new Error("Latitude is outside valid range: " + lat);
		this.lat = lat;
		
		if(lon > 180) lon -= 360;
		if(lon < -180) lon += 360;
		if(Math.abs(lon) > 180) throw new Error("Longitude is outside valid range: " + lat);
		this.lon = lon;
	}

	public int getYear() {
		return date.getYear();
	}

	public int getMonth() {
		return date.getMonth();
	}

	public int getDay() {
		return date.getDay();
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public YearParameter getYearAsParameter() {
		return yearParameter;
	}

	@Override
	public String toCacheKey() {
		return Integer.toString(getYear()) + "_" + Integer.toString(getMonth()) + "_" + Integer.toString(getDay()) + "_" + Double.toString(lat) + "_" + Double.toString(lon);
	}
	
	public double daysFrom2000() {
		if(daysFrom2000 == 0) {
			int JDAY4C = 365*400 + 97; //number of days in 4 centuries
			int JDAY1C = 365*100 + 24;  //number of days in 1 century
			int JDAY4Y = 365*  4 +  1; //number of days in 4 years
			int JDAY1Y = 365; //number of days in 1 year
			int[] JDSUMN = {0,31,59, 90,120,151, 181,212,243, 273,304,334};
			int[] JDSUML = {0,31,60, 91,121,152, 182,213,244, 274,305,335};
			
			double DATE = this.getDay() - 1 + .5 - this.getLon() / 360;
			
			int N4CENT = (int) Math.floor((getYear() - 2000) / 400d);
			int IYR4C = getYear() - 2000 - N4CENT * 400;
			int N1CENT = IYR4C / 100;
		    int IYR1C  = IYR4C - N1CENT * 100;
		    int N4YEAR = IYR1C / 4;
		    int IYR4Y = IYR1C - N4YEAR * 4;
		    int N1YEAR = IYR4Y;
		    double DAY = N4CENT * JDAY4C;
		    
		    if(N1CENT > 0) {//goto 10
		    	DAY = DAY + JDAY1C+1 + (N1CENT-1)*JDAY1C;
		    	if (N4YEAR > 0) {//goto 20
		    		DAY = DAY + JDAY4Y-1 + (N4YEAR-1)*JDAY4Y;
		    		if(N1YEAR > 0) { //goto 200
		    			DAY = DAY + JDAY1Y+1 + (N1YEAR-1)*JDAY1Y;
		    			DAY = DAY + JDSUMN[getMonth() - 1] + DATE;
		    		} else {
		    			DAY = DAY + JDSUML[getMonth() - 1] + DATE;
		    		}
		    	} else {
		    		DAY = DAY + N1YEAR*JDAY1Y;
		    		return DAY + JDSUMN[getMonth() - 1] + DATE;
		    	}
		    } else {
		    	DAY = DAY + N4YEAR*JDAY4Y;
		    	if (N1YEAR > 0) { //goto 200
		    		DAY = DAY + JDAY1Y+1 + (N1YEAR-1)*JDAY1Y;
		    		DAY = DAY + JDSUMN[getMonth() - 1] + DATE;
		    	} else { //goto 100
		    		DAY = DAY + JDSUML[getMonth() - 1] + DATE;
		    	}
		    }
		    daysFrom2000 = DAY;
		}
		return daysFrom2000;
	}

}
