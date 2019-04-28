package pers.afei.protocols;

import java.sql.SQLException;

import pers.afei.utils.Database;

public interface Server {
    Database db = new Database();
    void run() throws SQLException;
}