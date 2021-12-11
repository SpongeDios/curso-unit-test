package org.hector.test.springboot.app.services;

import lombok.AllArgsConstructor;
import org.hector.test.springboot.app.models.Banco;
import org.hector.test.springboot.app.models.Cuenta;
import org.hector.test.springboot.app.repositories.BancoRepository;
import org.hector.test.springboot.app.repositories.CuentaRepository;

import java.math.BigDecimal;

@AllArgsConstructor
public class CuentaServiceImpl implements CuentaService{

    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id);
    }

    @Override
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId);
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId);
        return cuenta.getSaldo();
    }

    @Override
    public void transferir(Long numeroCuentaOrigen, Long numeroCuentaDestino, BigDecimal monto
    , Long bancoId) {

        Cuenta cuentaOrigen = cuentaRepository.findById(numeroCuentaOrigen);
        cuentaOrigen.debito(monto);
        cuentaRepository.update(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(numeroCuentaDestino);
        cuentaDestino.credito(monto);
        cuentaRepository.update(cuentaDestino);

        Banco banco = bancoRepository.findById(bancoId);
        int totalTransferencias = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferencias);
        bancoRepository.update(banco);
    }
}
