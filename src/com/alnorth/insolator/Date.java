package com.alnorth.insolator;

public class Date implements Cloneable {

	private int year;
	private int month; // 1 - 12
	private int day; // 1 - 31
	
	private static int[] monthDays = {31,28,31, 30,31,30, 31,31,30, 31,30,31};
	private static int[] monthDaysLeap = {31,29,31, 30,31,30, 31,31,30, 31,30,31};
	
	public Date(int year, int month, int day) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}
	
	public static boolean isLeapYear(int year) {
		if(year % 4 == 0) {
			if(year % 100 == 0) {
				if(year % 400 == 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public boolean isLeapYear() {
		return isLeapYear(year);
	}
	
	public static int numDaysInYearRange(int startYear, int endYear) {
		int totalDays = 0;
		for(int year = startYear; year <= endYear; year++) {
			totalDays += Date.numDaysInYear(year);
		}
		return totalDays;
	}
	
	public static int numDaysInYear(int year) {
		if(isLeapYear(year)) {
			return 366;
		} else {
			return 365;
		}
	}
	
	public int numDaysInYear() {
		return(year);
	}
	
	public static int numDaysInMonth(int month, int year) {
		int[] months;
		if(isLeapYear(year)) {
			months = monthDaysLeap;
		} else {
			months = monthDays;
		}
		
		return months[month - 1];
	}
	
	public int numDaysInMonth() {
		return numDaysInMonth(month, year);
	}
	
	public static int previousYear(int year) {
		if(year == 1) {
			return -1;
		} else {
			return year - 1;	
		}
	}
	
	public static int nextYear(int year) {
		if(year == -1) {
			return 1;
		} else {
			return year + 1;	
		}
	}
	
	private int nextYear() {
		return nextYear(year);
	}
	
	private int nextDay() {
		if(day == numDaysInMonth()) {
			return 1;
		} else {
			return day + 1;			
		}
	}
	
	public Date getNextDate() {
		int nextDateDay = nextDay();
		if(nextDateDay == 1) {
			if(month == 12) {
				return new Date(nextYear(), 1, nextDateDay);
			} else {
				return new Date(year, month + 1, nextDateDay);
			}
		} else {
			return new Date(year, month, nextDateDay);
		}
	}
	
	public String toString() {
		return getYear() + "/" + getMonth() + "/" + getDay();
	}
		
}
