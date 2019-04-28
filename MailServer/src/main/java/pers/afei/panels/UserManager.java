package pers.afei.panels;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class UserManager {

    public static JPanel showBorder() {
        
        JPanel jp = new JPanel();

        jp.setBorder(new TitledBorder("用户列表"));
        
        Object[][] objects = new Object[100][3];    // 这TM是存数据的啊

        String[] name = {"编号", "用户名", "身份"};
        JTable jt = new JTable(objects, name);
        jp.add(new JScrollPane(jt));
        
        JPanel jp1 = new JPanel();
        jp1.setBorder(new TitledBorder("操作"));
        
        jp1.add(new JLabel("用户名"));
        jp1.add(new JTextField(30));
        
        jp1.add(new JLabel("密码"));
        jp1.add(new JPasswordField(30));

        jp1.add(new JLabel("状态"));
        String[] selects = {"--请选择--", "普通用户", "管理员"};
        jp1.add(new JComboBox<String>(selects));

        jp.add(jp1);

        return jp;
        
    }

}