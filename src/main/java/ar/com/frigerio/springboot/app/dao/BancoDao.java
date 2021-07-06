package ar.com.frigerio.springboot.app.dao;

import ar.com.frigerio.springboot.app.domain.Banco;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BancoDao extends JpaRepository<Banco,Long> {

}
