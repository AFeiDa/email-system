package com.afei.protocols.pop3;

import static com.afei.utils.Database.resultCount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.afei.utils.Database;

public class Pop3Server {
    private final Database db = new Database();
    private final ServerSocket mServerSocket;
    private String user;

    public Pop3Server(int port) throws IOException {
        mServerSocket = new ServerSocket(port);
    }

    private void handle(Socket socket) throws IOException, SQLException {

        InputStream in = socket.getInputStream();
        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(in));

        OutputStream out = socket.getOutputStream();
        out.write("+OK Welcome to AFei Mail\n".getBytes());

        String str = null, sql = null;
        boolean pass = false;
        String user = null, passwd = null;
        while ((str = bufferIn.readLine()) != null) {
            if (pass) {
                String tmp = str.substring(0, 4);
                if(tmp.equals("retr")) {
                    sql = "SELECT * FROM MAIL WHERE mail_to = '" + user + "';";

                    ResultSet rs = db.executeQuery(sql);
                    rs.next(); 
                    String from = rs.getString(2);
                    String to = rs.getString(3);
                    String sub = rs.getString(4);
                    String con = rs.getString(5);
                    String date = rs.getString(6);
                    out.write("+OK\n".getBytes());
                    out.write(("From:"+from+"\n").getBytes());
                    out.write(("To:"+to+"\n").getBytes());
                    out.write(("subject:"+sub+"\n").getBytes());
                    out.write(("\n" + con + "\n").getBytes());
                    out.write(("Date:" + date + "\n").getBytes());
                } else {
                    out.write("-ERROR\n".getBytes());
                }

            } else if (str.length() > 5) {

                String tmp = str.substring(0, 4), tmp2 = str.substring(5, str.length());
                if (tmp.equals("user")) {
                    user = tmp2 + "@afei.com";
                    sql = "SELECT * FROM USER WHERE user_name = '" + user + "';";
                    if (resultCount(db.executeQuery(sql)) > 0) {
                        out.write("+OK\n".getBytes());
                        
                        str = bufferIn.readLine();
                        if (str.length() > 5) {
                            tmp = str.substring(0, 4); 
                            tmp2 = str.substring(5, str.length());
                            if(tmp.equals("pass")) {
                                passwd = tmp2;
                                sql = "SELECT * FROM USER WHERE user_name = '" + user + "' AND user_password = '" + passwd + "';";
                                if (resultCount(db.executeQuery(sql)) > 0) {
                                    pass = true;
                                    out.write("+OK\n".getBytes());
                                } else {
                                    out.write("-ERR\n".getBytes());
                                }
                            } else {
                                out.write("-ERR\n".getBytes());
                            }
                        } else {
                            out.write("-ERR\n".getBytes());
                        }

                    } else {
                        out.write("-ERR\n".getBytes());
                    }
                    
                } else {
                    out.write("-ERR\n".getBytes());
                }
            } else {
                out.write("-ERR\n".getBytes());
            }
        }
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