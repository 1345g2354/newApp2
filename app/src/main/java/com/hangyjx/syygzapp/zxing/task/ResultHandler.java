package com.hangyjx.syygzapp.zxing.task;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

/**
 * A base class for the Android-specific barcode handlers. These allow the app
 * to polymorphically suggest the appropriate actions for each data type.
 * 
 * This class also contains a bunch of utility methods to take common actions
 * like opening a URL. They could easily be moved into a helper object, but it
 * can't be static because the Activity instance is needed to launch an intent.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public class ResultHandler {

	private ParsedResult result;

	public ResultHandler(ParsedResult result) {
		this.result = result;
	}

	/**
	 * Create a possibly styled string for the contents of the current barcode.
	 * 
	 * @return The text to be displayed.
	 */
	public CharSequence getDisplayContents() {
		String contents = result.getDisplayResult();
		return contents.replace("\r", "");
	}

	/**
	 * A convenience method to get the parsed type. Should not be overridden.
	 * 
	 * @return The parsed type, e.g. URI or ISBN
	 */
	public final ParsedResultType getType() {
		return result.getType();
	}


}
