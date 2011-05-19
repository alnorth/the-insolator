package com.alnorth.insolator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JProgressBar;

import com.csvreader.CsvWriter;

public class ExportThread extends Thread {

	private int minYear;
	private int maxYear;
	private int compareToYear;
	private double lat;
	private double lon;
	private int exportTypeIndex;
	private File outputFile;
	private Calculator<LatLonDateParameter, InsolationOutput> insolationCalculator;
	private JProgressBar progressBar;
	private JButton exportButton;
	
	public ExportThread(int minYear, int maxYear, int compareToYear, double lat, double lon, int exportTypeIndex, File outputFile, Calculator<LatLonDateParameter, InsolationOutput> insolationCalculator, JProgressBar progressBar, JButton exportButton) {
		super();
		this.minYear = minYear;
		this.maxYear = maxYear;
		this.compareToYear = compareToYear;
		this.lat = lat;
		this.lon = lon;
		this.exportTypeIndex = exportTypeIndex;
		this.outputFile = outputFile;
		this.insolationCalculator = insolationCalculator;
		this.progressBar = progressBar;
		this.exportButton = exportButton;
	}

	public void run() {
		try {
			if(exportTypeIndex == 0) {
				exportPerDayData();
			} else if(exportTypeIndex == 1) {
				exportPerMonthData();
			} else if(exportTypeIndex == 2) {
				exportPerYearData();
			} else if(exportTypeIndex == 3) {
				exportPerYearWinterSummerData();
			} else if(exportTypeIndex == 4) {
				exportPerMilleniaData();
			} else if(exportTypeIndex == 5) {
				exportPerMilleniaWinterSummerData();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		exportButton.setEnabled(true);
	}
	
	private static double getTotalForDay(int year, int month, int day, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		return getTotalForDay(new Date(year, month, day), lat, lon, calc);
	}
	
	private static double getTotalForDay(Date date, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		LatLonDateParameter params = new LatLonDateParameter(date, lat, lon);
		InsolationOutput output = calc.calculate(params);
		return output.getInsolation();
	}
	
	private static double getTotalForMonth(int year, int month, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		int daysInMonth = Date.numDaysInMonth(month, year);
		for(int day = 1; day <= daysInMonth; day++) {
			total += getTotalForDay(year, month, day, lat, lon, calc);
		}
		return total;
	}
	
	private static double getTotalForYear(int year, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		for(int month = 1; month <= 12; month++) {
			total += getTotalForMonth(year, month, lat, lon, calc);
		}
		return total;
	}
	
	private static double getWinterTotalForYear(int year, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		total += getTotalForMonth(Date.previousYear(year), 12, lat, lon, calc);
		total += getTotalForMonth(year, 1, lat, lon, calc);
		total += getTotalForMonth(year, 2, lat, lon, calc);
		return total;
	}
	
	private static double getSummerTotalForYear(int year, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		total += getTotalForMonth(year, 6, lat, lon, calc);
		total += getTotalForMonth(year, 7, lat, lon, calc);
		total += getTotalForMonth(year, 8, lat, lon, calc);
		return total;
	}
	
	private static double getTotalForYearRange(int minYear, int maxYear, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		for(int year = minYear; year <= maxYear; year++) {
			total += getTotalForYear(year, lat, lon, calc);
		}
		return total;
	}
	
	private static double getWinterTotalForYearRange(int minYear, int maxYear, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		for(int year = minYear; year <= maxYear; year++) {
			total += getWinterTotalForYear(year, lat, lon, calc);
		}
		return total;
	}
	
	private static double getSummerTotalForYearRange(int minYear, int maxYear, double lat, double lon, Calculator<LatLonDateParameter, InsolationOutput> calc) {
		double total = 0;
		for(int year = minYear; year <= maxYear; year++) {
			total += getSummerTotalForYear(year, lat, lon, calc);
		}
		return total;
	}
	
	private static int getWinterDaysForYear(int year) {
		int total = 0;
		total += Date.numDaysInMonth(12, Date.previousYear(year));
		total += Date.numDaysInMonth(1, year);
		total += Date.numDaysInMonth(2, year);
		return total;
	}
	
	private static int getSummerDaysForYear(int year) {
		int total = 0;
		total += Date.numDaysInMonth(6, year);
		total += Date.numDaysInMonth(7, year);
		total += Date.numDaysInMonth(8, year);
		return total;
	}
	
	private static int getWinterDaysForYearRange(int minYear, int maxYear) {
		int total = 0;
		for(int year = minYear; year <= maxYear; year++) {
			total += getWinterDaysForYear(year);
		}
		return total;
	}
	
	private static int getSummerDaysForYearRange(int minYear, int maxYear) {
		int total = 0;
		for(int year = minYear; year <= maxYear; year++) {
			total += getSummerDaysForYear(year);
		}
		return total;
	}
	
	private void exportPerDayData() throws IOException {
		FileWriter sw = new FileWriter(outputFile);
		CsvWriter csvWriter = new CsvWriter(sw, ',');
		
		csvWriter.writeComment("Average sunlight is given in watts per square metre");
		String[] headers = {"year", "month", "day", "latitude", "longitude", "average_sunlight", "compared_to_" + compareToYear};
		csvWriter.writeRecord(headers);
		
		//Used a cached one for the year we're comparing to, but we don't want to use a cache for the other values as there will probably be far too many of them.
		Calculator<LatLonDateParameter, InsolationOutput> cachedInsolationCalculator = new CalculatorCache<LatLonDateParameter, InsolationOutput>(insolationCalculator);
		
		Date date = new Date(minYear, 1, 1);
		
		while(date.getYear() <= maxYear) {
			double insolation = getTotalForDay(date, lat, lon, insolationCalculator);
			double compareToInsolation = getTotalForDay(compareToYear, date.getMonth(), date.getDay(), lat, lon, cachedInsolationCalculator);
						
			String[] dataLine = new String[7];
			dataLine[0] = Integer.toString(date.getYear());
			dataLine[1] = Integer.toString(date.getMonth());
			dataLine[2] = Integer.toString(date.getDay());
			dataLine[3] = Double.toString(lat);
			dataLine[4] = Double.toString(lon);
			dataLine[5] = Double.toString(insolation);
			dataLine[6] = Double.toString(insolation - compareToInsolation);
			csvWriter.writeRecord(dataLine);
			
			if(date.getMonth() == 12 && date.getDay() == date.numDaysInMonth()) {
				progressBar.setValue((int) ((((double) date.getYear() - minYear) / (maxYear - minYear)) * 100));
			}
			
			date = date.getNextDate();
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	private void exportPerMonthData() throws IOException {
		FileWriter sw = new FileWriter(outputFile);
		CsvWriter csvWriter = new CsvWriter(sw, ',');
		
		csvWriter.writeComment("Average sunlight is given in watts per square metre");
		String[] headers = {"year", "month", "latitude", "longitude", "average_sunlight"};
		csvWriter.writeRecord(headers);
		
		int year = minYear;
		int month = 1;
				
		while(year <= maxYear) {
			double insolationTotal = getTotalForMonth(year, month, lat, lon, insolationCalculator);
					
			String[] dataLine = new String[5];
			dataLine[0] = Integer.toString(year);
			dataLine[1] = Integer.toString(month);
			dataLine[2] = Double.toString(lat);
			dataLine[3] = Double.toString(lon);
			dataLine[4] = Double.toString(insolationTotal / Date.numDaysInMonth(month, year));
			csvWriter.writeRecord(dataLine);
			
			if(month == 12) {
				progressBar.setValue((int) ((((double) year - minYear) / (maxYear - minYear)) * 100));
				month = 1;
				year = Date.nextYear(year);
			} else {
				month += 1;				
			}
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	private void exportPerYearData() throws IOException {
		FileWriter sw = new FileWriter(outputFile);
		CsvWriter csvWriter = new CsvWriter(sw, ',');
		
		csvWriter.writeComment("Average sunlight is given in watts per square metre");
		String[] headers = {"year", "latitude", "longitude", "average_sunlight"};
		csvWriter.writeRecord(headers);
		
		int year = minYear;
		
		while(year <= maxYear) {
			double insolationTotal = getTotalForYear(year, lat, lon, insolationCalculator);
						
			String[] dataLine = new String[4];
			dataLine[0] = Integer.toString(year);
			dataLine[1] = Double.toString(lat);
			dataLine[2] = Double.toString(lon);
			dataLine[3] = Double.toString(insolationTotal / Date.numDaysInYear(year));
			csvWriter.writeRecord(dataLine);
			
			progressBar.setValue((int) ((((double) year - minYear) / (maxYear - minYear)) * 100));
		
			year = Date.nextYear(year);
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	private void exportPerYearWinterSummerData() throws IOException {
		FileWriter sw = new FileWriter(outputFile);
		CsvWriter csvWriter = new CsvWriter(sw, ',');
		
		csvWriter.writeComment("Average sunlight is given in watts per square metre. The winter given is that at the start of the year. Seasons are for northern hemisphere (Winter is Dec - Feb, Summer is Jun - Aug).");
		String[] headers = {"year", "latitude", "longitude", "average_sunlight_winter", "average_sunlight_summer"};
		csvWriter.writeRecord(headers);
		
		int year = minYear;
		
		while(year <= maxYear) {
			double winterTotal = getWinterTotalForYear(year, lat, lon, insolationCalculator);
			double summerTotal = getSummerTotalForYear(year, lat, lon, insolationCalculator);
			int winterDays = getWinterDaysForYear(year);
			int summerDays = getSummerDaysForYear(year);
			
			String[] dataLine = new String[5];
			dataLine[0] = Integer.toString(year);
			dataLine[1] = Double.toString(lat);
			dataLine[2] = Double.toString(lon);
			dataLine[3] = Double.toString(winterTotal / winterDays);
			dataLine[4] = Double.toString(summerTotal / summerDays);
			csvWriter.writeRecord(dataLine);
			
			progressBar.setValue((int) ((((double) year - minYear) / (maxYear - minYear)) * 100));
		
			year = Date.nextYear(year);
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	private void exportPerMilleniaData() throws IOException {
		FileWriter sw = new FileWriter(outputFile);
		CsvWriter csvWriter = new CsvWriter(sw, ',');
		
		csvWriter.writeComment("Average sunlight is given in watts per square metre");
		String[] headers = {"start_year", "end_year", "latitude", "longitude", "average_sunlight"};
		csvWriter.writeRecord(headers);
		
		int startYear = minYear;
		
		while(startYear <= maxYear) {
			int endYear = Math.min(startYear + 999, maxYear);
			double insolationTotal = getTotalForYearRange(startYear, endYear, lat, lon, insolationCalculator);
			
			String[] dataLine = new String[5];
			dataLine[0] = Integer.toString(startYear);
			dataLine[1] = Integer.toString(endYear);
			dataLine[2] = Double.toString(lat);
			dataLine[3] = Double.toString(lon);
			dataLine[4] = Double.toString(insolationTotal / Date.numDaysInYearRange(startYear, endYear));
			csvWriter.writeRecord(dataLine);
			
			startYear = Date.nextYear(endYear);
				
			progressBar.setValue((int) ((((double) startYear - minYear) / (maxYear - minYear)) * 100));
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
	private void exportPerMilleniaWinterSummerData() throws IOException {
		FileWriter sw = new FileWriter(outputFile);
		CsvWriter csvWriter = new CsvWriter(sw, ',');
		
		csvWriter.writeComment("Average sunlight is given in watts per square metre. The winter given is that at the start of the year. Seasons are for northern hemisphere (Winter is Dec - Feb, Summer is Jun - Aug).");
		String[] headers = {"start_year", "end_year", "latitude", "longitude", "average_sunlight_winter", "average_sunlight_summer"};
		csvWriter.writeRecord(headers);
		
		int startYear = minYear;
		
		while(startYear <= maxYear) {
			int endYear = Math.min(startYear + 999, maxYear);
			double winterTotal = getWinterTotalForYearRange(startYear, endYear, lat, lon, insolationCalculator);
			double summerTotal = getSummerTotalForYearRange(startYear, endYear, lat, lon, insolationCalculator);
			int winterDays = getWinterDaysForYearRange(startYear, endYear);
			int summerDays = getSummerDaysForYearRange(startYear, endYear);
			
			String[] dataLine = new String[6];
			dataLine[0] = Integer.toString(startYear);
			dataLine[1] = Integer.toString(endYear);
			dataLine[2] = Double.toString(lat);
			dataLine[3] = Double.toString(lon);
			dataLine[4] = Double.toString(winterTotal / winterDays);
			dataLine[5] = Double.toString(summerTotal / summerDays);
			csvWriter.writeRecord(dataLine);
			
			startYear = Date.nextYear(endYear);
				
			progressBar.setValue((int) ((((double) startYear - minYear) / (maxYear - minYear)) * 100));
		}
		csvWriter.flush();
		csvWriter.close();
	}
	
}
