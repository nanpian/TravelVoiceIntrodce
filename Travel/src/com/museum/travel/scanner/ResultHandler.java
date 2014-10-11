package com.museum.travel.scanner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;
import com.museum.travel.R;


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

	private static final String TAG = ResultHandler.class.getSimpleName();

	private static final String GOOGLE_SHOPPER_PACKAGE = "com.google.android.apps.shopper";
	private static final String GOOGLE_SHOPPER_ACTIVITY = GOOGLE_SHOPPER_PACKAGE
			+ ".results.SearchResultsActivity";
	private static final String MARKET_URI_PREFIX = "market://details?id=";
	private static final String MARKET_REFERRER_SUFFIX = "&referrer=utm_source%3Dbarcodescanner%26utm_medium%3Dapps%26utm_campaign%3Dscan";

	private static final String[] EMAIL_TYPE_STRINGS = { "home", "work",
			"mobile" };
	private static final String[] PHONE_TYPE_STRINGS = { "home", "work",
			"mobile", "fax", "pager", "main" };
	private static final String[] ADDRESS_TYPE_STRINGS = { "home", "work" };
	private static final int[] EMAIL_TYPE_VALUES = {
			ContactsContract.CommonDataKinds.Email.TYPE_HOME,
			ContactsContract.CommonDataKinds.Email.TYPE_WORK,
			ContactsContract.CommonDataKinds.Email.TYPE_MOBILE, };
	private static final int[] PHONE_TYPE_VALUES = {
			ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
			ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
			ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
			ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
			ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
			ContactsContract.CommonDataKinds.Phone.TYPE_MAIN, };
	private static final int[] ADDRESS_TYPE_VALUES = {
			ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
			ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK, };
	private static final int NO_TYPE = -1;

	public static final int MAX_BUTTON_COUNT = 4;

	private final ParsedResult result;
	private final Activity activity;
	private final Result rawResult;
	private final String customProductSearch;

	private final DialogInterface.OnClickListener shopperMarketListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			launchIntent(new Intent(Intent.ACTION_VIEW,
					Uri.parse(MARKET_URI_PREFIX + GOOGLE_SHOPPER_PACKAGE
							+ MARKET_REFERRER_SUFFIX)));
		}
	};

	ResultHandler(Activity activity, ParsedResult result) {
		this(activity, result, null);
	}

	ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
		this.result = result;
		this.activity = activity;
		this.rawResult = rawResult;
		this.customProductSearch = parseCustomSearchURL();
	}

	public final ParsedResult getResult() {
		return result;
	}

	final boolean hasCustomProductSearch() {
		return customProductSearch != null;
	}

	final Activity getActivity() {
		return activity;
	}

	/**
	 * Some barcode contents are considered secure, and should not be saved to
	 * history, copied to the clipboard, or otherwise persisted.
	 * 
	 * @return If true, do not create any permanent record of these contents.
	 */
	public boolean areContentsSecure() {
		return false;
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

	final void addPhoneOnlyContact(String[] phoneNumbers, String[] phoneTypes) {
		addContact(null, null, null, phoneNumbers, phoneTypes, null, null,
				null, null, null, null, null, null, null, null, null);
	}

	final void addEmailOnlyContact(String[] emails, String[] emailTypes) {
		addContact(null, null, null, null, null, emails, emailTypes, null,
				null, null, null, null, null, null, null, null);
	}

	final void addContact(String[] names, String[] nicknames,
			String pronunciation, String[] phoneNumbers, String[] phoneTypes,
			String[] emails, String[] emailTypes, String note,
			String instantMessenger, String address, String addressType,
			String org, String title, String[] urls, String birthday,
			String[] geo) {

		// Only use the first name in the array, if present.
		Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT,
				ContactsContract.Contacts.CONTENT_URI);
		intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		putExtra(intent, ContactsContract.Intents.Insert.NAME,
				names != null ? names[0] : null);

		putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME,
				pronunciation);

		int phoneCount = Math.min(phoneNumbers != null ? phoneNumbers.length
				: 0, Contents.PHONE_KEYS.length);
		for (int x = 0; x < phoneCount; x++) {
			putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
			if (phoneTypes != null && x < phoneTypes.length) {
				int type = toPhoneContractType(phoneTypes[x]);
				if (type >= 0) {
					intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
				}
			}
		}

		int emailCount = Math.min(emails != null ? emails.length : 0,
				Contents.EMAIL_KEYS.length);
		for (int x = 0; x < emailCount; x++) {
			putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
			if (emailTypes != null && x < emailTypes.length) {
				int type = toEmailContractType(emailTypes[x]);
				if (type >= 0) {
					intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
				}
			}
		}

		// No field for URL, birthday; use notes
		StringBuilder aggregatedNotes = new StringBuilder();
		if (urls != null) {
			for (String url : urls) {
				if (url != null && url.length() > 0) {
					aggregatedNotes.append('\n').append(url);
				}
			}
		}
		for (String aNote : new String[] { birthday, note }) {
			if (aNote != null) {
				aggregatedNotes.append('\n').append(aNote);
			}
		}
		if (nicknames != null) {
			for (String nickname : nicknames) {
				if (nickname != null && nickname.length() > 0) {
					aggregatedNotes.append('\n').append(nickname);
				}
			}
		}
		if (geo != null) {
			aggregatedNotes.append('\n').append(geo[0]).append(',')
					.append(geo[1]);
		}

		if (aggregatedNotes.length() > 0) {
			// Remove extra leading '\n'
			putExtra(intent, ContactsContract.Intents.Insert.NOTES,
					aggregatedNotes.substring(1));
		}

		putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE,
				instantMessenger);
		putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
		if (addressType != null) {
			int type = toAddressContractType(addressType);
			if (type >= 0) {
				intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE,
						type);
			}
		}
		putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
		putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
		launchIntent(intent);
	}

	private static int toEmailContractType(String typeString) {
		return doToContractType(typeString, EMAIL_TYPE_STRINGS,
				EMAIL_TYPE_VALUES);
	}

	private static int toPhoneContractType(String typeString) {
		return doToContractType(typeString, PHONE_TYPE_STRINGS,
				PHONE_TYPE_VALUES);
	}

	private static int toAddressContractType(String typeString) {
		return doToContractType(typeString, ADDRESS_TYPE_STRINGS,
				ADDRESS_TYPE_VALUES);
	}

	private static int doToContractType(String typeString, String[] types,
			int[] values) {
		if (typeString == null) {
			return NO_TYPE;
		}
		for (int i = 0; i < types.length; i++) {
			String type = types[i];
			if (typeString.startsWith(type)
					|| typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
				return values[i];
			}
		}
		return NO_TYPE;
	}

	final void sendEmail(String address, String subject, String body) {
		sendEmailFromUri("mailto:" + address, address, subject, body);
	}

	// Use public Intent fields rather than private GMail app fields to specify
	// subject and body.
	final void sendEmailFromUri(String uri, String email, String subject,
			String body) {
		Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(uri));
		if (email != null) {
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		}
		putExtra(intent, Intent.EXTRA_SUBJECT, subject);
		putExtra(intent, Intent.EXTRA_TEXT, body);
		intent.setType("text/plain");
		launchIntent(intent);
	}

	final void dialPhone(String phoneNumber) {
		launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ phoneNumber)));
	}

	final void dialPhoneFromUri(String uri) {
		launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
	}

	final void openMap(String geoURI) {
		launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(geoURI)));
	}

	/**
	 * Do a geo search using the address as the query.
	 * 
	 * @param address
	 *            The address to find
	 * @param title
	 *            An optional title, e.g. the name of the business at this
	 *            address
	 */
	final void searchMap(String address, CharSequence title) {
		String query = address;
		if (title != null && title.length() > 0) {
			query += " (" + title + ')';
		}
		launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
				+ Uri.encode(query))));
	}

	final void openURL(String url) {
		// Strangely, some Android browsers don't seem to register to handle
		// HTTP:// or HTTPS://.
		// Lower-case these as it should always be OK to lower-case these
		// schemes.
		if (url.startsWith("HTTP://")) {
			url = "http" + url.substring(4);
		} else if (url.startsWith("HTTPS://")) {
			url = "https" + url.substring(5);
		}
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			launchIntent(intent);
		} catch (ActivityNotFoundException ignored) {
			Log.w(TAG, "Nothing available to handle " + intent);
		}
	}

	final void webSearch(String query) {
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra("query", query);
		launchIntent(intent);
	}


	/**
	 * Like {@link #launchIntent(Intent)} but will tell you if it is not
	 * handle-able via {@link ActivityNotFoundException}.
	 * 
	 * @throws ActivityNotFoundException
	 */
	final void rawLaunchIntent(Intent intent) {
		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			Log.d(TAG, "Launching intent: " + intent + " with extras: "
					+ intent.getExtras());
			activity.startActivity(intent);
		}
	}

	/**
	 * Like {@link #rawLaunchIntent(Intent)} but will show a user dialog if
	 * nothing is available to handle.
	 */
	final void launchIntent(Intent intent) {
		try {
			rawLaunchIntent(intent);
		} catch (ActivityNotFoundException ignored) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.msg_intent_failed);
			builder.setPositiveButton("È·¶¨", null);
			builder.show();
		}
	}

	private static void putExtra(Intent intent, String key, String value) {
		if (value != null && value.length() > 0) {
			intent.putExtra(key, value);
		}
	}

	private String parseCustomSearchURL() {
		return "customProductSearch by gyy";
	}

	final String fillInCustomSearchURL(String text) {
		if (customProductSearch == null) {
			return text; // ?
		}
		try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// can't happen; UTF-8 is always supported. Continue, I guess,
			// without encoding
		}
		String url = customProductSearch.replace("%s", text);
		if (rawResult != null) {
			url = url.replace("%f", rawResult.getBarcodeFormat().toString());
			if (url.contains("%t")) {
				ParsedResult parsedResultAgain = ResultParser
						.parseResult(rawResult);
				url = url.replace("%t", parsedResultAgain.getType().toString());
			}
		}
		return url;
	}

}
