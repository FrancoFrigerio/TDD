package ar.com.frigerio.springboot.app.domain;

import java.math.BigDecimal;

public class TransaccionDTO {
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private Long bancoId;
    private BigDecimal monto;


//    public TransaccionDTO(Long cuentaOrigenId, Long cuentaDestinoId, Long bancoId, BigDecimal monto) {
//        this.cuentaOrigenId = cuentaOrigenId;
//        this.cuentaDestinoId = cuentaDestinoId;
//        this.bancoId = bancoId;
//        this.monto = monto;
//    }

    public TransaccionDTO() {

    }

    public Long getBancoId() {
        return bancoId;
    }

    public void setBancoId(Long bancoId) {
        this.bancoId = bancoId;
    }

    public Long getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public void setCuentaOrigenId(Long cuentaOrigenId) {
        this.cuentaOrigenId = cuentaOrigenId;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(Long cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
