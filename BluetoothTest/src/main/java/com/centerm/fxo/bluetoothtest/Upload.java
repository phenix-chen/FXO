package com.centerm.fxo.bluetoothtest;

/**
 * Created by Chen on 16/5/17.
 */

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Upload implements Runnable {
    private String Tag = "UPLOADER";
    private String urlString = "http://192.168.12.102:8080/Upload/UploadServlet";
    HttpURLConnection conn;

    public void run() {
        String lu = Environment.getRootDirectory().toString();
        String exsistingFileName = "storage/sdcard0/database/myDict3.db3";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            // ------------------ CLIENT REQUEST

            Log.e(Tag, "Inside second Method");
            FileInputStream fileInputStream = new FileInputStream(new File(
                    exsistingFileName));
            // open a URL connection to the Servlet

            URL url = new URL(urlString);
            // Open a HTTP connection to the URL

            conn = (HttpURLConnection) url.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            System.out.println("done");
            DataOutputStream dos = null;

            dos = new DataOutputStream(conn.getOutputStream());

            System.out.println("done");
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"myDict.db3\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1000;
            // int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bytesAvailable];

            // read file and write it into form...

            int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

            while (bytesRead > 0) {
                System.out.println(String.valueOf(bytesRead));
                dos.write(buffer, 0, bytesAvailable);
                bytesAvailable = fileInputStream.available();
                bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
            }

            // send multipart form data necesssary after file data...

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e(Tag, "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e(Tag, "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "error2: " + ioe.getMessage(), ioe);
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