package ar.com.frigerio.springboot.app.services;

import ar.com.frigerio.springboot.app.dao.BancoDao;
import ar.com.frigerio.springboot.app.dao.CuentaDao;
import ar.com.frigerio.springboot.app.domain.Banco;
import ar.com.frigerio.springboot.app.domain.Cuenta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaServiceImp implements CuentaService{

    private CuentaDao cuentaDao;
    private BancoDao bancoDao;

    public CuentaServiceImp(CuentaDao cuentaDao, BancoDao bancoDao) {
        this.cuentaDao = cuentaDao;
        this.bancoDao = bancoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaDao.findAll();
    }
    @Override
    @Transactional
    public Cuenta save(Cuenta cuenta) {
         return cuentaDao.save(cuenta);
    }

    @Override
    @Transactional
    public void deleteById(Long Id) {
        cuentaDao.deleteById(Id);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long id) {
        return cuentaDao.findById(id).orElseThrow();
    }


    @Override
    @Transactional(readOnly = true)
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoDao.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaDao.findById(cuentaId).orElseThrow();
        return cuenta.getSaldo();
    }

    @Override
    @Transactional
    public void transfereir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long idBanco) {

        Cuenta cuentaOrigen = cuentaDao.findById(numCuentaOrigen).orElseThrow();
            cuentaOrigen.debito(monto);
            cuentaDao.save(cuentaOrigen);

        Cuenta cuentaDestino = cuentaDao.findById(numCuentaDestino).orElseThrow();
            cuentaDestino.credito(monto);
            cuentaDao.save(cuentaOrigen);
        Banco banco = bancoDao.findById(idBanco).orElseThrow();
        int totalTransferencias = banco.getTotalTransferencias();
        banco.setTotalTransferencias(++totalTransferencias);
        bancoDao.save(banco);
    }
}
