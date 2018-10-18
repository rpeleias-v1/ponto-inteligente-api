package com.rodrigopeleias.pontointeligente.api.controllers;

import com.rodrigopeleias.pontointeligente.api.dto.CadastroPJDto;
import com.rodrigopeleias.pontointeligente.api.entities.Empresa;
import com.rodrigopeleias.pontointeligente.api.entities.Funcionario;
import com.rodrigopeleias.pontointeligente.api.enums.PerfilEnum;
import com.rodrigopeleias.pontointeligente.api.response.Response;
import com.rodrigopeleias.pontointeligente.api.services.EmpresaService;
import com.rodrigopeleias.pontointeligente.api.services.FuncionarioService;
import com.rodrigopeleias.pontointeligente.api.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin("*")
public class CadastroPJController {

    public static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto,
                                                             BindingResult result) throws NoSuchAlgorithmException {
        log.info("Cadastrando PJ: {}", cadastroPJDto.toString());
        Response<CadastroPJDto> response = new Response<>();

        validarDadosExistentes(cadastroPJDto, result);
        Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDto);
        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPJDto, result);

        if (result.hasErrors()) {
            log.error("Erro validando dados de cadasro PJ: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.empresaService.persistir(empresa);
        funcionario.setEmpresa(empresa);
        this.funcionarioService.persistir(funcionario);

        response.setData(this.converterCadastroPJDto(funcionario));
        return ResponseEntity.ok(response);
    }

    private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
        this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
                .ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));
        this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
                .ifPresent(funcionario -> result.addError(new ObjectError("funcionario", "CPF já existente.")));
        this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
                .ifPresent(funcionario -> result.addError(new ObjectError("funcionario", "Email já existente.")));
    }

    private Empresa converterDtoParaEmpresa(CadastroPJDto cadastroPJDto) {
        return Empresa.builder()
                .cnpj(cadastroPJDto.getCnpj())
                .razaoSocial(cadastroPJDto.getRazaoSocial())
                .build();
    }

    private Funcionario converterDtoParaFuncionario(CadastroPJDto cadastroPJDto, BindingResult result) throws NoSuchAlgorithmException {
        return Funcionario.builder()
                .nome(cadastroPJDto.getNome())
                .email(cadastroPJDto.getEmail())
                .cpf(cadastroPJDto.getCpf())
                .perfil(PerfilEnum.ROLE_ADMIN)
                .senha(PasswordUtils.gerarBCrypt(cadastroPJDto.getSenha()))
                .build();
    }

    private CadastroPJDto converterCadastroPJDto(Funcionario funcionario) {
        return CadastroPJDto.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .email(funcionario.getEmail())
                .cpf(funcionario.getCpf())
                .razaoSocial(funcionario.getEmpresa().getRazaoSocial())
                .cnpj(funcionario.getEmpresa().getCnpj())
                .build();
    }
}
