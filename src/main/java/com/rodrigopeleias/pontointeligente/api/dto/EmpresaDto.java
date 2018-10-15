package com.rodrigopeleias.pontointeligente.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class EmpresaDto {

    private Long id;
    private String razaoSocial;
    private String cnpj;

    @Override
    public String toString() {
        return "EmpresaDto{" +
                "id=" + id +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
