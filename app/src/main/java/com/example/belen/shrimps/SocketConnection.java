package com.example.belen.shrimps;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketConnection {
    private final int PORT = 8888; //Puerto para la conexión
    private final String HOST = "192.168.20.1"; //Host para la conexión
    protected String photoMessage = "fswebcam -p YUYV -d /dev/video0 -r 640x480 /home/pi/ftp/$(date +\"%Y-%m-%d_%H%M%S\").jpg"; //Mensajes entrantes (recibidos) en el servidor
    protected String shutdownMessage = "systemctl poweroff";
    protected Socket clientSocket; //Socket del cliente
    protected DataOutputStream salidaCliente; //Flujo de datos de salida

    public SocketConnection() throws IOException //Constructor
    {
        clientSocket = new Socket(HOST, PORT); //Socket para el cliente en localhost en puerto 1234

    }

    public void takePhoto() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bufferedWriter.flush();
            bufferedWriter.write(photoMessage);
            bufferedWriter.flush();
            readSocket();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws IOException{
        try {
            clientSocket.close();
            System.out.println("END CONNECTION");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void shutdownPi(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bufferedWriter.flush();
            bufferedWriter.write(shutdownMessage);
            bufferedWriter.flush();
            readSocket();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSocket() {
        // read text from the socket
        try
        {
            // read text from the socket
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null)
            {
                sb.append(str + "\n");
            }

            // close the reader, and return the results as a String
            bufferedReader.close();
            System.out.println(sb.toString());
            /*
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer response = new StringBuffer();
            int result;
            while ((result = in.read())!= -1) {
                response.append(Character.toChars(result));
                System.out.println(result);
            }*/
            this.closeConnection();
            System.out.println("Done!");  //never gets printed
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
    }
}


