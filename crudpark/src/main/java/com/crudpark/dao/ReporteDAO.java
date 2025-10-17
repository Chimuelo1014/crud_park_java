package com.crudpark.dao;

import com.crudpark.config.DatabaseConnection;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.math.RoundingMode;
public class ReporteDAO {

    // Suma todos los montos de la tabla pagos en la fecha de hoy.
    private static final String GET_GANANCIAS_DIARIAS_SQL =
            "SELECT SUM(monto) FROM pagos WHERE DATE(fecha_pago) = ?";

    /**
     * Calcula la suma total de pagos (ganancias) para un día específico.
     * @param fecha El día a consultar.
     * @return BigDecimal con la suma total, o cero si no hay pagos.
     */
    public BigDecimal getGananciasDiarias(LocalDate fecha) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        BigDecimal ganancias = BigDecimal.ZERO;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(GET_GANANCIAS_DIARIAS_SQL);
            stmt.setDate(1, Date.valueOf(fecha)); // Convierte LocalDate a SQL Date
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Obtiene la suma, que puede ser null si no hay registros.
                BigDecimal result = rs.getBigDecimal(1);
                if (result != null) {
                    ganancias = result.setScale(2, java.math.RoundingMode.HALF_UP);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DB en ReporteDAO.getGananciasDiarias: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return ganancias;
    }
}