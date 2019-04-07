package de.sw;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class FileTransferServer extends Thread {

    private ServerSocket ss;
    private final int BUFFER_SIZE = 256000;

    private static final String ACKNOWLEDGE_FILE_NAME = "Acknowledge_File_Name";

    public FileTransferServer(int port) {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket clientSock = ss.accept();
                saveFile(clientSock);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void acknowledge(Socket clientSocket) throws IOException{
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        byte[] buffer = ACKNOWLEDGE_FILE_NAME.getBytes();
        dos.write(buffer);
    }

    private void saveFile(Socket clientSock) {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            String clientMsg ;
            while( ( clientMsg = br.readLine() ) != null ){
                String[] splittedClientMsg = clientMsg.split("\\\\");
                clientMsg = splittedClientMsg[splittedClientMsg.length-1];
                System.out.println("Received: " + clientMsg + " which contains: " + clientMsg.length() + " characters.");
                break;
            }

            acknowledge(clientSock);

            DataInputStream dis = new DataInputStream(clientSock.getInputStream());
            FileOutputStream fos = new FileOutputStream(clientMsg);
            byte[] buffer = new byte[BUFFER_SIZE];

            long[] totalRead = new long[1];
            long timeInMillis = System.currentTimeMillis();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println( (totalRead[0] / ( (System.currentTimeMillis() - timeInMillis) / 1000.0f) / 1000000.f + " MB/s"));
                }
            }, 0,1000);

            int read = 0;
            while( (read = dis.read(buffer)) > 0) {
                totalRead[0] += read;
                fos.write(buffer, 0, read);
            }

            timer.cancel();
            fos.close();
            dis.close();
            System.out.println("Successfully received file.");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileTransferServer fs = new FileTransferServer(8531);
        fs.start();
    }

}