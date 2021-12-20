package org.hector.test.springboot.app;

import org.hector.test.springboot.app.models.Cuenta;
import org.hector.test.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class IntegracionJpaTest {

//    @Mock
//    CuentaRepository cuentaRepository;

    @Autowired
    CuentaRepository cuentaRepository;

    @BeforeEach
    void setUp() {

    }

    ////////////ELISEO RECOMIENDA MOCKKKK////////////////////////////


//    @Test
//    void testFindById_SOLO_ESTOY_PROBANDO_EL_MOCK() {
//        when(cuentaRepository.findById(anyLong())).thenReturn(Optional.of(new Cuenta(1L,"Andres", new BigDecimal("1000"))));
//
//        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
//        assertTrue(cuenta.isPresent());
//        assertEquals("Andres", cuenta.orElseThrow().getNombre());
//    }

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getNombre());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombre("Andres");
        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getNombre());
        //hay que ponerle el .00 porq asi lo registra la BBDD
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombre("Rod");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        cuentaRepository.save(cuentaPepe);
        Cuenta cuenta = cuentaRepository.findByNombre("Pepe").orElseThrow();

        assertEquals("Pepe", cuenta.getNombre());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

    }

    @Test
    void testSaveOtraForma() {
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        assertEquals("Pepe", cuenta.getNombre());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testUpdate() {
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        assertEquals("Pepe", cuenta.getNombre());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        cuenta.setSaldo(new BigDecimal("2000"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        assertEquals("Pepe", cuentaActualizada.getNombre());
        assertEquals("2000", cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Hector", cuenta.getNombre());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByNombre("Hector").orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());

    }
}
