package com.microservice.venta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.microservice.venta.model.Venta;
import com.microservice.venta.repository.VentaRepository;

import net.datafaker.Faker;

@Component
public class DataLoader implements CommandLineRunner{

    private final VentaRepository ventaRepository;
    private final DataSource dataSource;

    public DataLoader(VentaRepository ventaRepository, DataSource dataSource) {
        this.ventaRepository = ventaRepository;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {

        // Obtener IDs de usuarios
        List<Integer> idUsuarios = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id_usuario FROM usuario")) {

            while (rs.next()) {
                idUsuarios.add(rs.getInt("id_usuario"));
            }
        }

        Faker faker = new Faker(new Locale("es"));
        Random random = new Random();
        Set<Integer> usadosBoleta = new HashSet<>();

        for (int i = 0; i < 30; i++) {
            int nroBoleta;
            do {
                nroBoleta = 100000 + random.nextInt(900000);
            } while (!usadosBoleta.add(nroBoleta));

            int cantidad = random.nextInt(5) + 1;
            int precio = (random.nextInt(5000) + 1000);
            int total = cantidad * precio;

            Venta venta = Venta.builder()
                    .idUsuario(idUsuarios.get(random.nextInt(idUsuarios.size())))
                    .nroBoleta(nroBoleta)
                    .fechaCompra(LocalDate.now().minusDays(random.nextInt(30)).toString())
                    .producto(faker.commerce().productName())
                    .cantidad(cantidad)
                    .total(total)
                    .build();

            ventaRepository.save(venta);
        }

        System.out.println("✅ Se generaron 30 ventas aleatorias asociadas a usuarios.");
    }

}
