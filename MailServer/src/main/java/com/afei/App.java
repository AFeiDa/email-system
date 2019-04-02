/************************************************************
 *                    _ooOoo_
 *                   o8888888o
 *                   88" . "88
 *                   (| -_- |)
 *                   O\  =  /O
 *               ____/`---'\____
 *             .'  \\|     |//  `.
 *            /  \\|||  :  |||//  \
 *           /  _||||| -:- |||||-  \
 *           |   | \\\  -   * |   |
 *           | \_|  ''\---/''  |   |
 *           \  .-\__  `-`  ___/-. /
 *         ___`. .'  /--.--\  `. . __
 *      ."" '<  `.___\_<|>_/___.'  >'"".
 *     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *     \  \ `-.   \_ __\ /__ _/   .-` /  /
 *======`-.____`-.___\_____/___.-`____.-'======
 *                   `=---='
 *^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *
 *         佛祖保佑       永无BUG
 *
 *  本程序已经经过开光处理，绝无可能再产生bug
 **************************************************************/

package com.afei;

import java.io.IOException;


import com.afei.protocols.smtp.SmtpServer;

import org.apache.commons.codec.binary.Base64;

public class App {
    public static void main(String[] args) throws IOException {
        // Utils.run(new TabbedPanel(), 800, 600, "肥大邮件服务器");
        /*
 *c3VwZXJfYWZlaUBxcS5jb20=
 *eml1Y3ljcnhpbHFsZmpmYw==
 */
        Base64 base64 = new Base64();
        String a = "super_afei@qq.com", b = "ziucycrxilqlfjfc";
        System.out.println(base64.encodeToString(a.getBytes("UTF-8")));
        System.out.println(base64.encodeToString(b.getBytes("UTF-8")));
    }
}
