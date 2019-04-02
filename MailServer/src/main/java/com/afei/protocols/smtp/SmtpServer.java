package com.afei.protocols.smtp;

import static com.afei.utils.Database.resultCount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

import com.afei.beans.MailBean;
import com.afei.utils.Database;

public class SmtpServer {

    private final Database db = new Database();
    private final ServerSocket mServerSocket;
    private String user;

    public SmtpServer(int port) throws IOException {
        mServerSocket = new ServerSocket(port);
    }

    private void handle(Socket socket) throws IOException {

        InputStream in = socket.getInputStream();
        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(in));

        OutputStream out = socket.getOutputStream();

        String str = null;
        boolean step = false;
        while ((str = bufferIn.readLine()) != null) {

            String lowerCase = str.toLowerCase();

            if (lowerCase.equals("helo smtp")) {
                str = "250 smtp.afei.com\n";
                step = true;

            } else if(lowerCase.equals("auth login")) {

                if(!step) {
                    str = "503 Error: send HELO/EHLO first\n";
                } else {
                    str = "334 VXNlcm5hbWU6\n";
                    out.write(str.getBytes());

                    str = bufferIn.readLine();
                    if (resultCount(db.executeQuery("SELECT * FROM USER WHERE user_name = '" + str + "';")) > 0) {
                        user = str;

                        str = "334 UGFzc3dvcmQ6\n";
                        out.write(str.getBytes());
                        
                        str = bufferIn.readLine();

                        if(resultCount(db.executeQuery("SELECT * FROM USER WHERE user_name = '" + user +"' AND user_password = '" + str + "';")) > 0) {
                            str = "235 Authentication successful\n";
                            out.write(str.getBytes());

                            /*开始写邮件了*/
                            MailBean mail = new MailBean();
                            
                            boolean end = false;

                            while ((str = bufferIn.readLine()) != null && !end) {
                                
                                String tmp = str;
                                tmp.trim();

                                if(mail.getFrom() == null && tmp.length() > 11 && tmp.subSequence(0, 11).equals("mail from:<") 
                                && tmp.subSequence(tmp.length()-1, tmp.length()).equals(">")) {
                                    
                                    String from = tmp.subSequence(11, tmp.length()-1).toString();  

                                    if(!from.equals(user)) {
                                        out.write("501 mail from address must be same as authorization user\n".getBytes());
                                    } else {
                                        mail.setFrom(from);
                                        out.write("250 OK\n".getBytes());
                                    }

                                } else {

                                    if(mail.getFrom() == null) {
                                        out.write("501 Error: malformed authentication response\n".getBytes());
                                    
                                    } else if(mail.getTo() == null && tmp.length() > 9 && tmp.subSequence(0, 9).equals("rcpt to:<") 
                                    && tmp.subSequence(tmp.length()-1, tmp.length()).equals(">")) {
                                        
                                        mail.setTo(tmp.substring(9, tmp.length()-1));
                                        out.write("250 OK\n".getBytes());

                                    } else {
                                        if(mail.getTo() == null) {
                                            out.write("501 Error: malformed authentication response".getBytes());

                                        } else if(mail.getSubject() == null && tmp.length() > 9 && tmp.subSequence(0, 9).equals("subject:<") 
                                        && tmp.subSequence(tmp.length()-1, tmp.length()).equals(">")) { // subject:<>
                                            mail.setSubject(tmp.substring(9, tmp.length()-1));
                                            out.write("250 OK\n".getBytes());

                                        } else {
                                            if(mail.getSubject() == null) {
                                                out.write("501 Error: malformed authentication response".getBytes());
                                            
                                            } else if(tmp.equals("data")) { // 开始写正文了
                                                String con = "";
                                                while ((str = bufferIn.readLine()) != null) {
                                                    if(str.equals(".")) {
                                                        // 发邮件
                                                        mail.setContent(con);
                                                        mail.setDate(new Date());

                                                        String sql = "INSERT INTO mail(mail_from, mail_to, mail_subject, mail_content, mail_date, mail_state) values('" + mail.getFrom() + "', '" + mail.getTo() + "', '" + mail.getSubject() + "', '" + mail.getContent() + " ', '" + new Timestamp(mail.getDate().getTime()).toString() + "', 0);";

                                                        db.execute(sql);
                                                        end = true;
                                                        out.write("250 OK\n".getBytes());
                                                        break;
                                                    }  else {
                                                        con = con + str + "\n";
                                                    } 

                                                }

                                            } else {
                                                out.write("501 Error: malformed authentication response".getBytes());
                                            }
                                        }
                                    }
                                }
                            }
                            /*写完邮件了*/


                        } else {
                            str = "535 Error: password error\n";
                        }
                    } else {
                        str = "501 Error: malformed authentication response\n";
                    }
                }

            } else {
                str = "502 Error: command not implemented\n";
            }

            out.write(str.getBytes());

        }
    }

    public void run() {
        try {
            Socket socket = mServerSocket.accept();
            handle(socket);
        } catch (IOException e) {
            System.err.println("IOException: SmtpServer not run");
        }
    }

    public static void main(String[] args) throws IOException {
        SmtpServer server = new SmtpServer(25);
        server.run();
    }


}