package com.uneb.fluxblocks.DB;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/FluxBlocks";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static java.sql.Connection getConnection() {
        try {
            System.out.println("Conexão estabelecida com sucesso!");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco: " + e.getMessage());
            return null;
        }
    }
}