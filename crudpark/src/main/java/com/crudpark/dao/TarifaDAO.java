package com.crudpark.dao;

import com.crudpark.config.DatabaseConnection;
import com.crudpark.model.Tarifa;
import java.sql.*;
import java.math.BigDecimal;

public class TarifaDAO {

    private static final String FIND_ACTIVE_TARIFA =
            "SELECT id, nombre, valor_hora_base, valor_fraccion_minuto, minutos_gracia, activo FROM tarifas WHERE activo = TRUE LIMIT 1";

    /**
     * Obtiene la tarifa activa principal (asumimos solo una tarifa activa por ahora).
     * @return Objeto Tarifa activa, o null si no hay.
     */
    public Tarifa findActiveTarifa() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Tarifa tarifa = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(FIND_ACTIVE_TARIFA);
            rs = stmt.executeQuery();

            if (rs.next()) {
                tarifa = new Tarifa(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("valor_hora_base"),
                        rs.getBigDecimal("valor_fraccion_minuto"),
                        rs.getInt("minutos_gracia"),
                        rs.getBoolean("activo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error DB en TarifaDAO.findActiveTarifa: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return tarifa;
    }
}