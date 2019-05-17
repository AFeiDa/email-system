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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import pers.afei.utils.Database;
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
        mp.put("NoCOMMAND", "502 Error: auth command not implemented\r\n");

        // 通用邮箱操作指令
        mp.put("OK", "250 OK\r\n");

        // 正常流程
        mp.put("SERVEROK", "220 smtp.afei.com Smtp AFei Mail Server\r\n");
        mp.put("HELO", "250 stmp.afei.com\r\n");
        mp.put("USER", "334 " + Base64.encodeBase64String("User:".getBytes()) + "\r\n");
        mp.put("PASSWORD", "334 " + Base64.encodeBase64String("Password:".getBytes()) + "\r\n");
        mp.put("AUTHSucc", "235 Authentication successful\r\n");
        mp.put("DATAStart", "354 End data with <CR><LF>.<CR><LF>\r\n");
        mp.put("DATAEnd", "250 Ok: queued as\r\n");
        mp.put("QUIT", "221 Bye\r\n");

        // 流程顺序错误
        mp.put("NoHELO", "503 Error: send HELO first\r\n");
        mp.put("NoSMTP", "503 Error: need HELO and AUTH first !\r\n");
        mp.put("NoMAIL", "503 Error: need MAIL command\r\n");
        mp.put("NoRCPT", "503 Error: need RCPT command\r\n");

        // 语法错误
        mp.put("HELOSyntax", "501 Syntax: HELO hostname\r\n");
        mp.put("AUTHSyntax", "502 Error: auth command not implemented\r\n");
        mp.put("MAILSyntax", "501 Syntax: MAIL FROM: <address>\r\n");
        mp.put("RCPTSyntax", "501 Syntax: RCPT TO: <address>\r\n");

        // 其它问题
        mp.put("AUTHFail", "535 Error: authentication failed\r\n");
        mp.put("MAILFromError", "501 mail from address must be same as authorization user\r\n");
        mp.put("MAILFormatError", "501 Error: malformed authentication response\r\n");
        mp.put("MAILAddrError", "500 Error: Incorrect email address\r\n");
        mp.put("MAILBoxNotFound", "550 Mailbox not found or access denied\r\n");
        mp.put("MAILStorageOverrun", "452 Error: Insufficient storage\r\n");

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

        while ((str = bufferIn.readLine()) != null) {

            // 删除首尾空白
            str = str.trim();

            // 单个字符或者空字符串直接跳过
            if (str.length() <= 1) {
                continue;
            }

            // 直接将输入转成小写字符
            String lowerCase = str.toLowerCase();

            // 分割输入命令
            String[] arrayStrings = lowerCase.split(" ");

            if (code.get(arrayStrings[0]) == null || code.get(arrayStrings[0]) != true) {
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
                                user = new String(Base64.decodeBase64(str));

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
                                            String pass = new String(Base64.decodeBase64(str));
                                            String sql = "SELECT user_mailstorage FROM USER WHERE "
                                                    + "USER_NAME = ? AND USER_PASSWORD = ?;";

                                            ResultSet rs = Server.db.executeQuery(sql, user, pass);

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

                } else if (arrayStrings[0].equals("rcpt")) {
                    out.write(mp.get("NoMAIL").getBytes());

                } else {
                    out.write(mp.get("NoSMTP").getBytes());
                }
            } else if (arrayStrings[0].equals("mail")) {

                char[] codeString = lowerCase.substring(arrayStrings[0].length()).trim().toCharArray();

                boolean flag = codeString.length > 4;

                flag = flag && codeString[0] == 'f' && codeString[1] == 'r' && codeString[2] == 'o'
                        && codeString[3] == 'm';

                flag = flag && (lowerCase.indexOf("<") * lowerCase.indexOf(">") > 0);

                int i = 4;
                while (i < codeString.length) {
                    if (codeString[i] == ' ') {
                        ++i;
                    } else {
                        break;
                    }
                }
                flag = flag && codeString[i] == ':';

                if (!flag) {
                    out.write(mp.get("MAILSyntax").getBytes());
                } else {
                    String sender = lowerCase.indexOf("<") == -1 ? lowerCase.substring(lowerCase.indexOf(":") + 1)
                            : lowerCase.substring(lowerCase.indexOf("<") + 1, lowerCase.indexOf(">"));

                    if (!sender.trim().equals(user)) {
                        out.write(mp.get("MAILFromError").getBytes());
                    } else {
                        out.write(mp.get("OK").getBytes());
                    }
                }

            } else if (arrayStrings[0].equals("rcpt")) {

                char[] codeString = lowerCase.substring(arrayStrings[0].length()).trim().toCharArray();

                boolean flag = codeString.length > 2;

                flag = flag && codeString[0] == 't' && codeString[1] == 'o';
                flag = flag && (lowerCase.indexOf("<") * lowerCase.indexOf(">") > 0);

                int i = 2;
                while (i < codeString.length) {
                    if (codeString[i] == ' ') {
                        ++i;
                    } else {
                        break;
                    }
                }
                flag = flag && codeString[i] == ':';

                if (!flag) {
                    out.write(mp.get("RCPTSyntax").getBytes());
                } else {
                    String rcpt = lowerCase.indexOf("<") == -1 ? lowerCase.substring(lowerCase.indexOf(":") + 1)
                            : lowerCase.substring(lowerCase.indexOf("<") + 1, lowerCase.indexOf(">"));

                    rcpts.add(rcpt);
                    out.write(mp.get("OK").getBytes());
                }

            } else if (arrayStrings[0].equals("data")) {
                // 收到data命令时
                if (rcpts.size() == 0) {
                    out.write(mp.get("NoRCPT").getBytes());

                } else {
                    out.write(mp.get("DATAStart").getBytes());

                    StringBuilder mail = new StringBuilder();

                    boolean dataStartB = false;
                    boolean subjectB = false, fromB = false, toB = false, dateB = false;
                    String subjectString = null; 

                    while (!(str = bufferIn.readLine()).equals(".")) {

                        mail.append(str + "\r\n");
                        if (!dataStartB) {
                            
                            int t = str.indexOf(":");

                            if (t == -1) {
                                dataStartB = true;
                            } else {
                                String head = str.substring(0, t).toLowerCase().trim();

                                if (head.equals("subject") && !subjectB) {
                                    subjectB = true;
                                    subjectString = str.substring(t + 1).trim();
                                } else if (head.equals("from") && !fromB) {
                                    fromB = true;
                                } else if (head.equals("to") && !toB) {
                                    toB = true;
                                } else if (head.equals("date") && !dateB) {
                                    dateB = true;
                                } else {
                                    dataStartB = true;
                                }
                            }
                        }
                    }

                    String sql = "INSERT INTO MAIL(MAIL_FROM, MAIL_TO, MAIL_SUBJECT, MAIL_CONTENT, MAIL_DATE) VALUES (?, ?, ?, ?, ?);";
                    String con = new String(mail);
                    int length = con.getBytes().length;

                    if(length <= sizeFrom) {
                        
                        Server.db.execute("UPDATE USER SET USER_MAILSTORAGE=? WHERE USER_NAME=?", String.valueOf(sizeFrom-length), user);
                        for (String rcpt : rcpts) {

                            ResultSet rs = Server.db.executeQuery("SELECT user_mailstorage FROM USER WHERE USER_NAME = ?", rcpt);
                            int sizeTo = 0;
                            try {
                                rs.next();
                                out.write(mp.get("AUTHSucc").getBytes());
                                sizeTo = Integer.valueOf(rs.getString(1));

                            } catch (SQLException e) {
                                
                            }

                            /* 发邮件主体~~ */
                            if(sizeTo >= length) {
                                Server.db.execute(sql, user, rcpt, subjectString, new String(mail), Database.utilDateToSqlDate(new Date()));
                                Server.db.execute("UPDATE USER SET USER_MAILSTORAGE=? WHERE USER_NAME=?", String.valueOf(sizeTo-length), rcpt);
                            } else {
                                out.write(mp.get("MAILStorageOverrun").getBytes());
                            }
                
                        }
                    } else {
                        out.write(mp.get("MAILStorageOverrun").getBytes());
                    }

                    rcpts.clear();

                    out.write(mp.get("DATAEnd").getBytes());
                }
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