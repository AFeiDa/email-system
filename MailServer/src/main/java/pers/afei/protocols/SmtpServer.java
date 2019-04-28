package pers.afei.protocols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import pers.afei.utils.Utils;

import org.apache.commons.codec.binary.Base64;

public class SmtpServer implements Server {

    private final ServerSocket mServerSocket;

    private static final Map<String, String> mp;
    private static final Map<String, Boolean> code;
    static {
        /*
         * 服务器响应
         */
        mp = new HashMap<String, String>();

        // 命令输入错误
        // mp.put("NoCOMMAND", "502 Error: auth command not implemented");

        // 通用邮箱操作指令
        // mp.put("OK", "250 OK");

        // 正常流程
        // mp.put("SERVEROK", "220 smtp.afei.com Smtp AFei Mail Server");
        // mp.put("HELO", "250 stmp.afei.com");
        // mp.put("USER", "334 " + Base64.encodeBase64String("User:".getBytes()));
        // mp.put("PASSWORD", "334 " +
        // Base64.encodeBase64String("Password:".getBytes()));
        // mp.put("AUTHSucc", "235 Authentication successful");
        // mp.put("DATAStart", "354 End data with <CR><LF>.<CR><LF>");
        // mp.put("DATAEnd", "250 Ok: queued as");
        // mp.put("QUIT", "221 Bye");

        // 流程顺序错误
        // mp.put("NoHELO", "503 Error: send HELO first");
        // mp.put("NoSMTP", "503 Error: need HELO and AUTH first !");
        // mp.put("NoMAIL", "503 Error: need MAIL command");
        // mp.put("NoRCPT", "503 Error: need RCPT command");

        // 语法错误
        // mp.put("HELOSyntax", "501 Syntax: HELO hostname");
        // mp.put("AUTHSyntax", "502 Error: auth command not implemented");
        mp.put("MAILSyntax", "501 Syntax: MAIL FROM: <address>");
        mp.put("RCPTSyntax", "501 Syntax: RCPT TO: <address>");

        // 其它问题
        // mp.put("AUTHFail", "535 Error: authentication failed");
        mp.put("MAILFromError", "501 mail from address must be same as authorization user");
        // mp.put("MAILFormatError", "501 Error: malformed authentication response");
        // mp.put("MAILAddrError", "500 Error: Incorrect email address");
        mp.put("MAILBoxNotFound", "550 Mailbox not found or access denied");
        mp.put("MAILStorageOverrun", "452 Error: Insufficient storage");

        /*
         * SMTP命令
         */
        code = new HashMap<String, Boolean>();

        code.put("noop", true);
        code.put("helo", true);
        code.put("auth", true);
        code.put("mail", true);
        code.put("rcpt", true);
        code.put("data", true);
        code.put("quit", true);
    }

    public SmtpServer() throws IOException {
        mServerSocket = new ServerSocket(25);
    }

    public SmtpServer(int port) throws IOException {
        mServerSocket = new ServerSocket(port);
    }

    private void handle(Socket socket) throws IOException {

        InputStream in = socket.getInputStream();
        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(in));

        OutputStream out = socket.getOutputStream();
        out.write(mp.get("SERVEROK").getBytes());

        String str = null;
        boolean helo = false, auth = false;

        String user = null; // 存发信人
        Vector<String> rcpts = new Vector<String>(); // 存收信人
        int sizeFrom = 0; // 发信人剩余空间
        Vector<Integer> sizeTo = new Vector<Integer>(); // 存收信人剩余空间

        while ((str = bufferIn.readLine()) != null) {
            // 读入一行
            String lowerCase = str.toLowerCase();

            String[] arrayStrings = lowerCase.split(" ");

            if (arrayStrings[0].equals("data")) {
                // 收到data命令时
                if (rcpts.size() == 0) {
                    out.write(mp.get("NoRCPT").getBytes());

                } else {
                    out.write(mp.get("DATAStart").getBytes());
                    for (String rcpt : rcpts) {
                        /* 发邮件主体~~ */
                    }

                    out.write(mp.get("DATAEnd").getBytes());
                }
            } else if (code.get(arrayStrings[0]) != true) {
                // 无此命令
                out.write(mp.get("NoCOMMAND").getBytes());

            } else if (arrayStrings[0].equals("quit")) {
                // 退出命令,关闭连接
                out.write(mp.get("QUIT").getBytes());
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();

            } else if (arrayStrings[0].equals("noop")) {
                // noop命令，无条件OK
                out.write(mp.get("OK").getBytes());

            } else if (!helo) {
                // 没有发送HELO
                if (arrayStrings[0].equals("helo")) {
                    if (arrayStrings.length == 1) {
                        out.write(mp.get("HELOSyntax").getBytes());

                    } else {
                        out.write(mp.get("HELO").getBytes());
                        helo = true;
                    }

                } else if (arrayStrings[0].equals("rcpt")) {
                    out.write(mp.get("NoMAIL").getBytes());

                } else {
                    out.write(mp.get("NoHELO").getBytes());
                }

            } else if (!auth) {
                // 没有登录
                if (arrayStrings[0].equals("auth")) {
                    if (arrayStrings.length == 1 || !arrayStrings[1].equals("login")) {
                        out.write(mp.get("AUTHSyntax").getBytes());

                    } else {
                        out.write(mp.get("USER").getBytes());

                        // 输入邮箱
                        str = bufferIn.readLine();

                        if (str != null) {
                            if (!Utils.isBase64(str)) {
                                out.write(mp.get("MAILFormatError").getBytes());

                            } else {
                                user = Base64.decodeBase64(str).toString();

                                /** 判邮箱是否合法 **/
                                String[] tmp = user.split("@");
                                if (tmp.length != 2 || !tmp[1].equals("afei.com")) {
                                    out.write(mp.get("MAILAddrError").getBytes());

                                } else {
                                    out.write(mp.get("PASSWORD").getBytes());

                                    // 输入密码
                                    str = bufferIn.readLine();

                                    if (str != null) {
                                        if (!Utils.isBase64(str)) {
                                            out.write(mp.get("MAILFormatError").getBytes());

                                        } else {
                                            String pass = Base64.decodeBase64(str).toString();
                                            String sql = "SELECT user_mailstorage FROM USER WHERE "
                                                    + "USER_NAME = ?, USER_PASSWORD = ?;";
                                            Vector<String> v = new Vector<String>();
                                            v.add(user);
                                            v.add(pass);

                                            ResultSet rs = Server.db.executeQuery(sql, v);
                                                
                                            try {
                                                rs.next();
                                                auth = true;
                                                out.write(mp.get("AUTHSucc").getBytes());
                                                sizeFrom = Integer.valueOf(rs.getString(1));

                                            } catch (SQLException e) {
                                                out.write(mp.get("AUTHFail").getBytes());
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                } else if(arrayStrings[0].equals("rcpt")) {
                    out.write(mp.get("NoMAIL").getBytes());
                
                } else {
                    out.write(mp.get("NoSMTP").getBytes());
                }
            } else {
                
            }
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