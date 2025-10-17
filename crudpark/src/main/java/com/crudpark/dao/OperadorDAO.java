package com.crudpark.dao;

import com.crudpark.config.DatabaseConnection;
import com.crudpark.model.Operador;
import java.sql.*;

public class OperadorDAO {

    private static final String FIND_BY_USERNAME_SQL =
            "SELECT id, usuario, nombre, activo, password_hash FROM operadores WHERE usuario = ?";

    public Operador findByUsername(String usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Operador operador = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(FIND_BY_USERNAME_SQL);
            stmt.setString(1, usuario);
            rs = stmt.executeQuery();

            if (rs.next()) {
                operador = new Operador(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("nombre"),
                        rs.getBoolean("activo"),
                        rs.getString("password_hash")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error DB al buscar operador: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return operador;
    }
}