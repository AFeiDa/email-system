package pers.afei.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * 这个类基本没什么用，仅用于向Server发送信息，并且读取Server响应(测试Server可用性的)
 */
public class Client {

    private final Socket socket;

    public Client(String host, int port) throws IOException, UnknownHostException {
        socket = new Socket(host, port);
    }

    public void run() throws IOException {
        Thread readerThread = new Thread(new Runnable() {
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
        Client pop3 = new Client("localhost", 110);
        pop3.run();
    }
    
}
