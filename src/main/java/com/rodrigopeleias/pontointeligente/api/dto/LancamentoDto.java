package com.rodrigopeleias.pontointeligente.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Builder
@Getter
@Setter
public class LancamentoDto {

    private Optional<Long> id = Optional.empty();

    @NotEmpty(message = "Data não pode ser vazia.")
    private String data;
    private String tipo;
    private String descricao;
    private String localizacao;
    private Long funcionarioId;

    @Override
    public String toString() {
        return "LancamentoDto{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", tipo='" + tipo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", funcionarioId=" + funcionarioId +
                '}';
    }
}
