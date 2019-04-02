package com.afei.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class LogManager {

    public static JPanel showBorder() {

        JPanel jp = new JPanel();

        Object[][] objects = new Object[100][5];
        String[] name = {"编号", "操作者", "操作事项", "时间", "状态"};
        JTable jt = new JTable(objects, name);
        jp.add(new JScrollPane(jt));

        return jp;
        
    }

}