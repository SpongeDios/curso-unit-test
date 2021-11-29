package org.hector.junit5app.ejemplo.models;

import lombok.Getter;
import lombok.Setter;
import org.hector.junit5app.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

@Getter
@Setter
public class Cuenta {

    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public void debito(BigDecimal monto){
        if (saldo.subtract(monto).compareTo(BigDecimal.ZERO) < 0){
            throw new DineroInsuficienteException("Dinero insuficiente para realizar la operacion");
        }
        saldo = saldo.subtract(monto);
    }

    public void credito(BigDecimal monto){
        saldo = saldo.add(monto);
    }









    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cuenta)){
            return false;
        }

        Cuenta c = (Cuenta) obj;
        if (this.persona == null || this.saldo == null){
            return false;
        }

        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
    }


}
