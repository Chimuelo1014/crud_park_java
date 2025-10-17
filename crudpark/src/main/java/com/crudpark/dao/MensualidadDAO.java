package com.crudpark.dao;

import com.crudpark.config.DatabaseConnection;
import com.crudpark.model.Mensualidad;
import java.sql.*;
import java.time.LocalDate;

public class MensualidadDAO {

    private static final String FIND_BY_PLACA =
            "SELECT id, placa, nombre_cliente, fecha_inicio, fecha_fin, activa FROM mensualidades WHERE placa = ? ORDER BY fecha_fin DESC LIMIT 1";

    public Mensualidad findByPlaca(String placa) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Mensualidad mensualidad = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(FIND_BY_PLACA);
            stmt.setString(1, placa);
            rs = stmt.executeQuery();

            if (rs.next()) {
                mensualidad = new Mensualidad(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("nombre_cliente"),
                        rs.getDate("fecha_inicio").toLocalDate(), // Conversión de SQL Date a Java LocalDate
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getBoolean("activa")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error DB en MensualidadDAO.findByPlaca: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return mensualidad;
    }
}