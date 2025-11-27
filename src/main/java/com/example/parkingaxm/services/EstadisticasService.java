package com.example.parkingaxm.services;

import com.example.parkingaxm.models.Registro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class EstadisticasService {

    private List<Registro> cargarRegistros() {
        try {
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(
                    getClass().getResourceAsStream("/com/example/parkingaxm/data/registros.json")
            );

            return gson.fromJson(reader, new TypeToken<List<Registro>>(){}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public EstadisticasResumen obtenerEstadisticas() {

        List<Registro> registros = cargarRegistros();

        LocalDate hoy = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int semanaActual = hoy.get(wf.weekOfWeekBasedYear());
        int mesActual = hoy.getMonthValue();

        int diaCount = 0, semanaCount = 0, mesCount = 0;

        int carrosDia = 0, motosDia = 0;
        int carrosSemana = 0, motosSemana = 0;
        int carrosMes = 0, motosMes = 0;

        double dineroDia = 0, dineroSemana = 0, dineroMes = 0;

        for (Registro r : registros) {
            LocalDate fecha = r.getEntrada().toLocalDate();

            int semana = fecha.get(wf.weekOfWeekBasedYear());
            int mes = fecha.getMonthValue();

            boolean esCarro = r.getTipoVehiculo().equalsIgnoreCase("CARRO");
            double total = r.getTotal();

            // Día
            if (fecha.equals(hoy)) {
                diaCount++;
                dineroDia += total;
                if (esCarro) carrosDia++; else motosDia++;
            }

            // Semana
            if (semana == semanaActual) {
                semanaCount++;
                dineroSemana += total;
                if (esCarro) carrosSemana++; else motosSemana++;
            }

            // Mes
            if (mes == mesActual) {
                mesCount++;
                dineroMes += total;
                if (esCarro) carrosMes++; else motosMes++;
            }
        }

        return new EstadisticasResumen(
                diaCount, semanaCount, mesCount,
                carrosDia, motosDia,
                carrosSemana, motosSemana,
                carrosMes, motosMes,
                dineroDia, dineroSemana, dineroMes
        );
    }

    public static class EstadisticasResumen {

        public int dia, semana, mes;

        public int carrosDia, motosDia;
        public int carrosSemana, motosSemana;
        public int carrosMes, motosMes;

        public double dineroDia, dineroSemana, dineroMes;

        public EstadisticasResumen(
                int dia, int semana, int mes,
                int carrosDia, int motosDia,
                int carrosSemana, int motosSemana,
                int carrosMes, int motosMes,
                double dineroDia, double dineroSemana, double dineroMes
        ) {
            this.dia = dia;
            this.semana = semana;
            this.mes = mes;

            this.carrosDia = carrosDia;
            this.motosDia = motosDia;

            this.carrosSemana = carrosSemana;
            this.motosSemana = motosSemana;

            this.carrosMes = carrosMes;
            this.motosMes = motosMes;

            this.dineroDia = dineroDia;
            this.dineroSemana = dineroSemana;
            this.dineroMes = dineroMes;
        }
    }
}
