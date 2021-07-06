package ar.com.frigerio.springboot.app;

import ar.com.frigerio.springboot.app.domain.Banco;
import ar.com.frigerio.springboot.app.domain.Cuenta;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

public class Datos {

//    public final static Cuenta CUENTA_001 = new Cuenta(1L,"Franco", new BigDecimal("1000"));
//    public final static Cuenta CUENTA_002 = new Cuenta(2L,"Franco Paolo", new BigDecimal("2000"));
//    public final static Banco BANCO_001 = new Banco(1L,"Banco financiero",0);


    public static Optional<Cuenta> crearCuenta001(){
        return Optional.of(new Cuenta(1L,"Franco", new BigDecimal("1000")));
    }
    public static Optional<Cuenta> crearCuenta002(){
        return Optional.of(new Cuenta(2L,"Paolo", new BigDecimal("2000")));
    }
    public static Optional<Banco> crearBanco001(){
        return Optional.of(new Banco(1L,"Banco financiero",0));

    }


}
