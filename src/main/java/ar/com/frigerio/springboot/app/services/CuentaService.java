package ar.com.frigerio.springboot.app.services;


import ar.com.frigerio.springboot.app.domain.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    List<Cuenta> findAll();

    Cuenta findById(Long id);

    Cuenta save(Cuenta cuenta);

    void deleteById(Long Id);

    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo (Long cuentaId);

    void transfereir(Long cuentaOrigen, Long cuentaDestino, BigDecimal monto, Long idBanco);


}
