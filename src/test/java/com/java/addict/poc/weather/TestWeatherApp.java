package com.java.addict.poc.weather;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TestWeatherApp {
	ResourceBundle resourceBundle = null;
	
	@BeforeEach
	void init() {
		resourceBundle = WeatherApp.getResourceBundle("de", "DE");
	}
	
	@Nested
	class TestMessages {
		
		@Test
		void testDefaultMessage() {
			resourceBundle = WeatherApp.getResourceBundle(null, null);
			assertEquals("Enter the city:", WeatherApp.getMessage(AppConstants.CITY_CODE, resourceBundle));
		}
		
		@Test
		void testBRMessage() {
			resourceBundle = WeatherApp.getResourceBundle("pt", "BR");
			assertEquals("Digite a cidade: ", WeatherApp.getMessage(AppConstants.CITY_CODE, resourceBundle));
		}
		
		@Test
		void testDEMessage() {
			resourceBundle = WeatherApp.getResourceBundle("de", "DE");
			assertEquals("Betreten Sie die Stadt:", WeatherApp.getMessage(AppConstants.CITY_CODE, resourceBundle));
		}
		
		@Test
		void testINMessage() {
			resourceBundle = WeatherApp.getResourceBundle("en", "IN");
			assertEquals("Enter the city:", WeatherApp.getMessage(AppConstants.CITY_CODE, resourceBundle));
		}
	}

	

	@Test
	void testGetResourceBundle() {
		assertEquals(resourceBundle, resourceBundle);
	}

	@Test
	void testGetWeather() {
		fail("Not yet implemented");
	}

}
