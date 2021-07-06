package ar.com.frigerio.springboot.app.controllers;

import ar.com.frigerio.springboot.app.domain.Cuenta;
import ar.com.frigerio.springboot.app.domain.TransaccionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient client;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferir() throws JsonProcessingException {
        //given
        TransaccionDTO dto = new TransaccionDTO();
            dto.setCuentaOrigenId(1L);
            dto.setCuentaDestinoId(2L);
            dto.setMonto(new BigDecimal("100"));
            dto.setBancoId(1L);

        Map<String , Object> res = new HashMap<>();
        res.put("date" , LocalDate.now().toString());
        res.put("status" , "OK");
        res.put("mensaje" , "transferencia realizada con exito");
        res.put("transaccion" , dto);


        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange() //lo que espero por respuesta, en definitiva el exchange cambia del req al resp
                //Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response ->{
                    //PRIMERA FORMA DE PROBAR LOS VALORES --------------------
                    try {
                        JsonNode json = objectMapper.readTree(response.getResponseBody());
                        assertEquals("transferencia realizada con exito" , json.path("mensaje").asText());
                        assertEquals(1L , json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                    //SEGUNDA FORMA DE PROBAR LOS VALORES --------------------
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("transferencia realizada con exito"))
                .jsonPath("$.mensaje").value(value ->assertEquals("transferencia realizada con exito", value))
                .jsonPath("$.mensaje").isEqualTo("transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                ;


    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException {
        Cuenta cuenta = new Cuenta(1L,"Franco",new BigDecimal("900"));
        client.get().uri( "/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Franco")
                .jsonPath("$.saldo").isEqualTo(900)
        .json(objectMapper.writeValueAsString(cuenta)) //para comparar el json comple con un objeto completo
        ;
    }

    @Test
    @Order(3)
    void testDetalle2() {
        client.get().uri( "/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response ->{
                    Cuenta cuenta = response.getResponseBody();
                    assertNotNull(cuenta);
                    assertEquals("Paolo" , cuenta.getPersona());
                    assertEquals("2100.00" , cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(4)
    void listar() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Franco")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].persona").isEqualTo("Paolo")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));
    }

    @Test
    @Order(5)
    void listar2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response ->{
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2,cuentas.size());
                    assertEquals("Paolo" , cuentas.stream().filter(cuenta -> cuenta.getPersona().equals("Paolo")).findFirst().get().getPersona());
                    assertEquals("Franco" , cuentas.stream().filter(cuenta -> cuenta.getPersona().equals("Franco")).findFirst().get().getPersona());
                    assertEquals("Paolo" , cuentas.get(1).getPersona());
                    assertEquals(2L , cuentas.get(1).getId());
                    assertEquals("2100.0" , cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2) //metodo del BodyList
                .value(hasSize(2)); //matchers
    }

    @Test
    @Order(6)
    void guardar() {
        //given
        Cuenta cuenta = new Cuenta(null,"Frigerio",new BigDecimal("3000"));
        //when
        client.post().uri("/api/cuentas/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta) //de forma automatica lo convierte en json y lo envia como json
                .exchange()
               //then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Frigerio")
                .jsonPath("$.persona").value(is("Frigerio"))
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.saldo").isEqualTo(3000);

    }

    @Test
    @Order(7)
    void guardar2() {
        //given
        Cuenta cuenta = new Cuenta(null,"Sandez",new BigDecimal("3500"));
        //when
        client.post().uri("/api/cuentas/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta) //de forma automatica lo convierte en json y lo envia como json
                .exchange()
               //then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response->{
                   Cuenta c = response.getResponseBody();
                   assertNotNull(c);
                   assertEquals("Sandez" , c.getPersona());
                   assertEquals("3500" , c.getSaldo().toPlainString());
                   assertEquals(4L , c.getId());
                });
    }

    @Test
    @Order(8)
    void eliminar() {
        //CONTROLAMOS LA CANTIDAD INICIAL DE ELEMENTOS
        client.get().uri("api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);
        //ELIMINAMOS EL ELEMENTO 3 EN ESTE CASO
        client.delete().uri("api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
        //CONTROLAMOS NUEVAMENTE LA CANTIDAD
        client.get().uri("api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/3")
                .exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                    .expectBody().isEmpty();

    }
}