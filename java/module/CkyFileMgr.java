package com.kydsessc.ncm.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;

public final class CkyFileMgr
{
	//private static Activity laucherActivity;
	
	//private static boolean isInited;
	
//	public static void initilize(Activity laucherActivity)
//	{
//		//if( isInited == true ) return;
//		
//		CkyFileMgr.laucherActivity = laucherActivity;
//		//isInited = true;
//		
//	}
	
	
//	public static boolean isExistExternalDirectory(String directoryName)
//	{
//		return isExistFullPath(EXTENRNAL_PATH+"/"+directoryName, true);		
//	}
//	
//	public static boolean isExistExternalFile(String fileName)
//	{
//		return isExistFullPath(EXTENRNAL_PATH+"/"+fileName, false);		
//	}	
	
	public static boolean isExistFilePath(String fullPath)
	{
		return isExistFullPath(fullPath, false);
	}
	public static boolean isExistDirectoryPath(String fullPath)
	{
		return isExistFullPath(fullPath, true);
	}	
	
	public static boolean isExistFullPath(String fullPath, boolean isDirectory)
	{
		if( fullPath != null )
		{
			File file = new File(fullPath);
			
			if( file.exists() == true )
				return ( isDirectory )?file.isDirectory():file.isFile();
		}
		return false;
	}

	public static boolean isExistAndLengthIsNotZero(String filePath)
	{
		if( TextUtils.isEmpty(filePath) == false )
		{
			File file = new File(filePath);
			return file.exists() == true && file.isFile() && file.length() > 0;
		}
		return false;
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
	public static boolean createExternalDirectory(String fullPath)
	{
		return createExternalDirectory(fullPath, false);
	}
	
	
	public static boolean createExternalDirectory(String fullPath, boolean isCreateNomedia)
	{
		File file = new File(fullPath);
		if( file.exists() == true )
		{
			if( file.isDirectory() == true )
				return (isCreateNomedia==true)?createNomediaFile(fullPath):true;
		}
		
		if( file.mkdir() == true )
			return (isCreateNomedia==true)?createNomediaFile(fullPath):true;
		return false;
	}




	public static String getApplicationPath(Context context)
	{
		String applicationPath = context.getApplicationInfo().dataDir;
		if( isExistFullPath(applicationPath, true) == true ) return applicationPath;
		
		applicationPath = context.getFilesDir().getAbsolutePath();
		if( isExistFullPath(applicationPath, true) == true ) return applicationPath;
		
		/**
		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
		    PackageInfo p = m.getPackageInfo(s, 0);
		    s = p.applicationInfo.dataDir;
		} catch (PackageManager.NameNotFoundException e) {
		    Log.w("yourtag", "Error Package name not found ", e);
		}
		 */
		
		return "/data/data/"+context.getPackageName();
	}
	
	public static String getExternalYyyymmddPathIfNotExitCreate(Context context, int yyyymmdd)
	{
		//Calendar calendar = Calendar.getInstance();
		//int yyyymmdd = calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
		return getAppDataRootSubDirPath(context, String.valueOf(yyyymmdd), true);
	}

	/*
	public static String getExtAndroidDataAppSubDir(Context context, String folder)
	{
		File dirPath = null;
		if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true )
			dirPath = context.getExternalFilesDir(folder);
		else
			dirPath = context.getFilesDir();
		return ( dirPath != null && dirPath.exists() == true )?dirPath.getAbsolutePath() : null;
	}
	*/

	public static String getExternalPathIfNotExitCreate(Context context, String folder)
	{
		File dirPath = null;
		if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true )
			dirPath = context.getExternalFilesDir(folder);
		else
			dirPath = context.getFilesDir();

		if( dirPath != null )
		{
			if (dirPath.exists() == true) return dirPath.getAbsolutePath();
			return (dirPath.mkdir() == true) ? dirPath.getAbsolutePath() : null;
		}
		return null;
	}

	private static final boolean IS_내장메모리사용 = true;
	public static String getAppDataRootPath(Context context)
	{
		if( IS_내장메모리사용 == true )
			return context.getFilesDir().getPath();
		else
			return context.getExternalFilesDir("").getPath();
	}

	public static String getAppDataRootSubDirPath(Context context, String dirName, boolean isIfNotExistCreate)
	{
		String rootSubPath;
		if( .IS_내장메모리사용 == true )
		{
			rootSubPath = context.getFilesDir().getPath();
			if( rootSubPath.endsWith(File.separator) == false )
				rootSubPath = rootSubPath + File.separator;
			rootSubPath = rootSubPath + dirName;
		}
		else
			rootSubPath = context.getExternalFilesDir(dirName).getPath();

		if( rootSubPath.endsWith(File.separator) == false )
			rootSubPath = rootSubPath + File.separator;

		if( isIfNotExistCreate == true )
		{
			File dir = new File(rootSubPath);
			if( dir.exists() == false )
				dir.mkdir();
			createNomediaFile(rootSubPath);
		}
		return rootSubPath;
	}


/*	public static String getExtAndroidDataAppSubDirIfNotExitCreate(Context context, String folder)
	{
		File dirPath = null;
		if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true )
			dirPath = context.getExternalFilesDir(folder);
		else
			dirPath = context.getFilesDir();

		if( dirPath != null )
		{
			String dirAbsolutePath = dirPath.getAbsolutePath();
			if( dirAbsolutePath.endsWith(File.separator) == false )
				dirAbsolutePath = dirAbsolutePath + File.separator;

			if (dirPath.exists() == true)
				return dirAbsolutePath;

			if( dirPath.mkdir() == true )
			{
				createNomediaFile(dirAbsolutePath);
				return dirAbsolutePath;
			}
		}
		return null;
	}*/

	public static String getExtRootDir(Context context)
	{
		//File externalDir = Environment.getExternalStorageDirectory();
		//return externalDir.getAbsolutePath();
		return context.getExternalFilesDir(null).getAbsolutePath();
	}
	public static String getExtRootSubDirIfNotExitCreate(Context context, String dirName, boolean isNomediaFile)
	{
		//String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android/data/" + getPackageName();
		String dirPath = context.getExternalFilesDir(dirName).getAbsolutePath();
		if( dirPath.endsWith(File.separator) == false )
			dirPath += File.separator;

		//Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android/data/" + context.getApplicationContext().getPackageName();

		File wantDir = new File(dirPath);
		if( wantDir.exists() == false )
		{
			wantDir.mkdir();
			if( isNomediaFile == true )
				createNomediaFile(dirPath);
		}
		return dirPath;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static File getExternalCacheDir(Context context)
	{
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO )
		{
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD )
			{
				return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) == true ||
						Environment.isExternalStorageRemovable()==false)? 
						context.getExternalCacheDir() : context.getCacheDir();
			}
			else
				return context.getCacheDir();
		}
	    else
	    {
	    	return new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache");
	    }
	}

	public static String getExternalCacheFilePath(Context context)
	{
		File cacheFilePath = getExternalCacheDir(context);
		return cacheFilePath.getAbsolutePath() + File.separator;
	}

	public static String getExternalCacheDirFilePath(Context context, String filename)
	{
		File cacheFilePath = getExternalCacheDir(context);
		String dirPath = cacheFilePath.getAbsolutePath();
		return (filename.startsWith(File.separator))?dirPath+filename : dirPath +File.separator + filename;
	}

	public static boolean createNomediaFile(String fullPath)
	{
		if( fullPath.charAt(fullPath.length() - 1) == File.separatorChar )
			fullPath += ".nomedia";
		else
			fullPath += File.separator + ".nomedia";

		File file = new File(fullPath);
		if( file.exists() == true ) return true;

		try
		{
			return file.createNewFile();
		}
		catch (Exception e)
		{
			CkyLog.e(e);
		}
		return false;
	}


	public static String createCacheFilePath(Context context)
	{
		File cacheFilePath = getExternalCacheDir(context);
		return cacheFilePath.getAbsolutePath() + File.separator + "";
	}
	
	public static String createCacheFileFullPath(Context context, String fileNameAndExt)
	{
		return createCacheFilePath(context) + fileNameAndExt;
	}
	
	public static File createTempFile(Context context, String prefix, String extension)
	{
		return createTempFile(getExternalCacheDir(context), prefix, extension);
	}

	public static File createTempFile(File dir, String prefix, String extension)
	{
		try
		{
			return File.createTempFile(prefix, extension, dir);
		}
		catch( IOException e )
		{
			CkyLog.e(e);
		}
		return null;
	}	
	
	public static boolean deleteFile(String fullPath)
	{
		if( fullPath != null )
		{
			File file = new File(fullPath);
			if( file.exists() == true && file.isFile() == true )
			{
				//CkyLog.d("[CkyFileMgr]deleteFile="+fullPath);
				return file.delete();
			}
		}
		return false;
	}
	
	public static void deleteFiles(File rootDir) 
	{
		if( rootDir == null || rootDir.exists() == false )
			return;
		
	    for (File file : rootDir.listFiles()) 
	    {
	        if( file.isFile() == true ) 
	        {
	            if( file.exists() == true ) 
            		file.delete();
	        }
	        else
	        {
	        	deleteFiles(file);
	        }
	    }
	}	
	
	public static boolean checkCopyFile(int rawResId, String targetPath)
	{
		if( isExistFullPath(targetPath, false) == false )
			return copyFile(rawResId, targetPath);
		return true;
	}
	
	private static byte[] rawFileBuffer;
	
	public static void prepareRawFileBuffer()
	{
		if( rawFileBuffer == null ) rawFileBuffer = new byte[8*1024];
	}
	
	public static void releaseRawFileBuffer()
	{
		rawFileBuffer = null;
	}	

	
	public static boolean copyFile(String sourcePath, String targetPath)
	{
		return copyFile(new File(sourcePath), new File(targetPath));
	}
	
	public static boolean copyFile(File source, File target)
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel inChannel = null, outChannel = null; 
     
		 try     
		 {
			 fis = new FileInputStream(source);
			 fos = new FileOutputStream(target);
			 inChannel = fis.getChannel();     
			 outChannel = fos.getChannel();			 
			 
			 long transferSize = inChannel.size();
			 return transferSize <= inChannel.transferTo(0, transferSize, outChannel);

			 //outChannel.transferFrom(inChannel, 0, inChannel.size());
			 //return true;
		 }     
		 catch( Exception e)
		 {
			 CkyLog.e(e);
		 }
		 finally     
		 {         
			 CkyUtil.close(inChannel);
			 CkyUtil.close(outChannel);
			 
			 CkyUtil.close(fis);
			 CkyUtil.close(fos);			 
		 }
		 return false;
	}
	
	public static void copyStream(InputStream is, OutputStream os)
	{
	    final int bufferSize = 1024;
	    int count = 0, readed;
	    
	    try
	    {
	        byte[] bytes = new byte[bufferSize];
	        while( true )
	        {
	        	if( (readed = is.read(bytes, 0, bufferSize)) == -1 ) 
	        		break;
	        	
	        	os.write(bytes, 0, readed);
	        	count += count;
	        }
	    }
	    catch(Exception ex) {
	        ex.printStackTrace();
	    }
	}	
	
	public static boolean moveFile(String sourcePath, String targetPath)
	{
		return moveFile(new File(sourcePath), new File(targetPath));
	}
	
	public static boolean moveFile(File source, File target)
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel inChannel = null, outChannel = null; 
	     
		 try     
		 {
			 fis = new FileInputStream(source);
			 fos = new FileOutputStream(target);
			 inChannel = fis.getChannel();     
			 outChannel = fos.getChannel();			 
			 
			 long transferSize = inChannel.size();
			 if( transferSize <= inChannel.transferTo(0, transferSize, outChannel) )
			 {
				 source.delete();
				 
				 //CkyLog.d("CkyFileMgr moveFile �Ϸ�");
				 return true;
			 }
		 }     
		 catch( Exception e)
		 {
			 CkyLog.e(e);
		 }
		 finally     
		 {         
			 CkyUtil.close(inChannel);
			 CkyUtil.close(outChannel);
			 
			 CkyUtil.close(fis);
			 CkyUtil.close(fos);			 
		 }
		 return false;
	}	

	public static String uriToPath(Context context, Uri uri)
	{
		if( uri != null )
		{
			String scheme = uri.getScheme();  //����ŵ:content://com.google.android.keep/blob/image/21
			
			if( scheme.equalsIgnoreCase(ContentResolver.SCHEME_CONTENT) == true )
			{
				final String[] projection = { MediaStore.Images.Media.DATA };
				try {
					Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
					if( cursor != null && cursor.moveToFirst() == true ) {
						int comunIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						return cursor.getString(comunIndex);
					}
				} catch (Exception e) {
					CkyLog.e(e);
				}
			}
			else if( scheme.equalsIgnoreCase(ContentResolver.SCHEME_FILE) == true )
				return uri.getPath();
		}
		return null;
	}
	
	public static boolean isExistUri(Context context, Uri uri)
	{
		String filePath = uriToPath(context, uri);
		return filePath != null && isExistFullPath(filePath, false);
	}
	
	
	public static String getFileNameExtFromFullPath(String fullPath)
	{
		if( fullPath != null )
		{
			int offset = fullPath.lastIndexOf('/');
			if( offset >= 0 )
				return fullPath.substring(offset+1);//, fullPath.length());
		}
		return fullPath;
	}

	public static String getFileExtFromFullPath(String filePath)
	{
		if( TextUtils.isEmpty(filePath) == false )
		{
			try
			{
				int offset = filePath.lastIndexOf(".");
				if( offset != -1 )
					return filePath.substring(offset + 1);
			}
			catch (Exception e)
			{
			}
		}
		return null;
	}
	
	public static String getFileNameFromFullPath(String fullPath)
	{
		if( fullPath == null ) return null;
		
	    int offsetDelim = fullPath.lastIndexOf('/');
	    
	    int offsetDot = fullPath.lastIndexOf('.');
	    
	    if( offsetDelim >= 0 )
	    {
	    	if( offsetDot > 0 )
	    		return fullPath.substring(offsetDelim+1, offsetDot);
	    	else
	    		return fullPath.substring(offsetDelim+1, fullPath.length());
	    }
	    else if( offsetDot > 0 )
	    	return fullPath.substring(0, offsetDot);
	    return fullPath;
	}


	
	public static String getFileDirectoryInFullPath(String fullPath)
	{
		if(TextUtils.isEmpty(fullPath) == false)
		{
			int offset = fullPath.lastIndexOf('/');
			if (offset != -1 && offset != fullPath.length() - 1)
			{
				fullPath = fullPath.substring(0, offset);
			}
		}
		return fullPath;
	}
	
	public static long getFileSize(String filePath)
	{
		if( filePath == null ) return 0;
		
		File file = new File(filePath);
		return ( file.exists() == true && file.canRead() == true )?file.length() : 0;
	}



	public static boolean checkFileExtension(String filePath, String extensions)
	{
		if( TextUtils.isEmpty(filePath) == false )
		{
			final String REGEXP = "([^\\s]+(\\.(?i)("+extensions+"))$)";
			return Pattern.compile(REGEXP).matcher(filePath).matches();
		}
		return false;
	}

	public static boolean checkFileImageExtension(String filePath)
	{
		return checkFileExtension(filePath, "jpg|png|gif|bmp|jpeg|tiff");
	}
	public static boolean checkFileAudioExtension(String filePath)
	{
		return checkFileExtension(filePath, "mp3|3gp|ogg|mp4|wav|m4a|aac|mid");
	}

	public static boolean writeFileJPG(Bitmap bitmap, String filePath)
	{
		return writeFile(bitmap, CompressFormat.JPEG, 90, filePath);
	}
	
	public static boolean writeFileJPG(Bitmap bitmap, String filePath, int quality)
	{
		return writeFile(bitmap, CompressFormat.JPEG, quality, filePath);
	}	
	
	public static boolean writeFilePNG(Bitmap bitmap, String filePath)
	{
		return writeFile(bitmap, CompressFormat.PNG, 100, filePath);
	}	
	
	//Bitmap.CompressFormat.JPG, 100, "/sdcard/captureScreen.png")
	//Bitmap.CompressFormat.PNG, 100, "/sdcard/captureScreen.png")
	public static boolean writeFile(Bitmap bitmap, CompressFormat format, int quality, String filePath)
	{
		if( bitmap != null && filePath != null )
		{
			FileOutputStream fos = null;

			try 
			{
				 fos = new FileOutputStream(filePath);
				 return bitmap.compress(format, quality, fos);
			} 
			catch( FileNotFoundException e ) 
			{ 
				 CkyLog.e(e);
			}		
			finally
			{
				//CkyLog.d("[CkyFileMgr]JPG��="+mResultFilePath);
				CkyUtil.close(fos);
			}
		}
		return false;		
	}
	
//	public static boolean writeFile(String mResultFilePath, byte[] data)
//	{
//		return ( mResultFilePath != null && data != null )?
//				writeFile(new File(mResultFilePath), data) : false;
//	}
	
	public static boolean writeFile(String filePath, byte[] data, int offset, int length)
	{
		if( filePath != null && /*file.exists()==true && */
			data != null && data.length > 0 )
		{
			FileOutputStream fos = null;
			
			try 
			{
				fos = new FileOutputStream(filePath);
				fos.write(data,offset,length);
				fos.close();
				return true;
			} 
			catch( Exception e ) 
			{
				CkyLog.e(e);
			}
			finally
			{
				CkyUtil.close(fos);
			}			
		}
		return false;
	}		
	/*
	public static boolean writeFile(Activity activity, String fileNameDotExt, byte[] data)
	{
		if( fileNameDotExt != null && data != null && data.length > 0 )
		{
			try 
			{
				FileOutputStream fos = activity.openFileOutput(fileNameDotExt,Context.MODE_WORLD_WRITEABLE);
				fos.write(data);
				fos.close();
				return true;
			} 
			catch( Exception e ) 
			{
			}
		}
		return false;
	}
	*/
	
	public static byte[] readFile(String filePath)
	{
		return (filePath!=null)?readFile(new File(filePath)) : null;
	}

	public static byte[] readFile(File file)
	{
		if( file != null && file.exists() == true && file.canRead() == true )
		{
			try
			{
				return readFile(new FileInputStream(file));
			}
			catch(Exception e)
			{
				CkyLog.e(e);
			}
		}
		return null;
	}	
	
	public static byte[] readFile(Activity activity, String filePath)
	{
		try
		{
			return readFile(activity.openFileInput(filePath));
		}
		catch (Exception e)
		{
			CkyLog.e(e);
		}
		return null;
	}
	
	public static byte[] readFile(FileInputStream fis)
	{
		if( fis != null )
		{
			try
			{
				int fileSize = fis.available(), fileOffset = 0, readedSize;
				
				byte[] data = new byte[fileSize];
				
				while( fileOffset < fileSize && (readedSize = fis.read(data, fileOffset, fileSize-fileOffset)) != -1 )
					fileOffset += readedSize;  

				return data;
			}
			catch(Exception e)
			{
				CkyLog.e(e);
			}
			finally
			{
				CkyUtil.close(fis);
			}			
		}
		return null;
	}
	

	
	/*
	private static String getFileExtFromPathOrUri(String url) {
	    if (url.indexOf("?")>-1) {
	        url = url.substring(0,url.indexOf("?"));
	    }
	    if (url.lastIndexOf(".") == -1) {
	        return null;
	    } else {
	        String ext = url.substring(url.lastIndexOf(".") );
	        if (ext.indexOf("%")>-1) {
	            ext = ext.substring(0,ext.indexOf("%"));
	        }
	        if (ext.indexOf("/")>-1) {
	            ext = ext.substring(0,ext.indexOf("/"));
	        }
	        return ext.toLowerCase();

	    }
	}
	*/
		


	
	public static ArrayList<String> getFilePaths(String dirPath, ArrayList<String> filePaths)
	{
		if( dirPath != null && filePaths != null )
		{
			File file = new File(dirPath);
			
	        if( file.isDirectory() == true )
	        {
	        	getSubFilePaths( file , filePaths);
	        }
	        else
	        {
	        	filePaths.add( file.getAbsolutePath() );
	        }
		}
		return filePaths;
	}
	
    protected static void getSubFilePaths( File directory , ArrayList<String> filePaths) 
    {
    	if( directory == null ) return;
    	
        File[] files = directory.listFiles();
        if( files != null )
        {
	        for( File file : files )
	        {
	            if( file.isDirectory() == true )
	            {
            		getSubFilePaths( file , filePaths);
	            }
	            else
	            {
	            	filePaths.add( file.getAbsolutePath() );
	            }
	        }
        }
    }
    
    //TODO : 배포시 호출하는 부분 모두 주석처리할것
    public static void printFiles(String log, String dirPath)
    {
    	ArrayList<String> files = new ArrayList<String>();
    	getFilePaths(dirPath, files);
    	
    	CkyLog.rr("----------- "+log+" -----------");

    	CkyLog.r("DIR: "+dirPath);
		for( String file : files )
		{
			CkyLog.d("FILE: " + file);
		}
    }
}

//String state = android.os.Environment.getExternalStorageState();
//if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {

