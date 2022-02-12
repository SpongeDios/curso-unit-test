package org.hector.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hector.test.springboot.app.models.Cuenta;
import org.hector.test.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private Integer port;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {
        //arrange
        TransaccionDto dto = new TransaccionDto();
        dto.setMonto(new BigDecimal("100"));
        dto.setCuentaDestinoId(2L);
        dto.setCuentaOrigenId(1L);
        dto.setBancoId(1L);

        //act
        ResponseEntity<String> response = restTemplate.postForEntity("/api/cuentas/transferir", dto, String.class);

        //asserts
        String json = response.getBody();
        assertNotNull(json);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(json.contains("Transferencia realizada con exito!"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        //Otra fomar
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transferencia realizada con exito!", jsonNode.at("/mensaje").asText());
    }

    @Test
    @Order(2)
    void testDetalle() {
        //arrange

        //act
        ResponseEntity<Cuenta> response = restTemplate.getForEntity("/api/cuentas/1", Cuenta.class);

        //asserts
        assertNotNull(response.getBody());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cuenta cuenta = response.getBody();
        assertEquals("Andres", cuenta.getNombre());
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
        assertEquals(1L, cuenta.getId());
    }

    @Test
    @Order(3)
    void testListar() {
        //arrange

        //act
        ResponseEntity<Cuenta[]> response = restTemplate.getForEntity("/api/cuentas", Cuenta[].class);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Cuenta> cuentas = Arrays.asList(response.getBody());
        assertEquals(2, cuentas.size());
    }

    @Test
    @Order(4)
    void testGuardar() {
        //arrange
        Cuenta cuenta = new Cuenta(null, "PepaPig", new BigDecimal("1000"));

        //act
        ResponseEntity<Cuenta> response = restTemplate.postForEntity("/api/cuentas", cuenta, Cuenta.class);

        //asserts
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Cuenta cuentaResponse = response.getBody();

        assertNotNull(cuentaResponse);
        assertEquals("PepaPig", cuentaResponse.getNombre());
        assertEquals(3L, cuentaResponse.getId());
        assertEquals("1000",cuentaResponse.getSaldo().toPlainString());
    }

    @Test
    @Order(5)
    void testEliminar() {
        //arrange
        ResponseEntity<Cuenta[]> response = restTemplate.getForEntity("/api/cuentas", Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());
        assertEquals(3, cuentas.size());

        //act
        restTemplate.delete("/api/cuentas/3");

        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 3L);
        restTemplate.exchange("/api/cuentas/{id}", HttpMethod.DELETE, null, Void.class, pathVariables);

        //asserts
        response= restTemplate.getForEntity("/api/cuentas", Cuenta[].class);
        cuentas = Arrays.asList(response.getBody());
        assertEquals(2, cuentas.size());

    }
}