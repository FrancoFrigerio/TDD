package ar.com.frigerio.springboot.app.controllers;

import static org.springframework.http.HttpStatus.*;
import ar.com.frigerio.springboot.app.domain.Cuenta;
import ar.com.frigerio.springboot.app.domain.TransaccionDTO;
import ar.com.frigerio.springboot.app.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    CuentaService service;



    @GetMapping
    @ResponseStatus(OK)
    public List<Cuenta> listar(){
        return service.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Cuenta cuenta = null;
        try {
         cuenta = service.findById(id);

       }catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
       }
        return ResponseEntity.ok(cuenta);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return service.save(cuenta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransaccionDTO dto) {
        service.transfereir(dto.getCuentaOrigenId(),dto.getCuentaDestinoId(),
        dto.getMonto(), dto.getBancoId());
        Map<String , Object> response = new HashMap<>();
            response.put("date" , LocalDate.now().toString());
            response.put("status" , "OK");
            response.put("mensaje" , "transferencia realizada con exito");
            response.put("transaccion" , dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        service.deleteById(id);
    }


}
