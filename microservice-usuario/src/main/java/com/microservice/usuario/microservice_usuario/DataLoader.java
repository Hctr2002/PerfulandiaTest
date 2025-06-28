package com.microservice.usuario.microservice_usuario;

import org.springframework.boot.CommandLineRunner;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.repository.UsuarioRepository;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner{

    private final UsuarioRepository usuarioRepository;

    public DataLoader(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {

        Faker faker = new Faker(new Locale("es"));
        Random random = new Random();
        Set<String> usadosRut = new HashSet<>();
        Set<String> usadosCorreo = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            String rut;
            String correo;

            // Generar rut único dentro del batch
            do {
                int cuerpo = 10000000 + random.nextInt(9000000); // 7-8 dígitos
                String dv = generarDV(cuerpo);
                rut = cuerpo + "-" + dv;
            } while (!usadosRut.add(rut)); // solo se agrega si no existía

            // Generar correo único dentro del batch
            do {
                correo = faker.internet().emailAddress();
            } while (!usadosCorreo.add(correo));

            Usuario usuario = Usuario.builder()
                    .rut(rut)
                    .nombres(faker.name().firstName())
                    .apellidos(faker.name().lastName())
                    .correo(correo)
                    .build();

            usuarioRepository.save(usuario);
        }

        System.out.println("✅ Carga de datos de prueba completada: 20 usuarios insertados.");
    }

    // Método para calcular dígito verificador chileno
    private String generarDV(int rut) {
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return (s != 0) ? String.valueOf(s - 1) : "K";
    }

}
