package com.kydsessc.ncm.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CkyUtil
{
	public static String newString(String str)
	{
		return (str != null) ? new String(str) : null;
	}

	public static String notNullString(String str, String valueIfStrNull) { return (str != null) ? str : valueIfStrNull; }

	public static boolean isEmptyBytes(byte[] data)
	{
		return data == null || data.length == 0;
	}

	public static byte[] cloneBytes(byte[] data)
	{
		return (isEmptyBytes(data) == false) ? data.clone() : null;
	}

	public static boolean isEmptyInts(int[] data)
	{
		return data == null || data.length == 0;
	}

	public static int[] cloneInts(int[] data)
	{
		return (isEmptyInts(data) == false) ? data.clone() : null;
	}

	public static boolean equalsInts(int[] data1, int[] data2)
	{
		if (data1 != null)
		{
			if (data2 != null)
			{
				if (data1 == data2) return true;

				if (data1.length == data2.length)
				{
					for (int i = 0, count = data1.length; i < count; i++)
					{
						if (data1[i] != data2[i]) return false;
					}
					return true;
				}
			}
			return false;
		}
		else
		{
			return data2 == null;
		}
	}

	public static String toEmptyTrimStr(String str)
	{
		return (str != null && str.length() > 0) ? str.trim() : null;
	}

	public static boolean isEmptyStr(String str)
	{
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmptyStr(String str)
	{
		return str != null && str.length() > 0;
	}

	public static boolean isEmptyTrimStr(String str)
	{
		return isEmptyStr((str!=null)?str.trim():str);
	}


	public static boolean isEqualStr(String str1, String str2)
	{
		if (str1 != null)
		{
			if (str2 != null)
				return str1 == str2 || str1.equals(str2);

			return false;
		}
		return str2 == null;
	}

	public static boolean isEqualIgnoreCaseStr(String str1, String str2)
	{
		if (str1 != null)
		{
			if (str2 != null)
				return str1 == str2 || str1.equalsIgnoreCase(str2);

			return false;
		}
		return str2 == null;
	}

	public static boolean isIncludeValueInArray(int[] arrays, int value)
	{
		return findPositionValueInArray(arrays, value) >= 0;
	}

	public static int findPositionValueInArray(final int[] arrays, final int value)
	{
		if (arrays != null && arrays.length > 0)
		{
			for (int i = 0, count = arrays.length; i < count; i++)
			{
				if (arrays[i] == value)
					return i;
			}
		}
		return -1;
	}

	public static boolean isIncludeValueInArray(final String[] arrays, final String value)
	{
		return findPositionValueInArray(arrays, value) >= 0;
	}

	public static boolean isIncludeValueInArray(final String[] arrays, final String value, boolean isIgnoreCase)
	{
		return findPositionValueInArray(arrays, value, isIgnoreCase) >= 0;
	}

	public static int findPositionValueInArray(final String[] arrays, final String value)
	{
		return findPositionValueInArray(arrays, value, false);
	}

	public static int findPositionValueInArray(final String[] arrays, final String value, boolean isIgnoreCase)
	{
		if (arrays != null && arrays.length > 0 && TextUtils.isEmpty(value) == false)
		{
			if (isIgnoreCase == true)
			{
				final String lowerCaseValue = value.toLowerCase();
				for (int i = 0, count = arrays.length; i < count; i++)
				{
					if (arrays[i] == null) continue;

					if (TextUtils.equals(arrays[i].toLowerCase(), lowerCaseValue))
						return i;
				}
			}
			else
			{
				for (int i = 0, count = arrays.length; i < count; i++)
				{
					if (arrays[i] == null) continue;

					if (TextUtils.equals(arrays[i], value))
						return i;
				}
			}
		}
		return -1;
	}


	public static boolean checkValidUrl(String text)
	{
		final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

		if( TextUtils.isEmpty(text) == false )
		{
			Pattern p = Pattern.compile(URL_REGEX);
			Matcher m = p.matcher(text);
			return m.find();
		}
		return false;
	}

	public static String[] extractLinks(String text)
	{
		if( TextUtils.isEmpty(text) == false )
		{
			List<String> links = new ArrayList<String>();
			Matcher m = Patterns.WEB_URL.matcher(text);
			while (m.find())
				links.add(m.group());

			if( links.isEmpty() == false )
				return links.toArray(new String[links.size()]);
		}
		return null;
	}

	public static String extractHost(String url)
	{
		if( TextUtils.isEmpty(url) == false )
		{
			try
			{
				return (new URL(url)).getHost();
			}
			catch (MalformedURLException e)
			{
				CkyLog.e(e);
			}
		}
		return null;
	}


	public static int getBitFlag(int bigFlags, int changeBitFlag, boolean isOR /*true:or  false:xor*/)
	{
		if (isOR == true)
			bigFlags |= changeBitFlag;
		else
			bigFlags &= ~changeBitFlag;
		return bigFlags;
	}

	public static String getTrimTextFromTextView(View textView)
	{
		return (textView != null && textView instanceof TextView) ?
				getTrimTextFromTextView((TextView) textView) : null;
	}

	public static String getTrimTextFromTextView(TextView textView)
	{
		String inputed = null;
		if (textView != null)
		{
			if ((inputed = textView.getText().toString()) != null)
			{
				if ((inputed = inputed.trim()).length() == 0)
					inputed = null;
			}
		}
		return inputed;
	}


	public static String limitString(String source, int limitLength, String attach)
	{
		String result = limitString(source, limitLength);
		return (result == source) ? source : result + attach;
	}

	public static String limitString(String source, int limitLength)
	{
		return (source != null && limitLength > 0 && source.length() > limitLength) ?
				source.substring(0, limitLength) :
				source;
	}

	public static String omitString(String str, Paint paint, int clipWidth, String omit)
	{
		if (str == null)
			return null;

		char[] chars = str.toCharArray();

		int retCount, chkedCount = 0, checkCount = 1;

		while (checkCount < chars.length)
		{
			retCount = paint.breakText(chars, 0, checkCount, clipWidth, null);

			//CkyLog.d(retCount + " == " + chkedCount);
			if (retCount == chkedCount)
			{
				if (chkedCount > 0)
				{
					//CkyLog.d("OMITTED!!!!=>"+(new String(chars, 0, chkedCount) + omit));
					return new String(chars, 0, chkedCount) + omit;
				}
				break;
			}
			else
				chkedCount = checkCount++;
		}
		return str;
	}

	public static int[] splitTokens(String source, String delim, int defaultValue)
	{
		StringTokenizer tokens = new StringTokenizer(source, delim);
		int count = tokens.countTokens();

		int[] tokenInts = new int[count];
		int i = 0;

		while (tokens.hasMoreTokens())
		{
			tokenInts[i++] = parseInt(tokens.nextToken(), defaultValue);
		}
		return tokenInts;
	}

	public static int splitString(String str, Paint paint, int maxWidth, Vector<String> splits)
	{
		char[] chars = str.toCharArray();

		int offset = 0;

		int retCount, chkedCount = 0, nowChkCount = 1;

		if (splits.isEmpty() == false)
			splits.clear();

		while (offset + nowChkCount < chars.length)
		{
			retCount = paint.breakText(chars, offset, nowChkCount, maxWidth, null);

			if (retCount == chkedCount)
			{
				if (chkedCount > 0)
				{
					splits.add(new String(chars, offset, chkedCount));
					offset += chkedCount;
					if (offset >= chars.length)
						break;
					chkedCount = 0;
				}
				else
					break;
			}
			else
				chkedCount = nowChkCount;

			nowChkCount = chkedCount + 1;
		}

		if (chkedCount > 0)
		{
			if (offset == 0 && chkedCount == chars.length)
				return 1; //�Ѷ����̰� ���ٸ� ���ʹ� 0�̰� ���ϰ��� 1�̴�

			splits.add(new String(chars, offset, chkedCount + 1));
		}
		return splits.size();
	}

	public static int splitString(String str, Paint paint, int maxWidth, String[] splits)
	{
		char[] chars = str.toCharArray();

		int offset = 0, splitCount = 0;

		int retCount, chkedCount = 0, nowChkCount = 1;

		while (offset + nowChkCount < chars.length)
		{
			retCount = paint.breakText(chars, offset, nowChkCount, maxWidth, null);

			if (retCount == chkedCount)
			{
				if (chkedCount > 0)
				{
					if (splitCount < splits.length)
						splits[splitCount++] = new String(chars, offset, chkedCount);

					offset += chkedCount;
					if (offset >= chars.length)
						break;
					chkedCount = 0;
				}
				else
					break;
			}
			else
				chkedCount = nowChkCount;

			nowChkCount = chkedCount + 1;
		}

		if (chkedCount > 0)
		{
			if (offset == 0 && chkedCount == chars.length)
			{
				splits[0] = null;
				return 1; //�Ѷ����̰� ���ٸ� ���ʹ� 0�̰� ���ϰ��� 1�̴�
			}

			if (splitCount < splits.length)
				splits[splitCount++] = new String(chars, offset, chkedCount + 1);
		}
		return splitCount;
	}

	public static String getPlainTextFromHtmlText(String src)
	{
		if (src == null) return null;

		StringBuilder sb = new StringBuilder();

		boolean isInTag = false;

		for (int i = 0, count = src.length(); i < count; i++)
		{
			char ch = src.charAt(i);
			if (isInTag == false)
			{
				if (ch == '<')
					isInTag = true;
				else
					sb.append(ch);
			}
			else if (ch == '>')
				isInTag = false;
		}
		return sb.toString();
	}

	public static int parseInt(String str, int charIndex, int defaultValue)
	{
		if (str != null)
		{
			try
			{
				int ch = str.charAt(charIndex);
				if (Character.isDigit(ch) == true)
					return ch - '0';
			}
			catch (Exception e)
			{
			}
		}
		return defaultValue;
	}

	public static int parseInt(String str, int defaultValue)
	{
		if (str != null)
		{
			try
			{
				return Integer.parseInt(str);
			}
			catch (Exception e)
			{
			}
		}
		return defaultValue;
	}

	public static long parseLong(String str, long defaultValue)
	{
		if (str != null)
		{
			try
			{
				return Long.parseLong(str);
			}
			catch (Exception e)
			{
			}
		}
		return defaultValue;
	}

	public static float parseFloat(String str, float defaultValue)
	{
		if (str != null)
		{
			try
			{
				return Float.parseFloat(str);
			}
			catch (Exception e)
			{
			}
		}
		return defaultValue;
	}

	public static double parseDouble(String str, double defaultValue)
	{
		if (str != null)
		{
			try
			{
				return Double.parseDouble(str);
			}
			catch (Exception e)
			{
			}
		}
		return defaultValue;
	}


	public static JSONObject getJsonObj(String json)
	{
		if (TextUtils.isEmpty(json) == false)
		{
			try
			{
				return new JSONObject(json);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}

		}
		return new JSONObject();
	}
	public static JSONObject getUnsafeJsonObj(JSONObject jsonObj, String key) throws Exception
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			return jsonObj.getJSONObject(key);
		}
		return null;
	}

	public static int getJsonInt(JSONObject jsonObj, String key, int defaultValue)
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			try
			{
				return jsonObj.getInt(key);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return defaultValue;
	}
	public static int getUnsafeJsonInt(JSONObject jsonObj, String key, int defaultValue) throws Exception
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			return jsonObj.getInt(key);
		}
		return defaultValue;
	}

	public static int getJsonInt(String json, String key, int defaultValue)
	{
		if (TextUtils.isEmpty(json) == false)
		{
			try
			{
				JSONObject jsonObj = new JSONObject(json);
				if (jsonObj.has(key) == true)
					return jsonObj.getInt(key);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return defaultValue;
	}

	public static long getJsonLong(JSONObject jsonObj, String key, long defaultValue)
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			try
			{
				return jsonObj.getLong(key);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return defaultValue;
	}
	public static long getUnsafeJsonLong(JSONObject jsonObj, String key, long defaultValue) throws Exception
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			return jsonObj.getLong(key);
		}
		return defaultValue;
	}

	public static long getJsonLong(String json, String key, long defaultValue)
	{
		if (TextUtils.isEmpty(json) == false)
		{
			try
			{
				JSONObject jsonObj = new JSONObject(json);
				if (jsonObj.has(key) == true)
					return jsonObj.getLong(key);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return defaultValue;
	}


	public static String getJsonStr(JSONObject jsonObj, String key, String defaultValue)
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			try
			{
				return jsonObj.getString(key);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return defaultValue;
	}
	public static String getUnsafeJsonStr(JSONObject jsonObj, String key, String defaultValue) throws Exception
	{
		if (jsonObj != null && jsonObj.has(key) == true)
		{
			return jsonObj.getString(key);
		}
		return defaultValue;
	}


	public static String getJsonStr(String json, String key, String defaultValue)
	{
		if (TextUtils.isEmpty(json) == false)
		{
			try
			{
				JSONObject jsonObj = new JSONObject(json);
				if (jsonObj.has(key) == true)
					return jsonObj.getString(key);
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return defaultValue;
	}

	public static boolean isEmptyJson(JSONObject jsonObject)
	{
		return jsonObject == null || jsonObject.length() == 0;
	}

	public static int getVersionNum(String versionText)
	{
		if (versionText == null) return -1;

		int versionNum = 0;
		StringTokenizer tokens = new StringTokenizer(versionText, ".");
		while (tokens.hasMoreTokens())
		{
			if (versionNum == 0)
				versionNum += parseInt(tokens.nextToken(), 0);
			else
			{
				versionNum *= 100;
				versionNum += parseInt(tokens.nextToken(), 0);
			}
		}
		return versionNum;
	}


	public static String toSQLInjectionFilter(String text)
	{
		if (TextUtils.isEmpty(text) == false)
		{
			text = replaceForSQLInjection(text, "&", " ");
			text = replaceForSQLInjection(text, "null", " ");
			text = replaceForSQLInjection(text, "select", " ");
			text = replaceForSQLInjection(text, "&apos;", "&apos;&apos;");
			text = replaceForSQLInjection(text, "||", " ");
			text = replaceForSQLInjection(text, "--", " ");
			text = replaceForSQLInjection(text, "(", " ");
			text = replaceForSQLInjection(text, ")", " ");
			text = replaceForSQLInjection(text, "from", " ");
			text = replaceForSQLInjection(text, ",", " ");

			text = replaceForSQLInjection(text, "?", "");        // 2012.10.18
			text = replaceForSQLInjection(text, "'", "''");
			text = replaceForSQLInjection(text, "\"", "\"\"");
			text = replaceForSQLInjection(text, "\\", "\\\\");
			text = replaceForSQLInjection(text, ";", "");
			text = replaceForSQLInjection(text, "#", "");
		}
		return text;
	}

	public static String replaceForSQLInjection(String str, String pattern, String replace)
	{
		int s = 0;
		int e;
		StringBuilder result = null;
		while ((e = str.indexOf(pattern, s)) >= 0)
		{
			if (result == null) result = new StringBuilder();

			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}

		if (result != null)
		{
			result.append(str.substring(s));
			return result.toString();
		}
		return str;
	}


	//----------------------------------------------------------------------
	//클립보드 
	//----------------------------------------------------------------------	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static String pasteTextFromClipboard(Context context)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			CharSequence charSequence = clipboard.getText();
			return (charSequence != null) ? toEmptyTrimStr(charSequence.toString()) : null;
		}
		else
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = clipboard.getPrimaryClip();
			android.content.ClipData.Item clipItem = clip.getItemAt(0);
			return clipItem.getText().toString();
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static boolean copyTextToClipboard(Context context, String text)
	{
		if (CkyUtil.isEmptyStr(text) == true) return false;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		}
		else
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

			android.content.ClipData clip = android.content.ClipData.newPlainText("text", text);
			clipboard.setPrimaryClip(clip);
		}
		return true;
	}

	public static boolean copyImageToClipboard(@NonNull Context context, @NonNull String imageFilePath)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
				CkyFileMgr.isExistFilePath(imageFilePath) == true)
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

			ContentValues values = new ContentValues(2);
			values.put(MediaStore.Images.Media.MIME_TYPE, "Image/*");
			values.put(MediaStore.Images.Media.DATA, imageFilePath);

			ContentResolver cr = context.getContentResolver();
			Uri imageUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			clipboard.setPrimaryClip(ClipData.newUri(cr, "Image", imageUri));
			return true;
		}
		return false;
	}

	//----------------------------------------------------------------------	


	public static String getStringExtra(Intent intent, String key, String defaultValue)
	{
		String value = intent.getStringExtra(key);
		return (value != null) ? value : defaultValue;
	}

	public static void setText(View parentView, int id, String value)
	{
		TextView textView = (TextView) parentView.findViewById(id);
		if (textView != null)
			textView.setText(value);
	}

	public static void sleep(long delay)
	{
		try
		{
			Thread.sleep(delay);
		}
		catch (Exception e)
		{
		}
	}

	public static void close(Cursor cursor)
	{
		if (null != cursor && cursor.isClosed() == false)
		{
			try
			{
				cursor.close();
			}
			catch (SQLException e)
			{
				CkyLog.e(e);
			}
		}
	}

	public static void close(FileChannel fc)
	{
		if (fc != null)
		{
			try
			{
				fc.close();
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
	}

	public static void close(InputStream is)
	{
		if (is != null)
		{
			try
			{
				is.close();
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
	}

	public static void close(OutputStream os)
	{
		if (os != null)
		{
			try
			{
				os.close();
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
	}

	public static String getVersion(Context context)
	{
		if (context != null)
		{
			try
			{
				PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				return pi.versionName;
			}
			catch (Exception e)
			{
			}
		}
		return "0.0.0";
	}

	public static int getVersionCode(Context context)
	{
		int versionCode = 0;

		if (context != null)
		{
			try
			{
				PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				return pi.versionCode;
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return versionCode;
	}

	public static String[] getVersionAndCode(Context context)
	{
		if (context != null)
		{
			try
			{
				PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				return new String[]{pi.versionName, String.valueOf(pi.versionCode)};
			}
			catch (Exception e)
			{
				CkyLog.e(e);
			}
		}
		return new String[]{"0.0.0", "0"};
	}

	public static String[] getMajorMinorPatchVersion(Context context)
	{
		String versionName = getVersion(context);
		return versionName.split("\\.");
	}


	public static void setScreenOrientationBySettings(Activity activity)
	{
		if( activity == null ) return;

		final int 자동회전_OFF = 0;
		final int 자동회전_ON = 1;
		int result = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 자동회전_OFF);
		if( result == 자동회전_OFF )
		{
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
//			int orientation = activity.getRequestedOrientation();
//			int rotation = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
//			switch (rotation) {
//			case Surface.ROTATION_0:
//				orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//				break;
//			case Surface.ROTATION_90:
//				orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//				break;
//			case Surface.ROTATION_180:
//				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//				break;
//			default:
//				orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//				break;
//			}
//			activity.setRequestedOrientation(orientation);
		}
		else if( result == 자동회전_ON )
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}




	    
	public final static int NETSTATE_NONE = 0;
	public final static int NETSTATE_WIFI = 0X001;
	public final static int NETSTATE_3G_LTE = 0X002;
	public final static int NETSTATE_WIBRO = 0X004;
	public final static int NETSTATE_3G_WIFI = NETSTATE_WIFI|NETSTATE_3G_LTE;
	public final static int NETSTATE_3G_WIBRO_WIFI = NETSTATE_WIFI|NETSTATE_3G_LTE|NETSTATE_WIBRO;
	
	public static boolean isNetworkStateConnected(Context context) { return getNetworkState(context)!=NETSTATE_NONE; }
	
	public static boolean isNetworkStateDisconnected(Context context) { return getNetworkState(context)==NETSTATE_NONE; }  
	
	public static int getNetworkState(Context context) 
	{ 
		int networkState = NETSTATE_NONE;
		  
		ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 

		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if( networkInfo != null && (networkInfo.isConnected() == true || networkInfo.isAvailable() == true) ) 
			networkState |= NETSTATE_WIFI; 
 
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if( networkInfo != null && (networkInfo.isConnected() == true || networkInfo.isAvailable() == true) ) 
			networkState |= NETSTATE_3G_LTE;
		
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
		if( networkInfo != null && (networkInfo.isConnected() == true || networkInfo.isAvailable() == true) ) 
			networkState |= NETSTATE_WIBRO;		
 
		return networkState; 
	}  
	
	public static String getSizeByDiskSpaceFormat(long size)
	{
		String unit;
		if( size > 1024 )
		{
			size = (long)((float)size / 1024.f);
			if( size > 1024 )
			{
				size = (long)((float)size / 1024.f);
				if( size > 1024 )
				{
					size = (long)((float)size / 1024.f);
					unit = " GB";
				}
				else
					unit = " MB";
			}
			else
				unit = " KB";
		}
		else
			unit = " Byte";
		
		return size + unit;
		//return String.format("%d%s", size, unit);
	}	
	
	public static void vibrate(Context context, long milliseconds)
	{
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(500);
	}

}
