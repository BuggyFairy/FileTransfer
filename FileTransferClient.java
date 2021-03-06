package de.sw;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileTransferClient {

    private Socket s;
    private DataOutputStream dos;
    private DataInputStream dis;
    private static final String ACKNOWLEDGE_FILE_NAME = "Acknowledge_File_Name";

    public FileTransferClient(String host, int port, String file) {
        try {
            s = new Socket(host, port);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            sendFileName(file);
            sendFile(file);
            dos.close();
            dis.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        System.out.println("Begin to send file.");

        while (fis.read(buffer) > 0) {
            dos.write(buffer);
        }

        fis.close();
        System.out.println("Successfully send file.");
    }

    public void sendFileName(String file) throws IOException{
        byte[] fileName = (file+"\n").getBytes();
        dos.write(fileName );
        System.out.println("Send file name: " + file);

        byte[] bufferIn = new byte[4096];
        boolean acknowledged = false;
        int read = 0;
        while( !acknowledged ) {
            while( (read = dis.read(bufferIn)) > 0) {
                String msg = new String(bufferIn, StandardCharsets.UTF_8);
                if( msg.contains(ACKNOWLEDGE_FILE_NAME) ) {
                    System.out.println(ACKNOWLEDGE_FILE_NAME);
                    acknowledged = true;
                    break;
                }
            }
        }
        System.out.println("Successfully send file name.");
    }

    public static void main(String[] args) {
//        FileTransferClient fc = new FileTransferClient(args[0], 1988, args[1]);
        FileTransferClient fc = new FileTransferClient(args[0], 8531, args[1]);
    }

}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            