package pers.afei.utils;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class Database {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private Connection conn;
    private PreparedStatement stmt = null;
    private boolean isLinked = false;

    public Database() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found");
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mailsystem?&serverTimezone=GMT", "root", "");
            isLinked = true;

        } catch (SQLException se) {
            System.err.println("Link SQL Failed");
        }
    }

    public Database(String url, String user, String pwd) {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found");
        }
        try {
            conn = DriverManager.getConnection(url, user, pwd);
            isLinked = true;
        } catch (SQLException se) {
            System.err.println("Link SQL Failed");
        }
    }

    public void openLink(String url, String user, String pwd) {
        try {
            conn = DriverManager.getConnection(url, user, pwd);
            isLinked = true;
        } catch (SQLException se) {
            System.err.println("Open SQL Link Failed");
        }
    }

    public void closeLink() {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException se) {
            System.err.println("Close Statement Failed");
        }
        try {
            if (conn != null)
                conn.close();
            isLinked = false;
        } catch (SQLException se) {
            System.err.println("Close SQL Failed");
        }
    }

    public boolean isOpen() {
        return isLinked;
    }

    public int executeUpdate(String sql, Vector<String> v){
        try {
            stmt = conn.prepareStatement(sql);
            int sz = v.size();
            for(int i = 0; i < sz; ++ i) {
                stmt.setString(i+1, v.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int ret = -1;
        try {
            ret = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean execute(String sql, Vector<String> v) {
        try {
            stmt = conn.prepareStatement(sql);
            int sz = v.size();
            for(int i = 0; i < sz; ++ i) {
                stmt.setString(i+1, v.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean ret = false;
        try {
            ret = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public ResultSet executeQuery(String sql, Vector<String> v) {
        try {
            stmt = conn.prepareStatement(sql);
            int sz = v.size();
            for(int i = 0; i < sz; ++ i) {
                stmt.setString(i+1, v.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet ret = null;
        try {
            ret = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int resultCount(ResultSet rs) {
        int cnt = 0;
        try {
            while (rs.next()) {
                ++cnt;
            }
            rs.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public static String utilDateToSqlDate(java.util.Date d) {
        return new Timestamp(d.getTime()).toString();
    }

    public static java.util.Date sqlDateToUtilDate(String d) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        java.util.Date date = null;
        try {
            date = format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void main(String[] args)  {
        Database db = new Database();
        Vector<String> v = new Vector<String>();
        v.add("1");
        ResultSet rs = db.executeQuery("SELECT mail_date FROM MAIL where 1 = ?;", v);
        
        try {
            System.out.println(resultCount(rs));
            System.out.println(resultCount(rs));

            while (rs.next()) {
                String d = rs.getString(1);
                System.out.println(d);
                java.util.Date date = Database.sqlDateToUtilDate(d);
                System.out.println(date.toString());
                System.out.println(Database.utilDateToSqlDate(date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}