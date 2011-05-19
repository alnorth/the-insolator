package uk.co.alasdairnorth.insolator;

public class InsolationCalculator implements Calculator<LatLonDateParameter, InsolationOutput> {
	
	private Calculator<LatLonDateParameter, OrbitDataOutput> orbitDataCalculator;
	
	public InsolationCalculator(Calculator<LatLonDateParameter, OrbitDataOutput> orbitDataCalculator) {
		this.orbitDataCalculator = orbitDataCalculator;
	}
	
	public static Calculator<LatLonDateParameter, InsolationOutput> getInsolationCalculator() {
		//Set up the chain of calculator objects used to calculate insolation.
		Calculator<YearParameter, OrbitalParametersOutput> obl = new CalculatorCache<YearParameter, OrbitalParametersOutput>(new OrbitalParametersCalculator());
		Calculator<LatLonDateParameter, OrbitDataOutput> odc = new OrbitDataCalculator(obl); // Don't need to cache this, each set of output will only be used once.
		return new InsolationCalculator(odc);
	}

	public InsolationOutput calculate(LatLonDateParameter params) {
		OrbitDataOutput data = orbitDataCalculator.calculate(params);
		
		//COSZIJ from SRLOCAT.FOR
		double SINJ = Math.sin(2 * Math.PI * params.getLat() / 360);
	    double COSJ = Math.cos(2 * Math.PI * params.getLat() / 360);
	    double SJSD = SINJ * data.getSineOfDeclinationAngle();
	    double CJCD = COSJ * data.getCosineOfDeclinationAngle();
	    
	    double COSZT;
	    	    
	    if (SJSD + CJCD <= 0) { //goto 20
	    	//Constant nightime at this latitude
	    	COSZT = 0;
	    } else if(SJSD - CJCD >= 0) { //goto 10
	    	//Constant daylight at this latitude
	        COSZT = SJSD;  //  = ECOSZ/TWOPI
	    } else {
	    	//Compute DAWN and DUSK (at local time) and their sines
	    	double CDUSK = -SJSD/CJCD;
	        double DUSK = Math.acos(CDUSK);
	        double SDUSK = Math.sqrt(CJCD*CJCD-SJSD*SJSD) / CJCD;
	        double DAWN = -DUSK;
	        double SDAWN = -SDUSK;
	        
	    	//Nightime at initial and final times with daylight in between
	        double ECOSZ = SJSD * (DUSK - DAWN) + CJCD *(SDUSK - SDAWN);
	    	COSZT = ECOSZ / (2 * Math.PI);
	    }
	    
	    return new InsolationOutput(1367 * COSZT / Math.pow(data.getDistanceToSun(),2));
	}
}
