package com.crudpark.service;

import com.crudpark.dao.ReporteDAO;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteService {

    private ReporteDAO reporteDAO;

    public ReporteService() {
        this.reporteDAO = new ReporteDAO();
    }

    public BigDecimal getGananciasDeHoy() {
        return reporteDAO.getGananciasDiarias(LocalDate.now());
    }
}