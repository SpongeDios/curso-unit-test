package org.hector.junit5app.ejemplo.models;

import org.hector.junit5app.ejemplo.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
    }

    @AfterEach
    void afterEach() {
        System.out.println("Finalizado el test");
    }

    @Test
    void test_nombre_cuenta() {
        cuenta.setPersona("Hector");
        Assertions.assertEquals("Hector", cuenta.getPersona());
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Test
    void test_saldo_cuenta() {
        cuenta.setSaldo(new BigDecimal("123.12345"));
        Assertions.assertEquals(123.12345, cuenta.getSaldo().doubleValue());
        Assertions.assertFalse(cuenta.getSaldo().doubleValue() < 0);
    }

    @Test
    void testReferenciaCuenta() {
        this.cuenta.setPersona("Jhon Doe");
        this.cuenta.setSaldo(new BigDecimal("8900.9997"));
        Cuenta cuenta = new Cuenta();
        cuenta.setPersona("Jhon Doe");
        cuenta.setSaldo(new BigDecimal("8900.9997"));

        //asserts
        Assertions.assertEquals(this.cuenta, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));
        cuenta.debito(new BigDecimal(100));

        //asserts
        Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertEquals(900, cuenta.getSaldo().intValue());
    }

    @Test
    void testCreditoCuenta() {
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));
        cuenta.credito(new BigDecimal(1000));

        //asserts
        Assertions.assertEquals(2000, cuenta.getSaldo().intValue());
        Assertions.assertEquals(2000, cuenta.getSaldo().intValue());
    }

    @Test
    void testDineroInsuficienteException() {
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));

        Assertions.assertThrows(DineroInsuficienteException.class, () -> cuenta.debito(new BigDecimal("100000")));
    }

    @Test
    void testTransferirDineroCuentas() {
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000"));

        Cuenta nuevaCuenta = new Cuenta();

        nuevaCuenta.setPersona("Hector");
        nuevaCuenta.setSaldo(new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.setNombre("Banco estado");

        banco.transferir(nuevaCuenta, cuenta, new BigDecimal("100"));

        Assertions.assertEquals("1100", cuenta.getSaldo().toPlainString());
        Assertions.assertEquals("900", nuevaCuenta.getSaldo().toPlainString());

    }

    @Test
    @DisplayName("Probando la relacion de bancos y cuentas!")
    void testRelacionBancoCuentas(TestInfo testInfo, TestReporter testReporter) {
        //testInfo traen info del test que se esta ejecutando (completamente opcional)
        //testReporter es un log
        System.out.println(" ejecutando: " + testInfo.getDisplayName() + " "+ testInfo.getTestMethod());
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000"));

        Cuenta nuevaCuenta = new Cuenta();

        nuevaCuenta.setPersona("Hector");
        nuevaCuenta.setSaldo(new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.setNombre("Banco estado");
        banco.addCuenta(nuevaCuenta);
        banco.addCuenta(cuenta);

        banco.transferir(nuevaCuenta, cuenta, new BigDecimal("100"));

        Assertions.assertEquals(2, banco.getCuentas().size());
        Assertions.assertEquals("Banco estado", cuenta.getBanco().getNombre());
        Assertions.assertEquals("Hector", banco.getCuentas().get(0).getPersona());


        //CUando un test falla, no se detiene, sigue testeando los demas assertions
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, banco.getCuentas().size(), "la cuenta tiene que ser 2"),
                () -> Assertions.assertEquals("Banco estado", cuenta.getBanco().getNombre()),
                () -> Assertions.assertEquals("Hector", banco.getCuentas().get(0).getPersona())
        );
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testOnlyMacAndLinux() {
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() {
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void onlyJdk11() {
    }

    @Test
    @DisabledOnJre(JRE.JAVA_11)
    void disabledOnJdk11() {
    }

    @Test
    void imprimirSistemProperties() {
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> System.out.println(k + ":" + v));
    }



    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "11.0.11")
    void testJavaVersion() {

    }

    @Test
    @EnabledIfSystemProperty(named = "user.name", matches = "Hector")
    void testUsername() {
    }

    @Test
    void imprimirVariablesDeAmbiente() {
        Map<String, String> getEnv = System.getenv();
        getEnv.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @Nested //<-- puedes anidar clases con test con caracterisitcas en comun
    class TestVaciosAnidados{

        @BeforeEach
        void setUp() {
            System.out.println("Se pueden usar los each dentro de esto :O!");
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.11.9-hotspot")
        void testJavaHome(){
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "4")
        void testProcesadores(){
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
        void testEnv() {
        }
    }

    @Test
    void test_saldo_cuenta_dev() {
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        cuenta.setSaldo(new BigDecimal("123.12345"));

        Assumptions.assumeTrue(esDev); // <--- se asume que sea true, si no lo es, no pasa nada, el test no fallara. Lo ignora

        Assertions.assertEquals(123.12345, cuenta.getSaldo().doubleValue());
        Assertions.assertFalse(cuenta.getSaldo().doubleValue() < 0);
    }

    @Test
    void test_saldo_cuenta_dev_false() {
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        cuenta.setSaldo(new BigDecimal("123.12345"));

        Assumptions.assumeFalse(esDev); // <--- se asume que sea true, si no lo es, no pasa nada, el test no fallara. Lo ignora

        Assertions.assertEquals(123.12345, cuenta.getSaldo().doubleValue());
        Assertions.assertFalse(cuenta.getSaldo().doubleValue() < 0);
    }

    @Test
    void test_saldo_cuenta_dev_with_lambda() {
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        cuenta.setSaldo(new BigDecimal("123.12345"));

        Assumptions.assumingThat(true, () -> {
            Assertions.assertEquals(123.12345, cuenta.getSaldo().doubleValue());
            Assertions.assertFalse(cuenta.getSaldo().doubleValue() < 0);
        });
    }

    //TEST REPETIDO <- SE PUEDE USAR CUANDO TENEMOS CIERTA ALEATORIEDAD
    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testTransferirDineroCuentasAleatorio(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3){
            System.out.println("Esta es la repeticion 3");
        }

        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000"));

        Cuenta nuevaCuenta = new Cuenta();

        nuevaCuenta.setPersona("Hector");
        nuevaCuenta.setSaldo(new BigDecimal("1000"));

        Banco banco = new Banco();
        banco.setNombre("Banco estado");

        banco.transferir(nuevaCuenta, cuenta, new BigDecimal("100"));

        Assertions.assertEquals("1100", cuenta.getSaldo().toPlainString());
        Assertions.assertEquals("900", nuevaCuenta.getSaldo().toPlainString());
    }


    //test con distintos parametros
    //ESTA DE PANA ESTOOOOOOOOOOOOOOOO------------------------------------------------
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
    void testDebitoCuentaParametrizer(String monto) {
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));
        cuenta.debito(new BigDecimal(monto));

        //asserts
        //Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    //test con distintos parametros
    //ESTA DE PANA ESTOOOOOOOOOOOOOOOO------------------------------------------------
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000"})
    void testDebitoCuentaParametrizerConCSV(String index, String monto) {
        System.out.println(index + " -> " + monto);
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));
        cuenta.debito(new BigDecimal(monto));

        //asserts
        //Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Tag("param") //<- se utiliza para etiquetar test o. EJ de uso: ejecutar todos los test que tengan el tag "param" (La palabra param va en el running de CuentaTest)
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"200,100", "201,200", "301,300", "650,500", "750,700", "1200,1000"})
    void testDebitoCuentaParametrizerConCSV2(String saldo, String monto) {
        System.out.println(saldo + " -> " + monto);
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.debito(new BigDecimal(monto));

        //asserts
        //Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Disabled
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    //puede recibir tantos parametros como nosotros queramos
    @CsvSource({"puede,recibir,muchos,parametros", "puede,recibir,muchos,parametros", "puede,recibir,muchos,parametros", "puede,recibir,muchos,parametros", "750,700,200,10", "1000,1000,1,2"})
    void testDebitoCuentaParametrizerConCSV3(String parametro1, String parametro2, String parametro3, String parametro4) {
        System.out.println(parametro1 + " -> " + parametro2);
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal(parametro3));
        cuenta.debito(new BigDecimal(parametro4));

        //asserts
        //Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    //test con distintos parametros
    //ESTA DE PANA ESTOOOOOOOOOOOOOOOO------------------------------------------------
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    void testDebitoCuentaParametrizerConCSVImportado(String monto) {
        System.out.println("monto = "+ monto);
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));
        cuenta.debito(new BigDecimal(monto));

        //asserts
        //Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList") //<-- nombre del metodo al que hace referencia
    void testDebitoCuentaParametrizerConMethodSource(String monto) {
        System.out.println("monto = "+ monto);
        cuenta.setPersona("Hector");
        cuenta.setSaldo(new BigDecimal("1000.123"));
        cuenta.debito(new BigDecimal(monto));

        //asserts
        //Assertions.assertEquals(900, cuenta.getSaldo().intValue());
        Assertions.assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList(){
        return Arrays.asList("100", "200", "300", "500", "700", "1000");
    }


    @Nested
    @Tag("timeout")
    class EjemploTimeOutTest {
        @Test
        @Timeout(5)
        void pruebaTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }

        @Test
        @Timeout(value = 5000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeOut2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }

        @Test
        void testTimeOutAssertios() {
            Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4500);
            });
        }
    }


}
