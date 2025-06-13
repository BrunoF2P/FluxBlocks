package com.uneb.tetris.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class UserDAO {

    // Inserir usuário
    public static boolean userInclude(String name, String email) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);

            stmt.executeUpdate();
            System.out.println("Usuário incluído com sucesso.");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao incluir usuário: " + e.getMessage());
            return false;
        }
    }

    // Consultar usuário
    public static User userConsult(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new User(rs.getString("name"), rs.getString("email"));
                }

            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar usuário: " + e.getMessage());
        }

        return null;
    }

    // Excluir usuário
    public static void userDelete(String name) {
        String sql = "DELETE FROM users WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Usuário removido com sucesso.");

        } catch (SQLException e) {
            System.out.println("Erro ao excluir usuário: " + e.getMessage());
        }
    }

    // Atualizar usuário
    public static boolean userUpdate(String oldName, String newName, String email) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, newName);
                stmt.setString(2, email);
                stmt.setString(3, oldName);

                stmt.executeUpdate();
                System.out.println("Usuário atualizado com sucesso.");
                return true;

            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }
}
