package com.example.proto.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

public class Prefs {
	private static final String TVBOGO_CONFIG = "tvbogo_config";
	private static SharedPreferences prefs;

	public static void initPrefs(Context context) {
		if (prefs == null) {
			String key = TVBOGO_CONFIG;			
			prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
		}
	}

	public static SharedPreferences getPreferences() {
		if (prefs != null) {
			return prefs;
		}
		return null;
	}

	public static Map<String, ?> getAll() {
		return getPreferences().getAll();
	}


	public static int getInt(final String key, final int defValue) {
		return getPreferences().getInt(key, defValue);
	}


	public static boolean getBoolean(final String key, final boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}


	public static long getLong(final String key, final long defValue) {
		return getPreferences().getLong(key, defValue);
	}


	public static float getFloat(final String key, final float defValue) {
		return getPreferences().getFloat(key, defValue);
	}


	public static String getString(final String key, final String defValue) {
		return getPreferences().getString(key, defValue);
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static Set<String> getStringSet(final String key,
			final Set<String> defValue) {
		SharedPreferences prefs = getPreferences();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return prefs.getStringSet(key, defValue);
		} else {
			if (prefs.contains(key + "#LENGTH")) {
				HashSet<String> set = new HashSet<String>();
				// Workaround for pre-HC's lack of StringSets
				int stringSetLength = prefs.getInt(key + "#LENGTH", -1);
				if (stringSetLength >= 0) {
					for (int i = 0; i < stringSetLength; i++) {
						prefs.getString(key + "[" + i + "]", null);
					}
				}
				return set;
			}
		}
		return defValue;
	}


	public static void putLong(final String key, final long value) {
		final Editor editor = getPreferences().edit();
		editor.putLong(key, value);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}


	public static void putInt(final String key, final int value) {
		final Editor editor = getPreferences().edit();
		editor.putInt(key, value);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}


	public static void putFloat(final String key, final float value) {
		final Editor editor = getPreferences().edit();
		editor.putFloat(key, value);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}


	public static void putBoolean(final String key, final boolean value) {
		final Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}


	public static void putString(final String key, final String value) {
		final Editor editor = getPreferences().edit();
		editor.putString(key, value);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void putStringSet(final String key, final Set<String> value) {
		final Editor editor = getPreferences().edit();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			editor.putStringSet(key, value);
		} else {
			
			int stringSetLength = 0;
			if (prefs.contains(key + "#LENGTH")) {
				// First read what the value was
				stringSetLength = prefs.getInt(key + "#LENGTH", -1);
			}
			editor.putInt(key + "#LENGTH", value.size());
			int i = 0;
			for (String aValue : value) {
				editor.putString(key + "[" + i + "]", aValue);
				i++;
			}
			for (; i < stringSetLength; i++) {
				// Remove any remaining values
				editor.remove(key + "[" + i + "]");
			}
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}


	public static void remove(final String key) {
		SharedPreferences prefs = getPreferences();
		final Editor editor = prefs.edit();
		if (prefs.contains(key + "#LENGTH")) {
			// Workaround for pre-HC's lack of StringSets
			int stringSetLength = prefs.getInt(key + "#LENGTH", -1);
			if (stringSetLength >= 0) {
				editor.remove(key + "#LENGTH");
				for (int i = 0; i < stringSetLength; i++) {
					editor.remove(key + "[" + i + "]");
				}
			}
		}
		editor.remove(key);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		} else {
			editor.apply();
		}
	}

	public static boolean contains(final String key) {
		return getPreferences().contains(key);
	}
}
