package com.rodrigopeleias.pontointeligente.api.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Builder
public class CadastroPFDto {

    private Long id;

    @NotEmpty(message = "Nome não pode ser vazio")
    @Length(min = 3, max = 200, message = "Nome deve conter entre 3 e 200 caracteres.")
    private String nome;

    @NotEmpty(message = "Email não pode ser vazio")
    @Length(min = 5, max = 200, message = "Email deve conter entre 5 e 200 caracteres.")
    @Email(message = "Email inválido")
    private String email;

    @NotEmpty(message = "CPF não pode ser vazio")
    @CPF(message = "CPF inválido")
    private String cpf;
    private Optional<String> senha = Optional.empty();
    private Optional<String> valorHora = Optional.empty();
    private Optional<String> qtdHorasTrabalhoDia = Optional.empty();
    private Optional<String> qtdHorasAlmoco = Optional.empty();

    @NotEmpty(message = "CNPJ não pode ser vazio.")
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;

    @Override
    public String toString() {
        return "CadastroPFDto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", senha=" + senha +
                ", valorHora=" + valorHora +
                ", qtdHorasTrabalhoDia=" + qtdHorasTrabalhoDia +
                ", qtdHorasAlmoco=" + qtdHorasAlmoco +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
