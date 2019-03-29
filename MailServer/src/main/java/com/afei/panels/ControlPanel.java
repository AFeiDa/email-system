package com.afei.panels;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import com.afei.Utils;

// 构建控制面板界面
public class ControlPanel extends JComponent {

    private static final long serialVersionUID = 1L;

    public static JPanel showBorder() {
        JPanel jp = new JPanel();
        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        JPanel jp3 = new JPanel();

        jp.setLayout(new GridLayout(3, 1));

        jp1.setBorder(new TitledBorder("IP地址"));
        jp1.setLayout(new GridLayout(2, 1));
        jp1.add(new JLabel("当前主机名" + Utils.getHostName(), JLabel.CENTER));
        jp1.add(new JLabel("当前IP" + Utils.getIP(), JLabel.CENTER));

        jp2.setBorder(new TitledBorder("SMTP协议"));
        
        jp3.setBorder(new TitledBorder("POP3协议"));

        jp.add(jp1);
        jp.add(jp2);
        jp.add(jp3);

        return jp;
    }
}