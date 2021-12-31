package org.hector.test.springboot.app.controllers;

import org.hector.test.springboot.app.models.Cuenta;
import org.hector.test.springboot.app.models.TransaccionDto;
import org.hector.test.springboot.app.services.CuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> listar(){
        return cuentaService.findAll();
    }





    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> detalle(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(cuentaService.findById(id));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return cuentaService.save(cuenta);
    }






    @PostMapping("/transferir")
    public ResponseEntity<Object> transferir(@RequestBody TransaccionDto dto){
        cuentaService.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);
        return ResponseEntity.ok(response);
    }
}