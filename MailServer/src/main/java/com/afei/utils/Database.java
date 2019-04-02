package com.afei.utils;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mailserver?&serverTimezone=GMT", "root", "");
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

    public int executeUpdate(String sql) {
        try {
            stmt = conn.prepareStatement(sql);
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

    public boolean execute(String sql) {
        try {
            stmt = conn.prepareStatement(sql);
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

    public ResultSet executeQuery(String sql) {
        try {
            stmt = conn.prepareStatement(sql);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public static void main(String[] args) {
        Database db = new Database();
        ResultSet rs = db.executeQuery("SELECT * FROM USER;");
        System.err.println(resultCount(rs));
    }

}