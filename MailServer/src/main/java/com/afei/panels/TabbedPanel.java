package com.afei.panels;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

// 构建主面板
public class TabbedPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private String[] titleStrings = { "控制面板", "用户管理", "日志管理", "系统管理" };
    private JPanel[] jpanel = { 
        ControlPanel.showBorder(), 
        UserManager.showBorder(), 
        LogManager.showBorder(),
        SystemManager.showBorder() 
    };

    private JTabbedPane tabs = new JTabbedPane();

    public TabbedPanel() {
        for (int i = 0; i < 4; ++i) {
            tabs.addTab(titleStrings[i], jpanel[i]);
        }

        add(tabs);
    }
}