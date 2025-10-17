package com.crudpark.dao;

import com.crudpark.config.DatabaseConnection;
import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import java.sql.*;
import java.time.LocalDateTime;

public class TicketDAO {

    // --- CONSULTAS SQL CONSOLIDADAS ---

    private static final String FIND_OPEN_BY_PLACA =
            "SELECT id, placa, fecha_ingreso, operador_ingreso_id, tipo_vehiculo FROM tickets WHERE placa = ? AND fecha_salida IS NULL";

    private static final String INSERT_TICKET =
            "INSERT INTO tickets (placa, fecha_ingreso, operador_ingreso_id, tipo_vehiculo) " +
                    "VALUES (?, ?, ?, ?)"; // No necesitamos 'RETURNING id' en la String si usamos Statement.RETURN_GENERATED_KEYS

    private static final String UPDATE_TICKET_SALIDA =
            "UPDATE tickets SET fecha_salida = ?, operador_salida_id = ? WHERE id = ?";

    private static final String FIND_LATEST_OPEN_BY_PLACA =
            "SELECT id, placa, fecha_ingreso, operador_ingreso_id, tipo_vehiculo FROM tickets " +
                    "WHERE placa = ? AND fecha_salida IS NULL ORDER BY fecha_ingreso DESC LIMIT 1";

    // --- MÉTODOS ---

    /**
     * Busca un ticket abierto (sin fecha_salida) para una placa específica.
     * @param placa La placa a buscar.
     * @return Ticket si encuentra uno abierto, o null.
     */
    public Ticket findOpenTicketByPlaca(String placa) {
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
                // Usamos un Operador dummy (placeholder).
                Operador operadorPlaceholder = new Operador(rs.getInt("operador_ingreso_id"), "usuario_dummy", "Nombre", true);

                ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getTimestamp("fecha_ingreso").toLocalDateTime(),
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
     * Crea un nuevo ticket de ingreso en la base de datos y devuelve el ID generado.
     * * NOTA: Este método es la versión correcta y consolidada de los dos métodos 'create' que tenías.
     * * @param ticket El objeto Ticket con placa, fecha y operador.
     * @return El ID (folio) generado del nuevo ticket, o -1 si falla.
     */
    public int create(Ticket ticket) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int ticketId = -1;

        try {
            conn = DatabaseConnection.getConnection();

            // Usamos la constante INSERT_TICKET y pedimos las claves generadas
            stmt = conn.prepareStatement(INSERT_TICKET, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, ticket.getPlaca());
            stmt.setTimestamp(2, Timestamp.valueOf(ticket.getFechaIngreso()));
            stmt.setInt(3, ticket.getOperadorIngreso().getId());
            // Se usa el valor del objeto o un valor por defecto si es nulo (para evitar NullPointerException)
            stmt.setString(4, ticket.getTipoVehiculo() != null ? ticket.getTipoVehiculo() : "AUTO");

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

    /**
     * Busca el ticket ABIERTO más reciente para una placa (utilizado para imprimir).
     * @param placa La placa a buscar.
     * @return Ticket si encuentra uno abierto, o null.
     */
    public Ticket findLatestOpenTicketByPlaca(String placa) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Ticket ticket = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(FIND_LATEST_OPEN_BY_PLACA);
            stmt.setString(1, placa);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Usamos un Operador dummy (placeholder).
                Operador operadorPlaceholder = new Operador(rs.getInt("operador_ingreso_id"), "usuario_dummy", "Nombre", true);

                ticket = new Ticket(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getTimestamp("fecha_ingreso").toLocalDateTime(),
                        null,
                        operadorPlaceholder,
                        null,
                        rs.getString("tipo_vehiculo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error DB en TicketDAO.findLatestOpenTicketByPlaca: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(rs, stmt, conn);
        }
        return ticket;
    }
}