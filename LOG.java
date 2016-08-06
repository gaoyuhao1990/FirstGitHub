
package com.android.cuppos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public class LOG {
	private static boolean mDebug = true;
	
	public static boolean isSdCardExist() {  
	    return Environment.getExternalStorageState().equals(  
	            Environment.MEDIA_MOUNTED);  
	}
	
	public static void writeLog(String str) {  
		if(!isSdCardExist()){
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);   
		SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss", Locale.CHINA); 
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间 
		String fileName = "Log_" + formatter.format(curDate) + ".txt";
	    try{
	    	File dic = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LOG");
	    	if(!dic.exists()){
	    		dic.mkdir();
	    	}
	    	else{
	    		if(!dic.isDirectory()){
	    			dic.delete();
	    			dic.mkdir();
	    		}
	    	}
		    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LOG/" + fileName); 
		    if (!file.exists()) {  
		    	file.createNewFile();
		    }
		    String ss = "[" + formatter2.format(curDate) + "] " + str + "\n";
		    FileOutputStream fos = new FileOutputStream(file,true); 
		    fos.write(ss.getBytes());
		    fos.close();
	    }
	    catch(IOException e){
	    	e.printStackTrace();
	    }
	}
	
    public static int d(String tag, String msg) {
        return Log.d(tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return Log.d(tag, msg, tr);
    }

    public static int e(String tag, String msg) {
        return Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return Log.e(tag, msg, tr);
    }

    public static int i(String tag, String msg) {
        return Log.i(tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return Log.i(tag, msg, tr);
    }

    public static int v(String tag, String msg) {
        return Log.v(tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return Log.v(tag, msg, tr);
    }

    public static int w(String tag, String msg) {
        return Log.w(tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return Log.w(tag, msg, tr);
    }    
}
