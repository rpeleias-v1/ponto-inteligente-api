package com.rodrigopeleias.pontointeligente.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigopeleias.pontointeligente.api.dto.LancamentoDto;
import com.rodrigopeleias.pontointeligente.api.entities.Funcionario;
import com.rodrigopeleias.pontointeligente.api.entities.Lancamento;
import com.rodrigopeleias.pontointeligente.api.enums.TipoEnum;
import com.rodrigopeleias.pontointeligente.api.services.FuncionarioService;
import com.rodrigopeleias.pontointeligente.api.services.LancamentoService;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LancamentoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LancamentoService lancamentoService;

    @MockBean
    private FuncionarioService funcionarioService;

    public static final String URL_BASE = "/api/lancamentos/";
    public static final Long ID_FUNCIONARIO = 1L;
    public static final Long ID_LANCAMENTO = 1L;
    public static final String TIPO = TipoEnum.INICIO_TRABALHO.name();
    public static final Date DATA = new Date();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    @WithMockUser
    public void testCadastroLancamento() throws Exception{
        Lancamento lancamento = obterDadosLancamento();
        given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Funcionario()));
        given(this.lancamentoService.persistir(Mockito.any(Lancamento.class))).willReturn(lancamento);

        mvc.perform(post(URL_BASE)
                .with(csrf())
                .content(this.obterJsonRequisicaoPost())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ID_LANCAMENTO))
                .andExpect(jsonPath("$.data.tipo").value(TIPO))
                .andExpect(jsonPath("$.data.data").value(this.dateFormat.format(DATA)))
                .andExpect(jsonPath("$.data.funcionarioId").value(ID_FUNCIONARIO))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @WithMockUser
    public void testCadastrarLancamentoFuncionarioIdInvalido() throws Exception{
        given(this.funcionarioService.buscarPorId(anyLong())).willReturn(Optional.empty());

        mvc.perform(post(URL_BASE)
                .with(csrf())
                .content(this.obterJsonRequisicaoPost())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Funcionário não encontrado. ID inexistente"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    public void testRemoverLancamento() throws Exception {
        given(this.lancamentoService.buscarPorId(anyLong())).willReturn(Optional.of(new Lancamento()));

        mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testRemoverLancamentoAcessoNegado() throws Exception {
        given(this.lancamentoService.buscarPorId(anyLong())).willReturn(Optional.of(new Lancamento()));

        mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + ID_LANCAMENTO)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private String obterJsonRequisicaoPost() throws JsonProcessingException {
        LancamentoDto lancamentoDto = LancamentoDto.builder()
                .id(null)
                .data(this.dateFormat.format(DATA))
                .tipo(TIPO)
                .funcionarioId(ID_FUNCIONARIO)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(lancamentoDto);

    }

    private Lancamento obterDadosLancamento() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(ID_FUNCIONARIO);
        return Lancamento.builder()
                .id(ID_LANCAMENTO)
                .data(DATA)
                .tipo(TipoEnum.valueOf(TIPO))
                .funcionario(funcionario)
                .build();
    }
}
