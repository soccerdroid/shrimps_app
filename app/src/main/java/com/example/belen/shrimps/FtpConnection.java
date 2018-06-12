package com.example.belen.shrimps;

import android.app.Application;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FtpConnection extends Application {
    private String username,password,server;
    private int port;
    private FTPClient ftp;

    public void connectToFTP(){
        port = 21;
        server = "10.10.1.118";
        //server = "192.168.0.15";
        //server = "192.168.0.108";
        username = "usuario";
        password = "0000";
        this.ftp = new FTPClient();
        try
        {
            int reply;
            this.ftp.connect(server,port);
            // After connection attempt, you should check the reply code to verify
            // success.
            Log.d("SUCCESS","Connected to " + server + ".");
            Log.d("FTP_REPLY",this.ftp.getReplyString());
            reply = this.ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply))
            {
                this.ftp.disconnect();
                Log.d("REPLY_ERROR","FTP server refused connection.");
            }
            boolean status = this.ftp.login(username, password);
            /*
             * Set File Transfer Mode
             * To avoid corruption issue you must specified a correct
             * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
             * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
             * transferring text, image, and compressed files.
             */
            this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftp.enterLocalPassiveMode();
            //this.ftp.changeWorkingDirectory(working_directory);
        }
        catch (IOException e)
        {
            Log.d("ERROR","Could not connect to host");
            e.printStackTrace();
        }

    }

}
