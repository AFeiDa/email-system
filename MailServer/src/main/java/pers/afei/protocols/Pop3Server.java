package pers.afei.protocols;

import static pers.afei.utils.Database.resultCount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import pers.afei.utils.Database;

public class Pop3Server implements Server {
    private final Database db = new Database();
    private final ServerSocket mServerSocket;
    public Pop3Server(int port) throws IOException {
        mServerSocket = new ServerSocket(port);
    }

    private void handle(Socket socket) throws IOException, SQLException {

        InputStream in = socket.getInputStream();
        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(in));

        OutputStream out = socket.getOutputStream();
        out.write("+OK Welcome to AFei Mail\n".getBytes());

      
    
    }

    public void run() throws SQLException {
        try {
            Socket socket = mServerSocket.accept();
            handle(socket);
        } catch (IOException e) {
            System.err.println("IOException: SmtpServer not run");
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        
        Pop3Server server = new Pop3Server(110);
        server.run();
    }
}