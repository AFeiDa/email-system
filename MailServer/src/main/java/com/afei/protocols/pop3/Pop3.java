package com.afei.protocols.pop3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Pop3 {

    private final Socket socket;

    public Pop3(String host, int port) throws IOException, UnknownHostException{
        socket = new Socket(host, port);
    }

    public void run() throws IOException {
        Thread readerThread = new Thread(new Runnable(){
            public void run() {
                readResponse();
            }
        });
        readerThread.start();

        OutputStream out = socket.getOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = System.in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
    }

    private void readResponse() {
        try {
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) {
                System.out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Pop3 smtp = new Pop3("localhost", 110);
        smtp.run();
    }
    
}
