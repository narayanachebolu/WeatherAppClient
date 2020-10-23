package com.java.addict.poc.weather;

public class AppConstants {

	static final String COUNTRY_CODE = "country_code";
	static final String CITY_CODE = "city_code";
	static final String ZIP_CODE = "zip_code";

	static final String CITY_REQUIRED = "city_reqd";
	static final String ZIPCODE_REQUIRED = "zipcode_reqd";
	static final String COUNTRY_CITY_NOT_FOUND = "country_city_not_found";
	static final String INVALID_COUNTRY_CITY = "invalid_country_city";
	static final String VALIDATION_ERROR = "vld_err";

	// resource bundle constants
	final static String RESOURCE_BUNDLE_NAME = "AppMsgs";
	
	// rest API constants
	final static String URL_API = "http://api.openweathermap.org/data/2.5/weather/";
	final static String API_KEY = "PLEASE_CREATE_YOUR_OWN_API_KEY_AND_INSERT_HERE";
	
	// UI label constants
	final static String BY_CITY = "by_city";
	final static String BY_ZIPCODE = "by_zipcode";
	final static String QUIT = "quit";
	final static String GET_WEATHER = "get_weather";

}
