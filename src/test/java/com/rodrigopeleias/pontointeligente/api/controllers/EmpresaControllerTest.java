package com.rodrigopeleias.pontointeligente.api.controllers;

import com.rodrigopeleias.pontointeligente.api.entities.Empresa;
import com.rodrigopeleias.pontointeligente.api.services.EmpresaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmpresaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmpresaService empresaService;

    public static final String BUSCAR_EMPRESA_CNPJ_URL = "/api/empresas/cnpj/";
    public static final Long ID = Long.valueOf(1);
    private static final String CNPJ = "51463645000100";
    private static final String RAZAO_SOCIAL = "Empresa XYZ";

    @Test
    @WithMockUser
    public void testBuscaremresaCnpjInvalido() throws Exception{
        BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").value("Empresa não encontrada para o CNPJ " + CNPJ));
    }

    private Empresa obterDadosEmpresa() {
        return Empresa.builder()
                .id(ID)
                .razaoSocial(RAZAO_SOCIAL)
                .cnpj(CNPJ)
                .build();
    }
}
