package com.kydsessc.ncm.util;

import android.graphics.Rect;
import android.util.Log;

import com.kydsessc.ncm.core.cfg.NcmCfg;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class CkyLog
{
	private final static String TAG = "--CKY--";
	private final static boolean sIsEnable = true;

	private CkyLog() {}

	private static String toWithDebugInfo(String msg) {
		if( sIsEnable == true && NcmCfg.IS_LOG_DETAIL == true ) {
			StringBuilder buffer = new StringBuilder();
			final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[5];

			buffer.append("[");
			buffer.append(Thread.currentThread().getName());
			buffer.append(": ");
			buffer.append(stackTraceElement.getFileName());
			buffer.append(": ");
			buffer.append(stackTraceElement.getLineNumber());
			buffer.append(": ");
			buffer.append(stackTraceElement.getMethodName());
			buffer.append("()] ");
			buffer.append(msg);
			return buffer.toString();
		}
		return msg;
	}

	public static void d(String log) {
		d(TAG, log);
	}
	
	public static void d(String tag, String log) {
		if( sIsEnable == true && Log.isLoggable(TAG, Log.INFO) )
		android.util.Log.d(tag, log);
	}	
	
	public static void dd(String log) {
		dd(TAG, log);
	}
	
	public static void dd(String tag,String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ ) {
		android.util.Log.d(tag, " ");
		android.util.Log.d(tag, "----------------------------------------");
		android.util.Log.d(tag, "" + toWithDebugInfo(log));
		android.util.Log.d(tag, "----------------------------------------");
		}

	}	
	
	public static void d(Rect rect) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		android.util.Log.d(TAG, (rect!=null)?rect.toString():"rect=null");
	}
	
	public static void d(String log, Rect rect) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		android.util.Log.d(TAG, log +" "+rect.toString());
	}	
	
	public static void d(int width, int height) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		android.util.Log.d(TAG, "W["+width+"] x H:["+height+"]");
	}
	
	public static void d(byte[] datas, String comment) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ ){
		if( datas != null ) {
			if( datas.length > 0 ) {
				if( comment != null ) android.util.Log.d(TAG,comment);
				
				for( int i = 0 ; i < datas.length ; i++ )
					android.util.Log.d(TAG, "["+i+"] = " + datas[i]);
			} else {
				if( comment != null ) android.util.Log.d(TAG,comment +" length=0");
			}
		} else {
			if( comment != null ) android.util.Log.d(TAG,comment +" null");				
		}
		}
	}
	
	public static void d(int[] datas, String comment) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ ){
		if( datas != null ) {
			if( datas.length > 0 ) {
				if( comment != null ) android.util.Log.d(TAG,comment);
				
				for( int i = 0 ; i < datas.length ; i++ )
					android.util.Log.d(TAG, "["+i+"] = " + datas[i]);
			}
			else {
				if( comment != null ) android.util.Log.d(TAG,toWithDebugInfo(comment +" length=0"));
			}
		} else {
			if( comment != null ) android.util.Log.d(TAG,toWithDebugInfo(comment +" null"));
		}
		}
	}	
	
	public static void w(String log) {
		w(TAG, log);
	}
	
	public static void w(String tag, String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.WARN)*/ )
		android.util.Log.w(tag, log);
	}	
	
	public static void ww(String log) {
		ww(TAG, log);
	}
	
	public static void ww(String tag,String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.WARN)*/ ) {
			android.util.Log.w(tag, " ");
			android.util.Log.w(tag, "----------------------------------------");
			android.util.Log.w(tag, "" + toWithDebugInfo(log));
			android.util.Log.w(tag, "----------------------------------------");
		}
	}
	
	public static void i(String log) {
		i(TAG, log);
	}
	
	public static void i(String tag, String log)
	{
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		android.util.Log.i(tag, log);
	}

	public static void ii() { ii(" "); }
	public static void ii(String log)
	{
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		ii(TAG, log);
	}
	
	public static void ii(String tag,String log)
	{
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ ) {
		android.util.Log.i(tag, " ");
		android.util.Log.i(tag, "----------------------------------------");
		android.util.Log.i(tag, "" + toWithDebugInfo(log));
		android.util.Log.i(tag, "----------------------------------------");
		}
	}		
	
	public static void r(String log)
	{
		r(TAG, log);
	}
	
	public static void r(String tag, String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		android.util.Log.e(tag, log);
	}	
	
	public static void rr(String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ )
		rr(TAG, log);
	}
	
	public static void rr(String tag,String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.INFO)*/ ) {
		android.util.Log.e(tag, " ");
		android.util.Log.e(tag, "----------------------------------------");
		android.util.Log.e(tag, "" + toWithDebugInfo(log));
		android.util.Log.e(tag, "----------------------------------------");
		}
	}
	
	public static void e(String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ )
		e(TAG, log);
	}

	public static void e(String tag, String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ )
		android.util.Log.e(tag, "[에러]"+log);
	}
	
	public static void ee(String log) { ee(TAG, log); }
	public static void ee(String tag, String log) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ ){
		android.util.Log.e(tag, " ");
		android.util.Log.e(tag, "[에러]*************************************");
		android.util.Log.e(tag, "[에러]" + toWithDebugInfo(log));
		android.util.Log.e(tag, "[에러]*************************************");
		}
	}


	public static void e(Exception e)  {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ )
		e(TAG, null, e); 
	}
	
	public static void e(String tag, Exception e) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ )
		e(tag, null, e);
	}

	public static void e(String tag, String msg, Throwable t) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ )
		android.util.Log.e(tag, msg, t);
	}

	public static void e(String tag, String log, Exception e) {
		if( sIsEnable == true /*&& Log.isLoggable(TAG, Log.ERROR)*/ ) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exception = sw.toString();
		
		android.util.Log.e(tag, " ");
		android.util.Log.e(tag, "[에러]*************************************");
		if (log != null)
		android.util.Log.e(tag, "[에러]" + toWithDebugInfo(log) + " 예외발생!!!");
		android.util.Log.e(tag, exception);
		android.util.Log.e(tag, "[에러]*************************************");
		}
	}

}
