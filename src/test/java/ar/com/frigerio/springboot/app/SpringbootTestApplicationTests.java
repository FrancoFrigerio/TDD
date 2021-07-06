package ar.com.frigerio.springboot.app;

import static org.mockito.Mockito.*;
import ar.com.frigerio.springboot.app.dao.BancoDao;
import ar.com.frigerio.springboot.app.dao.CuentaDao;
import ar.com.frigerio.springboot.app.domain.Banco;
import ar.com.frigerio.springboot.app.domain.Cuenta;
import ar.com.frigerio.springboot.app.exceptions.DineroInsuficienteException;
import ar.com.frigerio.springboot.app.services.CuentaService;
import ar.com.frigerio.springboot.app.services.CuentaServiceImp;
import static ar.com.frigerio.springboot.app.Datos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	CuentaDao cuentaDao;
	@MockBean
	BancoDao bancoDao;

	@Autowired
	CuentaService service;

	@BeforeEach
	@Test
	void setUp() {
//		cuentaDao = mock(CuentaDao.class);
//		bancoDao = mock(BancoDao.class);
//
//		service = new CuentaServiceImp(cuentaDao,bancoDao);

//		Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
//		Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//		Datos.BANCO_001.setTotalTransferencias(0);
	}

	@Test
	void contextLoads() {
		//contexto o Given
		when(cuentaDao.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaDao.findById(2L)).thenReturn(crearCuenta002());
		when(bancoDao.findById(1L)).thenReturn(crearBanco001());
		//When
		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000" , saldoOrigen.toPlainString());
		assertEquals("2000" , saldoDestino.toPlainString());


		service.transfereir(1L,2L,new BigDecimal("100"),1L);
		int total = service.revisarTotalTransferencias(1L);
		saldoDestino = service.revisarSaldo(2L);
		saldoOrigen = service.revisarSaldo(1L);

		assertEquals("900" , saldoOrigen.toPlainString());
		assertEquals("2100" , saldoDestino.toPlainString());
		assertEquals(1 , total);

		verify(cuentaDao , times(3)).findById(1L);
		verify(cuentaDao , times(3)).findById(2L);
		verify(cuentaDao, times(2)).save(any());
		verify(bancoDao , times(2)).findById(1L);


		verify(cuentaDao , times(6)).findById(anyLong());
		verify(cuentaDao, never()).findAll();

	}


	@Test
	void contextLoads2() {
		//contexto o Given
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaDao.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoDao.findById(1L)).thenReturn(Datos.crearBanco001());
		//When
		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000" , saldoOrigen.toPlainString());
		assertEquals("2000" , saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class ,()->{
			service.transfereir(1L,2L,new BigDecimal("1500"),1L);
		});
//		service.transfereir(1L,2L,new BigDecimal("1500"),1L);

		int total = service.revisarTotalTransferencias(1L);
		saldoDestino = service.revisarSaldo(2L);
		saldoOrigen = service.revisarSaldo(1L);

		assertEquals("1000" , saldoOrigen.toPlainString());
		assertEquals("2000" , saldoDestino.toPlainString());
		assertEquals(0 , total);

		verify(cuentaDao , times(3)).findById(1L);
		verify(cuentaDao , times(2)).findById(2L);
		verify(cuentaDao, never()).save(any(Cuenta.class));

		verify(bancoDao , times(1)).findById(1L);
		verify(bancoDao , never()).save(any(Banco.class));

		verify(cuentaDao , times(5)).findById(anyLong());
		verify(cuentaDao,never()).findAll();
	}


	@Test
	void contextLoad3() {
		when(cuentaDao.findById(1L)).thenReturn(crearCuenta001());

		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1,cuenta2);
		assertTrue(cuenta1.equals(cuenta2));
		assertEquals(cuenta1,cuenta2);
		assertEquals("Franco" , cuenta1.getPersona());
		assertEquals("Franco" , cuenta1.getPersona());

		verify(cuentaDao,times(2)).findById(anyLong());
	}


	@Test
	void testFindAll() {
		//Given
		List<Cuenta> datos =  Arrays.asList(crearCuenta001().orElseThrow(),crearCuenta002().orElseThrow());
		when(cuentaDao.findAll()).thenReturn(datos);

		//when
		List<Cuenta> cuentas = service.findAll();

		//Then
		assertFalse(cuentas.isEmpty());
		assertEquals(2 , cuentas.size());
		assertTrue(cuentas.contains(crearCuenta001().orElseThrow()));

		verify(cuentaDao).findAll();
	}

	@Test
	void guardar() {
		//Given
		Cuenta pepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaDao.save(any())).then((invocation)->{
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return  c;
		});

		//when
		Cuenta cuenta = service.save(pepe);

		//then
		assertEquals("Pepe" , cuenta.getPersona());
		assertEquals(3,cuenta.getId());
		assertEquals("3000" , cuenta.getSaldo().toPlainString());
		verify(cuentaDao).save(any());
	}
}

