package com.rodrigopeleias.pontointeligente.api.controllers;

import com.rodrigopeleias.pontointeligente.api.dto.FuncionarioDto;
import com.rodrigopeleias.pontointeligente.api.entities.Funcionario;
import com.rodrigopeleias.pontointeligente.api.response.Response;
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
@RequestMapping("/api/funcionarios")
@CrossOrigin("*")
public class FuncionarioController {

    public static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id,
                                                              @Valid @RequestBody FuncionarioDto funcionarioDto,
                                                              BindingResult result) throws NoSuchAlgorithmException {
        log.info("Atualizando funcionário: {}", funcionarioDto.toString());
        Response<FuncionarioDto> response = new Response<>();

        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
        if (!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionário não encontrado."));
        }

        this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);

        if(result.hasErrors()) {
            log.error("Erro validando funcionário: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        this.funcionarioService.persistir(funcionario.get());
        response.setData(this.converterFuncionarioDto(funcionario.get()));

        return ResponseEntity.ok(response);
    }

    private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException {
        funcionario.setNome(funcionarioDto.getNome());

        if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
            this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
                    .ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
            funcionario.setEmail(funcionarioDto.getEmail());
        }
        funcionario.setQtdHorasTrabalhoDia(null);
        funcionarioDto.getQtdHorasTrabalhoDia()
                .ifPresent(qtdHorasTrabDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabDia)));

        funcionario.setQtdHorasAlmoco(null);
        funcionarioDto.getQtdHorasAlmoco()
                .ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));

        funcionario.setValorHora(null);
        funcionarioDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));

        if (funcionarioDto.getSenha().isPresent()) {
            funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
        }
    }

    private FuncionarioDto converterFuncionarioDto(Funcionario funcionario) {
        FuncionarioDto funcionarioDto = FuncionarioDto.builder()
                .id(funcionario.getId())
                .email(funcionario.getEmail())
                .nome(funcionario.getNome())
                .build();
        funcionario.getQtdHorasAlmocoOpt().ifPresent(
                qtdHorasAlmoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco)))
        );
        funcionario.getValorHoraOpt().ifPresent(
                valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString()))
        );
        return funcionarioDto;
    }
}
