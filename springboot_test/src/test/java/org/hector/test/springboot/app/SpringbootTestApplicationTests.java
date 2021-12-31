package org.hector.test.springboot.app;

import org.hector.test.springboot.app.exceptions.DineroInsuficienteException;
import org.hector.test.springboot.app.models.Banco;
import org.hector.test.springboot.app.models.Cuenta;
import org.hector.test.springboot.app.repositories.BancoRepository;
import org.hector.test.springboot.app.repositories.CuentaRepository;
import org.hector.test.springboot.app.services.CuentaService;
import org.hector.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hector.test.springboot.app.Datos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SpringbootTestApplicationTests {
    /*

    * ****** VERSION SPRING *********


    @MockBean
    CuentaRepository cuentaRepository;

    @MockBean
    BancoRepository bancoRepository;

    @Autowired
    CuentaService cuentaService;

    ******** VERSION SPRING ********

    */

    @Mock
    CuentaRepository cuentaRepository;

    @Mock
    BancoRepository bancoRepository;

    CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);
    }

    @Test
    void contextLoads() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
        BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        cuentaService.transferir(1L, 2L, new BigDecimal("500"), 1L);

        saldoOrigen = cuentaService.revisarSaldo(1L);
        saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("500", saldoOrigen.toPlainString());
        assertEquals("2500", saldoDestino.toPlainString());

        int total = cuentaService.revisarTotalTransferencias(1L);

        assertEquals(1, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).save(any(Banco.class));

        verify(cuentaRepository, times(6)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();
    }

    @Test
    void contextLoads2() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());

        BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
        BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        assertThrows(DineroInsuficienteException.class, () -> cuentaService.transferir(1L, 2L, new BigDecimal("5000"), 1L));
        saldoOrigen = cuentaService.revisarSaldo(1L);
        saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        int total = cuentaService.revisarTotalTransferencias(1L);

        assertEquals(0, total);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(2)).findById(2L);
        verify(cuentaRepository, never()).save(any(Cuenta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).save(any(Banco.class));

        verify(cuentaRepository, times(5)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();

    }

    @Test
    void contextLoads3() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
        Cuenta cuenta1 = cuentaService.findById(1L);
        Cuenta cuenta2 = cuentaService.findById(1L);

        assertSame(cuenta1, cuenta2); // --> Abreviatura de assertTrue(cuenta1 == cuenta2)
        assertEquals("Andres" ,cuenta1.getNombre());
        assertEquals("Andres" ,cuenta2.getNombre());
        verify(cuentaRepository, times(2)).findById(1L);

    }

    @Test
    void testFindAll() {
        //arrange
        List<Cuenta> cuentas = Arrays.asList(crearCuenta001().orElseThrow(), crearCuenta002().orElseThrow());
        when(cuentaRepository.findAll()).thenReturn(cuentas);

        //act
        List<Cuenta> response = cuentaService.findAll();

        //asserts
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));
        verify(cuentaRepository).findAll();
    }

    @Test
    void testSave() {
        //arrange
        Cuenta cuenta = new Cuenta(null, "Jose", new BigDecimal("5000"));
        when(cuentaRepository.save(any(Cuenta.class))).then(invocation -> {
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        //act
        Cuenta response = cuentaService.save(cuenta);

        //asserts
        assertEquals("Jose", response.getNombre());
        assertEquals("5000", response.getSaldo().toPlainString());
        assertEquals(3L, response.getId());

        verify(cuentaRepository).save(any(Cuenta.class));
    }
}
