package com.okay.phonegap.ImgDownloader;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

import java.util.Calendar;
import android.content.Intent;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import android.os.Environment;
import android.net.Uri;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import java.lang.reflect.Method;
import android.os.storage.StorageManager;


public class ImgDownloader extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) 
            throws JSONException {
        if("download".equals(action)) {
            this.download(args.getString(0), callbackContext);
            return true;
        }
        return false;
    }
     
    private Boolean download(String filePath, CallbackContext callbackContext) {
        String errorText = "";
        String[] filePathParts = filePath.split("://");
        filePath = filePathParts[1];

        String outpath;

        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String filename = filenameFromCurrentDate()+".jpg";

        //if sdcard exist,we save it to sdcard
        if(sdCardExist){
            String sdpath = this.getSecondaryStoragePath();
            if(sdpath != null){
                outpath = sdpath + "/DCIM/Camera";

                File cameraDir = new File(outpath);
                if(cameraDir.exists()){
                    this.copyFile(filePath,filename,outpath,callbackContext);
                    return true;
                }
            }
        }
         
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        if(dir.exists()){
            outpath = dir.getAbsolutePath() + "/Camera";
        }else{
            outpath =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        }


        this.copyFile(filePath,filename,outpath,callbackContext);
        return true;
        // File ext =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // try {
        //     FileChannel inChannel = new FileInputStream(filePath).getChannel();
        //     File outFile = new File(ext, filenameFromCurrentDate()+".jpg");
        //     FileChannel outChannel = new FileOutputStream(outFile).getChannel();
            
        //     try {
        //         inChannel.transferTo(0,inChannel.size(),outChannel);
        //     }
        //     finally {
        //         if(inChannel!=null) inChannel.close();
        //         if(outChannel!=null) outChannel.close();
        //     }
            
        //     scanPhoto(outFile.getPath());
        //     callbackContext.success(outFile.getPath());
        //     return outFile.getPath();
        // }
        // catch(IOException e) {
        //     callbackContext.error("error: "+errorText);
        //     e.printStackTrace();

        //     return "";
        // }
    }

    public String getSecondaryStoragePath() {
        try {
            StorageManager sm = (StorageManager) cordova.getActivity().getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", null);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, null);
            // second element in paths[] is secondary storage path
            return paths[1];
        } catch (Exception e) {

        }
        return null;
    }

    private Boolean copyFile(String inputFilePath, String inputFile, String outputPath,CallbackContext callbackContext) {
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath); 
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            String outputFilePath = outputPath +"/"+ inputFile;
            in = new FileInputStream(inputFilePath);        
            out = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            
            in = null;

            File file = new File(outputFilePath);
            if(file.exists()){
                callbackContext.success(outputFilePath);
            }else{
                callbackContext.error("error");
            }

                // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;        
            return true;
        }catch (FileNotFoundException fnfe1) {
            return false;
        }catch (Exception e) {
            return false;
        }
    }
     
    private String filenameFromCurrentDate() {
        Calendar c = Calendar.getInstance();
        String date = fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.MONTH)) + 
                fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.HOUR_OF_DAY)) + 
                fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));
        return date;
    }
     
    private String fromInt(int val) {
        return String.valueOf(val);
    }
     
    private void scanPhoto(String imageFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageFileName);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.cordova.getActivity().sendBroadcast(mediaScanIntent);
    }
}


