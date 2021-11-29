package org.hector.junit5app.ejemplo.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Banco {
    private String nombre;
    private List<Cuenta> cuentas = new ArrayList<>();

    public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto){
        origen.setSaldo(origen.getSaldo().subtract(monto));
        destino.setSaldo(destino.getSaldo().add(monto));

    }

    public void addCuenta(Cuenta cuenta){
        cuentas.add(cuenta);
        cuenta.setBanco(this);
    }
}
