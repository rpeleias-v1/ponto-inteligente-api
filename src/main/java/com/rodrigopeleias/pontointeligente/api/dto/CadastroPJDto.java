package com.rodrigopeleias.pontointeligente.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@Setter
public class CadastroPJDto {

    private Long id;

    @NotEmpty(message = "Nome não pode ser vazio")
    @Length(min = 3, max = 200, message = "Nome deve conter entre 3 e 200 caracteres.")
    private String nome;

    @NotEmpty(message = "Email não pode ser vazio")
    @Length(min = 5, max = 200, message = "Email deve conter entre 5 e 200 caracteres.")
    @Email(message = "Email inválido")
    private String email;
    private String senha;

    @NotEmpty(message = "CPF não pode ser vazio")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotEmpty(message = "Razão social não pode ser vazio.")
    @Length(min = 5, max = 200, message = "Razão social deve conter entre 5 e 200 caracteres")
    private String razaoSocial;

    @NotEmpty(message = "CNPJ não pode ser vazio.")
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;
}
