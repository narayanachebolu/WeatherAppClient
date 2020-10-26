/*
 * Copyright (c) 2017 Leonardo Sartori
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.java.addict.poc.weather;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * WeatherApp allows to get the current weather of particular city or zipcode.
 * UI supports localization,
 *
 */
public class WeatherApp {

	private static Logger appLogger = LoggerFactory.getLogger(WeatherApp.class);

	/**
	 * Use this method to get localized message from resource properties.
	 * 
	 * @param msgKey         - key to retrieve localized message
	 * @param resourceBundle - bundle from which resource has to be fetched
	 * @return - localized message if found else NULL
	 */
	public static String getMessage(String msgKey, ResourceBundle resourceBundle) {

		String msg = null;

		if (msgKey == null || msgKey.isEmpty()) {
			appLogger.debug("Message key is either NULL or EMPTY.");
			return null;
		}

		try {
			msg = resourceBundle.getString(msgKey);
		} catch (MissingResourceException mrx) {
			appLogger.error(mrx.getMessage());
		}

		return msg;

	}

	/**
	 * This message to get the resource bundle for a particular Country and
	 * Language.
	 * 
	 * @param languageCode - ISO 2 character country code
	 * @param countryCode  - ISO 2 character country code
	 * @return - ResourceBundle of particular Country or Language
	 */
	public static ResourceBundle getResourceBundle(String languageCode, String countryCode) {

		ResourceBundle resourceBundle = null;

		if ((languageCode == null && countryCode == null) || languageCode == null || countryCode == null) {
			appLogger.debug("Locale is NULL or Invalid. Using default Locale {}", Locale.getDefault());
			resourceBundle = ResourceBundle.getBundle(AppConstants.RESOURCE_BUNDLE_NAME);
		}

		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle(AppConstants.RESOURCE_BUNDLE_NAME,
					new Locale(languageCode, countryCode));
		}

		return resourceBundle;

	}

	/**
	 * Service method to get the weather for particular city or zipcode. Displays
	 * localized weather forecast based on the language code.
	 * 
	 * @param languageCode - 2 character ISO language code
	 * @param countryCode  - 2 character ISO country code
	 */
	public static void getWeather(String languageCode, String countryCode) {

		Client client = ClientBuilder.newClient();

		ResourceBundle resourceBundle = getResourceBundle(languageCode, countryCode);

		// custom modal dialog for CITY or ZIPCODE
		Object[] options1 = { resourceBundle.getString(AppConstants.BY_CITY),
				resourceBundle.getString(AppConstants.BY_ZIPCODE), resourceBundle.getString(AppConstants.QUIT) };

		JPanel panel = new JPanel();

		String city = null, zipcode = null, serviceResponse = null;

		boolean cityIndicator = false, zipCodeIndicator = false;

		panel.add(new JLabel(resourceBundle.getString(AppConstants.COUNTRY_CODE)));
		JTextField textField = new JTextField(20);
		panel.add(textField);

		int result = JOptionPane.showOptionDialog(null, panel, resourceBundle.getString(AppConstants.GET_WEATHER),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);
		if (result == JOptionPane.YES_OPTION) {
			city = JOptionPane.showInputDialog(resourceBundle.getString(AppConstants.CITY_CODE));

			// validates if city was filled
			if (city == null || city.equals(null) || city.isEmpty()) {
				JOptionPane.showMessageDialog(null, resourceBundle.getString(AppConstants.CITY_REQUIRED));
				return;
			}

			appLogger.debug("User has choosen to get weather by City - {}", city);
			cityIndicator = true;
		} else if (result == JOptionPane.NO_OPTION) {
			zipcode = JOptionPane.showInputDialog(resourceBundle.getString(AppConstants.ZIP_CODE));

			// validates if zipcode was filled
			if (zipcode == null || zipcode.equals(null) || zipcode.isEmpty()) {
				JOptionPane.showMessageDialog(null, resourceBundle.getString(AppConstants.ZIPCODE_REQUIRED));
				return;
			}

			appLogger.debug("User has choosen to get weather by Zipcode - {}", zipcode);
			zipCodeIndicator = true;
		} else {
			// cancel option
			appLogger.debug("User has choosen to quit.");
			return;
		}

		String country = textField.getText();

		try {
			// search only by city if country is NULL or EMPTY
			if (country == null || country.equals(null) || country.isEmpty()) {
				appLogger.debug("User has NOT provided Country Code.");
				if (cityIndicator) {
					serviceResponse = client.target(AppConstants.URL_API).queryParam("q", city)
							.queryParam("appid", AppConstants.API_KEY).queryParam("lang", languageCode)
							.request(MediaType.APPLICATION_JSON).get(String.class);
				} else if (zipCodeIndicator) {
					serviceResponse = client.target(AppConstants.URL_API).queryParam("q", zipcode)
							.queryParam("appid", AppConstants.API_KEY).queryParam("lang", languageCode)
							.request(MediaType.APPLICATION_JSON).get(String.class);
				}
			} else {
				appLogger.debug("User has provided Country Code: {}", country);
				if (cityIndicator) {
					serviceResponse = client.target(AppConstants.URL_API)
							.queryParam("q", city.concat(",").concat(country)).queryParam("appid", AppConstants.API_KEY)
							.queryParam("lang", languageCode).request(MediaType.APPLICATION_JSON).get(String.class);
				} else if (zipCodeIndicator) {
					serviceResponse = client.target(AppConstants.URL_API)
							.queryParam("q", zipcode.concat(",").concat(country))
							.queryParam("appid", AppConstants.API_KEY).queryParam("lang", languageCode)
							.request(MediaType.APPLICATION_JSON).get(String.class);
				}
			}
			
			appLogger.debug("REST API Endpoint URL: {}", client.toString());
			appLogger.debug(serviceResponse);
		} catch (javax.ws.rs.NotFoundException n) {
			JOptionPane.showMessageDialog(null, resourceBundle.getString(AppConstants.COUNTRY_CITY_NOT_FOUND),
					resourceBundle.getString(AppConstants.VALIDATION_ERROR), JOptionPane.WARNING_MESSAGE);
			appLogger.error(n.getMessage());
			// n.printStackTrace();
		} catch (javax.ws.rs.BadRequestException b) {
			JOptionPane.showMessageDialog(null, resourceBundle.getString(AppConstants.COUNTRY_CITY_NOT_FOUND),
					resourceBundle.getString(AppConstants.VALIDATION_ERROR), JOptionPane.WARNING_MESSAGE);
			appLogger.error(b.getMessage());
			// b.printStackTrace();
		} finally {
			client.close();
		}

	}

}
