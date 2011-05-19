package uk.co.alasdairnorth.insolator;

public class OrbitDataCalculator implements Calculator<LatLonDateParameter, OrbitDataOutput> {
	
	private Calculator<YearParameter, OrbitalParametersOutput> orbitalParamsCalculator;

	public OrbitDataCalculator(Calculator<YearParameter, OrbitalParametersOutput> orbitalParamsCalculator) {
		this.orbitalParamsCalculator = orbitalParamsCalculator;
	}

	@Override
	public OrbitDataOutput calculate(LatLonDateParameter params) {
		OrbitalParametersOutput orbitalParameters = this.orbitalParamsCalculator.calculate(params.getYearAsParameter());
		double EDAYzY = 365.2425d;
		double VE2000 = 79.3125d;
		
		double BSEMI  = Math.sqrt(1 - orbitalParameters.getEccentricity() * orbitalParameters.getEccentricity());
	    double TAofVE = - orbitalParameters.getPerihelion();
	    double EAofVE = Math.atan2(BSEMI* Math.sin(TAofVE), orbitalParameters.getEccentricity() + Math.cos(TAofVE));
	    double MAofVE = EAofVE - orbitalParameters.getEccentricity() * Math.sin(EAofVE);
	      
	    double MA = OrbitalParametersCalculator.modulo(2 * Math.PI *(params.daysFrom2000() - VE2000) / EDAYzY + MAofVE, 2 * Math.PI);
	    double EA  = MA + orbitalParameters.getEccentricity()*(Math.sin(MA) + orbitalParameters.getEccentricity()*Math.sin(2*MA)/2);
	    	    
	    double dEA = 0;
	    do {
	    	dEA = (MA - EA + orbitalParameters.getEccentricity() * Math.sin(EA)) / (1 - orbitalParameters.getEccentricity() * Math.cos(EA));
	       	EA  = EA + dEA;
	    } while(Math.abs(dEA) > Math.pow(1, -10));
	    	    
	    double SUNDIS = 1 - orbitalParameters.getEccentricity() * Math.cos(EA);
	    double TA = Math.atan2(BSEMI * Math.sin(EA), Math.cos(EA) - orbitalParameters.getEccentricity());
	    
	    double SIND = Math.sin(TA-TAofVE) * Math.sin(orbitalParameters.getObliquity());
	    double COSD = Math.sqrt(1 - SIND*SIND);
	    double SUNX = Math.cos(TA-TAofVE);
	    double SUNY = Math.sin(TA-TAofVE) * Math.cos(orbitalParameters.getObliquity());
	    double SLNORO = Math.atan2(SUNY,SUNX);
	    
	    double VEQLON = 2 * Math.PI * VE2000 - Math.PI + MAofVE - TAofVE;
	    double ROTATE = 2 * Math.PI * (params.daysFrom2000() - VE2000)*(EDAYzY+1)/EDAYzY;
	    double SUNLON = OrbitalParametersCalculator.modulo(SLNORO-ROTATE-VEQLON, 2 * Math.PI);
	    if (SUNLON > Math.PI) SUNLON = SUNLON - 2 * Math.PI;
	    double SUNLAT = Math.asin(Math.sin(TA-TAofVE)*Math.sin(orbitalParameters.getObliquity()));
	    
	    double SLMEAN = Math.PI - 2 * Math.PI * (params.daysFrom2000() - Math.floor(params.daysFrom2000()));
	    double EQTIME = OrbitalParametersCalculator.modulo(SLMEAN-SUNLON, 2 * Math.PI);
	    if (EQTIME > Math.PI)  EQTIME = EQTIME - 2 * Math.PI;
	    
	    return new OrbitDataOutput(SIND, COSD, SUNDIS, SUNLON, SUNLAT, EQTIME);
	}
	
	
}
