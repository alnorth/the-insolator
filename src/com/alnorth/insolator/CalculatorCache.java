package com.alnorth.insolator;

import java.util.HashMap;
import java.util.Map;

public class CalculatorCache<T extends CalculatorParameters, V extends CalculatorOutput> implements Calculator<T,V> {
	
	private Map<String, V> cache = new HashMap<String, V>();
	private Calculator<T,V> calc;
	private int cacheHit = 0;
	private int cacheMiss = 0;

	public CalculatorCache(Calculator<T,V> calc) {
		this.calc = calc;
	}
	
	public V calculate(T params) {
		String cacheKey = params.toCacheKey();
		if(cache.containsKey(cacheKey)) {
			cacheHit++;
			return cache.get(cacheKey);
		} else {
			cacheMiss++;
			V val = calc.calculate(params);
			cache.put(cacheKey, val);
			return val;
		}
	}

	public int getCacheHit() {
		return cacheHit;
	}

	public int getCacheMiss() {
		return cacheMiss;
	}
	
}
