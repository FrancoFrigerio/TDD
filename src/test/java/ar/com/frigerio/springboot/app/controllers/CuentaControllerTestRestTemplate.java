package ar.com.frigerio.springboot.app.controllers;

import ar.com.frigerio.springboot.app.domain.Cuenta;
import ar.com.frigerio.springboot.app.domain.TransaccionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.print.attribute.standard.Media;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@Tag("integracion_rt")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplate {

    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate client;


    @LocalServerPort
    private int puerto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();

    }

    @Test
    @Order(1)
    void transferir() throws JsonProcessingException {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setMonto(new BigDecimal("100"));
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);

        ResponseEntity<String> response =
                                        //http:localhost:8080/api/cuentas/transferir... --> de esta forma obliga a levantar el servidor
                client.postForEntity("/api/cuentas/transferir", dto, String.class);
        String json= response.getBody();
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("transferencia realizada con exito"));
        System.out.println(puerto);

        JsonNode jsonNode = mapper.readTree(json);
        assertEquals("transferencia realizada con exito" , jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

        Map<String , Object> response1 = new HashMap<>();
        response1.put("date" , LocalDate.now().toString());
        response1.put("status" , "OK");
        response1.put("mensaje" , "transferencia realizada con exito");
        response1.put("transaccion" , dto);
        assertEquals(mapper.writeValueAsString(response1) , json);
    }

    @Test
    @Order(2)
    void detalle() {
        ResponseEntity<Cuenta> respuesta = client.getForEntity("/api/cuentas/1", Cuenta.class);
            Cuenta cuenta = respuesta.getBody();
                assertEquals(HttpStatus.OK,respuesta.getStatusCode());
                assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

                assertNotNull(cuenta);
                assertEquals("Franco" , cuenta.getPersona());
                assertEquals(1L , cuenta.getId());
                assertEquals("900.00" , cuenta.getSaldo().toPlainString());
                assertEquals(new Cuenta(1L,"Franco",new BigDecimal("900.00")),cuenta);
    }

    @Test
    @Order(3)
    void listar() throws JsonProcessingException {
        ResponseEntity<Cuenta[]> respuesta = client.getForEntity("/api/cuentas", Cuenta[].class);
           assertNotNull(respuesta.getBody());
            List<Cuenta> cuentas= Arrays.asList(respuesta.getBody());

                assertEquals(HttpStatus.OK,respuesta.getStatusCode());
                assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
            assertEquals(2, cuentas.size());
            assertEquals("Franco",cuentas.get(0).getPersona());
            assertEquals("Paolo", cuentas.stream().filter(cuenta -> cuenta.getPersona().equals("Paolo")).findFirst().orElseThrow().getPersona());
            assertEquals(1L , cuentas.get(0).getId());
            assertEquals(2L, cuentas.stream().filter(cuenta -> cuenta.getId().equals(2L)).findFirst().orElseThrow().getId());

        JsonNode jsonNode = mapper.readTree(mapper.writeValueAsString(cuentas));
        assertEquals("Franco" , jsonNode.get(0).path("persona").asText());
        assertEquals(1L , jsonNode.get(0).path("id").asLong());
        assertEquals("900.0" , jsonNode.get(0).path("saldo").asText());

    }

    @Test
    @Order(4)
    void guardar() {
        Cuenta cuenta = new Cuenta(null,"Pepa",new BigDecimal("3800"));
        ResponseEntity<Cuenta> response = client.postForEntity("/api/cuentas", cuenta, Cuenta.class);
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        Cuenta cuentaCreada = response.getBody();
        assertNotNull(cuentaCreada);
        assertEquals("Pepa",cuentaCreada.getPersona());
        assertEquals("3800",cuentaCreada.getSaldo().toPlainString());
    }

    @Test
    @Order(5)
    void eliminar() {
        ResponseEntity<Cuenta[]> respuesta = client.getForEntity("/api/cuentas", Cuenta[].class);
        assertNotNull(respuesta.getBody());
        List<Cuenta> cuentas= Arrays.asList(respuesta.getBody());
        assertEquals(3, cuentas.size());

             client.delete("/api/cuentas/3");

        respuesta = client.getForEntity("/api/cuentas", Cuenta[].class);
        assertNotNull(respuesta.getBody());
            cuentas= Arrays.asList(respuesta.getBody());
        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> respuestaDetalle = client.getForEntity("/api/cuentas/3", Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND,respuestaDetalle.getStatusCode());
        assertFalse(respuestaDetalle.hasBody());
    }
}