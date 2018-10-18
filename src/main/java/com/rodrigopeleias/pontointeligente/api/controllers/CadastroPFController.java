package com.rodrigopeleias.pontointeligente.api.controllers;

import com.rodrigopeleias.pontointeligente.api.dto.CadastroPFDto;
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
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin("*")
public class CadastroPFController {

    public static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private FuncionarioService funcionarioService;

    @PostMapping
    public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto,
                                                             BindingResult result) throws NoSuchAlgorithmException {
        log.info("Cadastrando PF: {}", cadastroPFDto.toString());
        Response<CadastroPFDto> response = new Response<>();

        validarDadosExistentes(cadastroPFDto, result);
        Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPFDto, result);

        if (result.hasErrors()) {
            log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
        empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
        this.funcionarioService.persistir(funcionario);

        response.setData(this.converterCadastroPFDto(funcionario));
        return ResponseEntity.ok(response);
    }

    private void validarDadosExistentes(CadastroPFDto cadastroPFDto, BindingResult result) {
        Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
        if (!empresa.isPresent()) {
            result.addError(new ObjectError("empresa", "Empresa não cadastrada."));
        }

        this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

        this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
                .ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
    }

    private Funcionario converterDtoParaFuncionario(CadastroPFDto cadastroPFDto, BindingResult result) {
        Funcionario funcionario = Funcionario.builder()
                .nome(cadastroPFDto.getNome())
                .email(cadastroPFDto.getEmail())
                .cpf(cadastroPFDto.getEmail())
                .perfil(PerfilEnum.ROLE_USUARIO)
                .senha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha().get()))
                .build();

        cadastroPFDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
        cadastroPFDto.getValorHora()
                .ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        return funcionario;
    }

    private CadastroPFDto converterCadastroPFDto(Funcionario funcionario) {
        CadastroPFDto cadastroPFDto = CadastroPFDto.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .email(funcionario.getEmail())
                .cpf(funcionario.getCpf())
                .cnpj(funcionario.getEmpresa().getCnpj())
                .build();
        funcionario.getQtdHorasAlmocoOpt().ifPresent(
                qtdHorasAlmoco -> cadastroPFDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
        funcionario.getValorHoraOpt()
                .ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));
        return cadastroPFDto;
    }
}
