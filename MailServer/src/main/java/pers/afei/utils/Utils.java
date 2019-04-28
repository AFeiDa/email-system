package pers.afei.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author AFei
 */
public class Utils {

    // 创建一个名为title的窗口
    public static void run(final JFrame f, final int width, final int height, final String title) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.setTitle(title);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(width, height);
                f.setVisible(true);
            }
        });
    }

    // 获取主机名
    public static String getHostName() {

		try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException e) {
        // 获取不到主机名
            return "UnknownHostName";
        }
    }

    // 获取当前IP
    public static String getIP() {
        try {
            return (InetAddress.getLocalHost()).getHostAddress();
        } catch(UnknownHostException e) {
        // 获取不到IP
            return "UnknownIP";
        }
    }

    // 判断字符串是否符合base64编码规则
    public static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }
}