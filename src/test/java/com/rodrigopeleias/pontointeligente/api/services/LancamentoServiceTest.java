package com.rodrigopeleias.pontointeligente.api.services;

import com.rodrigopeleias.pontointeligente.api.entities.Lancamento;
import com.rodrigopeleias.pontointeligente.api.repositories.LancamentoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @MockBean
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private LancamentoService lancamentoService;

    @Before
    public void setUp() throws Exception {
        given(this.lancamentoRepository.findByFuncionarioId(anyLong(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(new ArrayList<>()));
        given(this.lancamentoRepository.getOne(anyLong())).willReturn(new Lancamento());
        given(this.lancamentoRepository.save(any(Lancamento.class))).willReturn(new Lancamento());
    }

    @Test
    public void testBuscarLancamentoPorFuncionarioId() {
        Page<Lancamento> lancamento = this.lancamentoService.buscarPorFuncionarioId(1L, new PageRequest(0, 10));
        assertNotNull(lancamento);
    }

    @Test
    public void testBuscarLancamentoId() {
        Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(1L);
        assertTrue(lancamento.isPresent());
    }

    @Test
    public void testPersistirLancamento() {
        Lancamento lancamento = this.lancamentoService.persistir(new Lancamento());
        assertNotNull(lancamento);
    }
}
