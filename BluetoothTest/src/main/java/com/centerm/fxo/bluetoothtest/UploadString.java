package com.centerm.fxo.bluetoothtest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Chen on 16/5/18.
 */
public class UploadString implements Runnable {
    private String Tag = "UploadString";
    private String urlstring = "http://192.168.12.102:8080/Upload/UploadString";
    String Body;
    HttpURLConnection conn;
    public  UploadString(String input){
        Body = input;
    }

    @Override
    public void run(){
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "****";
        try{
            URL url = new URL(urlstring);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(Body);
            dos.flush();
            dos.close();

        }catch (MalformedURLException ex) {
            Log.e(Tag, "error: " + ex.getMessage(), ex);
        }catch (IOException ioe){
            Log.e(Tag, "error: " + ioe.getMessage(), ioe);
        }

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.e("Dialoge Box", "Message: " + line);
            }
            rd.close();

        } catch (IOException ioex) {
            Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
        }


    }
}