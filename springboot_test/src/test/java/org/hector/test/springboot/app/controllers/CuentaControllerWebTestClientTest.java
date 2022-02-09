package org.hector.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hector.test.springboot.app.models.Cuenta;
import org.hector.test.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //Orden de ejecucion de los test mediante anotaciones
//un problema de las pruebas de integracion es que, cuando se ejecuta un test afecta a los demas.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    @Autowired
    private WebTestClient webTestClient;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(3)
    void testTransferir() {
        //arrange
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));

        //act
        //webTestClient.post().uri("http://localhost:8080/api/cuentas/transferir") ahora a probar con algo que no sea localhost
        webTestClient.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
        //asserts
                .expectStatus().isOk()
                .expectBody()
                //Otra forma de realizar consultas///////
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("Transferencia realizada con exito!", json.path("mensaje").asText());
                        assertEquals(1, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                ////////
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(Matchers.is("Transferencia realizada con exito!"))
                .jsonPath("$.mensaje").value(valor -> assertEquals("Transferencia realizada con exito!", valor))
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString());

    }

    @Test
    @Order(1)
    void testDetalle() throws JsonProcessingException {

        Cuenta cuenta = new Cuenta(1L, "Andres", new BigDecimal("1000"));
        webTestClient.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Andres")
                .jsonPath("$.saldo").isEqualTo(1000)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(2)
    void testDetalle2() {
        webTestClient.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta cuenta = response.getResponseBody();
                    assertEquals("Hector", cuenta.getNombre());
                    assertEquals(2000, cuenta.getSaldo().intValue());
                });
    }

    @Test
    @Order(4)
    void testListar() {
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Andres")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].nombre").isEqualTo("Hector")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(Matchers.hasSize(2));

    }

    @Test
    @Order(5)
    void testListar2() {
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertEquals(2, cuentas.size());

                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("900.0", cuentas.get(0).getSaldo().toPlainString());
                    assertEquals("Andres", cuentas.get(0).getNombre());

                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("2100.0", cuentas.get(1).getSaldo().toPlainString());
                    assertEquals("Hector", cuentas.get(1).getNombre());
                })
                .hasSize(2)
                .value(Matchers.hasSize(2));
    }

    @Test
    @Order(6)
    void testGuardar() {
        //arrange
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //act
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
        //asserts
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.saldo").isEqualTo(3000)
                .jsonPath("$.nombre").isEqualTo("Pepe");
    }

    @Test
    @Order(7)
    void testGuardar2() {
        //arrange
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("4000"));

        //act
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                //asserts
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta c = response.getResponseBody();
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getNombre());
                    assertEquals("4000", c.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(8)
    void testEliminar() {
        //arrange

        //act
        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .hasSize(4);
        webTestClient.delete().uri("/api/cuentas/3")
                .exchange()
        //asserts
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        webTestClient.get().uri("/api/cuentas/3").exchange()
                .expectStatus().is5xxServerError();
    }
}