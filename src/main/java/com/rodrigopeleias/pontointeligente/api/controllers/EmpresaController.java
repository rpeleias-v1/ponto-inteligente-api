package com.rodrigopeleias.pontointeligente.api.controllers;

import com.rodrigopeleias.pontointeligente.api.dto.EmpresaDto;
import com.rodrigopeleias.pontointeligente.api.entities.Empresa;
import com.rodrigopeleias.pontointeligente.api.response.Response;
import com.rodrigopeleias.pontointeligente.api.services.EmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    public static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

    @Autowired
    private EmpresaService empresaService;

    @GetMapping(value = "/cnpj/{cnpj}")
    public ResponseEntity<Response<EmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
        log.info("Buscando empresa por cnpj: {}", cnpj);
        Response<EmpresaDto> response = new Response<>();
        Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

        if (!empresa.isPresent()) {
            log.info("Empresa não encontrada para o CNPJ: {}", cnpj);
            response.getErrors().add("Empresa não encontrada para o CNPJ " + cnpj);
            return  ResponseEntity.badRequest().body(response);
        }
        response.setData(this.converterEmpresaDto(empresa.get()));
        return ResponseEntity.ok(response);
    }

    private EmpresaDto converterEmpresaDto(Empresa empresa) {
        return EmpresaDto.builder()
                .id(empresa.getId())
                .cnpj(empresa.getCnpj())
                .razaoSocial(empresa.getRazaoSocial())
                .build();
    }
}
