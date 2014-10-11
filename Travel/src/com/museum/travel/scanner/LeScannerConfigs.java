package com.museum.travel.scanner;



public class LeScannerConfigs {
	public static final String SCANNER_SCHEME = "scanner://";
	//public static final LeSharedPrefUnit FIRST_CLICK_SCANNER = new LeSharedPrefUnit(LePrimitiveType.BOOLEAN,
		//	"first_click_scanner_item", true);

	public static final boolean KEY_DECODE_1D = false;
	public static final boolean KEY_DECODE_QR = true;
	public static final boolean KEY_DECODE_DATA_MATRIX = false;

	public static final boolean KEY_AUTO_FOCUS = true;
	public static final boolean KEY_DISABLE_CONTINUOUS_FOCUS = false;

	private LeScannerConfigs() {
	}
}
