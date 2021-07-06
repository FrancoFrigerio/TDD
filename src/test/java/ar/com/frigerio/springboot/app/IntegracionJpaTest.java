package ar.com.frigerio.springboot.app;


import ar.com.frigerio.springboot.app.dao.CuentaDao;
import ar.com.frigerio.springboot.app.domain.Cuenta;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Tag("integracion_jpa")
@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    CuentaDao cuentaDao;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaDao.findById(1L);

        assertTrue(cuenta.isPresent());
        assertEquals("Franco",cuenta.orElseThrow().getPersona());

    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaDao.findByPersona("Franco");

        assertTrue(cuenta.isPresent());
        assertEquals("Franco",cuenta.orElseThrow().getPersona());
        assertEquals("1000.00",cuenta.orElseThrow().getSaldo().toPlainString());

    }
    @Test
    void testFindByPersona2() {
        Optional<Cuenta> cuenta = cuentaDao.findByPersona("Franco2");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());


    }@Test
    void testfindAll() {
        List<Cuenta> cuentas = cuentaDao.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2,cuentas.size());


    }

    @Test
    void testSave() {
        //Given dado una cuenta
        Cuenta cuentaGuardar = new Cuenta(null,"Pepe",new BigDecimal("3000"));

        //When la persistimos, obtenemos la cuenta
        //Cuenta pepe = cuentaDao.findByPersona("Pepe").orElseThrow();
        //Cuenta pepe = cuentaDao.findById(save.getId()).orElseThrow();
        Cuenta save = cuentaDao.save(cuentaGuardar);



         //Then probamos la cuenta
        assertEquals("Pepe" , save.getPersona());
        assertEquals("3000" , save.getSaldo().toPlainString());
//        assertEquals(3, pepe.getId());
    }

    @Test
    void testUpdate() {
        //Given
        Cuenta cuentaGuardar = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        //When
        Cuenta save = cuentaDao.save(cuentaGuardar);
        //Then
        assertEquals("Pepe" , save.getPersona());
        assertEquals("3000" , save.getSaldo().toPlainString());

        save.setSaldo(new BigDecimal("3800"));

        Cuenta cuentaActualizada = cuentaDao.save(save);
        //Then
        assertEquals("Pepe" , cuentaActualizada.getPersona());
        assertEquals("3800" , cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void delete() {
        Cuenta cuenta = cuentaDao.findById(2L).orElseThrow();

        assertEquals("Paolo" , cuenta.getPersona());
        cuentaDao.delete(cuenta);

        assertThrows(NoSuchElementException.class,()->{
            cuentaDao.findById(2L).orElseThrow();
        });
        assertEquals(1, cuentaDao.findAll().size());
    }
}
