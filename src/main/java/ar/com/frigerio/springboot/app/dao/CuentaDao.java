package ar.com.frigerio.springboot.app.dao;

import ar.com.frigerio.springboot.app.domain.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaDao extends JpaRepository<Cuenta,Long> {

//    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> findByPersona(String persona);

}
