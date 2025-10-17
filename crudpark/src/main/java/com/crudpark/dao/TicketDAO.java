package com.crudpark.dao;

import com.crudpark.config.DatabaseConnection;
import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import java.sql.*;
import java.time.LocalDateTime;

public class TicketDAO {

    private static final String FIND_OPEN_BY_PLACA =
            "SELECT id, placa, fecha_ingreso, operador_ingreso_id, tipo_vehiculo FROM tickets WHERE placa = ? AND fecha_salida IS NULL";

    private static final String INSERT_TICKET =
            "INSERT INTO tickets (placa, fecha_ingreso, operador_ingreso_id, tipo_vehiculo) VALUES (?, ?, ?, ?) RETURNING id";

    /**
     * Busca un ticket abierto (sin fecha_salida) para una placa específica.
     * @param placa La placa a buscar.
     * @return Ticket si encuentra uno abierto, o null.
     */
    public Ticket findOpenTicketByPlaca(String placa) {
        // Nota: Por simplicidad, este método devuelve un Ticket con un Operador placeholder.
        // En un sistema real, necesitarías un join o llamar a OperadorDAO para obtener el objeto completo.
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Ticket ticket = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(FIND_OPEN_BY_PLACA);
            stmt.setString(1, placa);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Usamos un Operador dummy para evitar dependencia circular.
                // Mejor aún: crear un método en OperadorDAO para obtener el nombre solo por ID.
                Operador operadorPlaceholder = new Operador(rs.getInt("operador_ingreso_id"), "usuario_dummy", "Nombre", true);

                ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getTimestamp("fecha_ingreso").toLocalDateTime(), // Conversión de SQL Timestamp a Java LocalDateTime
                        null, // fecha_salida es null
                        operadorPlaceholder,
                        null,
                        rs.getString("tipo_vehiculo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error DB en TicketDAO.findOpenTicketByPlaca: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return ticket;
    }

    /**
     * Crea un nuevo ticket de ingreso en la base de datos.
     * @param ticket El objeto Ticket con placa, fecha y operador.
     * @return El ID (folio) generado del nuevo ticket, o -1 si falla.
     */
    public int create(Ticket ticket) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int ticketId = -1;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_TICKET, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, ticket.getPlaca());
            stmt.setTimestamp(2, Timestamp.valueOf(ticket.getFechaIngreso()));
            stmt.setInt(3, ticket.getOperadorIngreso().getId());
            stmt.setString(4, ticket.getTipoVehiculo());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    ticketId = rs.getInt(1); // Obtiene el ID generado
                }
            }
        } catch (SQLException e) {
            System.err.println("Error DB en TicketDAO.create: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return ticketId;
    }

    private static final String UPDATE_TICKET_SALIDA =
            "UPDATE tickets SET fecha_salida = ?, operador_salida_id = ? WHERE id = ?";

    /**
     * Actualiza un ticket para registrar la hora de salida y el operador.
     * @param ticketId El ID del ticket a cerrar.
     * @param operadorSalidaId El ID del operador que registra la salida.
     * @param fechaSalida La hora actual de salida.
     * @return True si se actualizó el registro.
     */
    public boolean closeTicket(int ticketId, int operadorSalidaId, LocalDateTime fechaSalida) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(UPDATE_TICKET_SALIDA);

            stmt.setTimestamp(1, Timestamp.valueOf(fechaSalida));
            stmt.setInt(2, operadorSalidaId);
            stmt.setInt(3, ticketId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error DB en TicketDAO.closeTicket: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeConnection(null, stmt, conn);
        }
    }
}