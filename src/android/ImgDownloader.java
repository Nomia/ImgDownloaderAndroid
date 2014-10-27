/**
 * Phonegap ClipboardManager plugin
 * Omer Saatcioglu 2011
 * enhanced by Guillaume Charhon - Smart Mobile Software
 * ported to Phonegap 2.0 by Jacob Robbins
 * ported to Phonegap 3.0 by Guillaume Charhon - Smart Mobile Software 
 */

package do.okay.phonegap.ImgDownloader;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

import java.util.Calendar;
import android.content.Intent;
import java.io.File;
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


public class ImgDownloader extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) 
            throws JSONException {
        if("download".equals(action)) {
            this.download(args.getString(0), callbackContext);
            callbackContext.success();
            return true;
        }
        return false;
    }
     
    private void download(String filePath, CallbackContext callbackContext) {
        String errorText = "";
        String[] filePathParts = filePath.split("://");
        filePath = filePathParts[1];
        File ext =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            FileChannel inChannel = new FileInputStream(filePath).getChannel();
            File outFile = new File(ext, filenameFromCurrentDate()+".jpg");
            FileChannel outChannel = new FileOutputStream(outFile).getChannel();
            try {
                inChannel.transferTo(0,inChannel.size(),outChannel);
            }
            finally {
                if(inChannel!=null) inChannel.close();
                if(outChannel!=null) outChannel.close();
            }
            scanPhoto(outFile.getPath());
            callbackContext.success(outFile.getPath());
        }
        catch(IOException e) {
            callbackContext.error("error: "+errorText);
            e.printStackTrace();
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


