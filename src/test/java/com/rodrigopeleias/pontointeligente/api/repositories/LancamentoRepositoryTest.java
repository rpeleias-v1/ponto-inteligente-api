package com.rodrigopeleias.pontointeligente.api.repositories;

import com.rodrigopeleias.pontointeligente.api.entities.Empresa;
import com.rodrigopeleias.pontointeligente.api.entities.Funcionario;
import com.rodrigopeleias.pontointeligente.api.entities.Lancamento;
import com.rodrigopeleias.pontointeligente.api.enums.PerfilEnum;
import com.rodrigopeleias.pontointeligente.api.enums.TipoEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private Long funcionarioId;

    @Before
    public void setUp() throws Exception {
        Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());

        Funcionario funcionario = this.funcionarioRepository.save(obterDadosFuncionario(empresa));
        this.funcionarioId = funcionario.getId();

        this.lancamentoRepository.save(obterDadosLancamentos(funcionario));
        this.lancamentoRepository.save(obterDadosLancamentos(funcionario));
    }

    @After
    public void tearDown() throws Exception {
        this.empresaRepository.deleteAll();
    }

    @Test
    public void testBuscarLancamentosPorFuncionarioId() {
        List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId);
        assertEquals(2, lancamentos.size());
    }

    @Test
    public void testBuscarLancamentosPorFuncionarioIdPaginado() {
        PageRequest page = new PageRequest(0, 10);
        Page<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId, page);

        assertEquals(2, lancamentos.getTotalElements());
    }

    public Empresa obterDadosEmpresa() {
        return Empresa.builder()
                .razaoSocial("Empresa XPTO")
                .cnpj("51463645000100")
                .build();
    }

    public Funcionario obterDadosFuncionario(Empresa empresa) {
        return Funcionario.builder()
                .nome("Rodrigo Peleias")
                .perfil(PerfilEnum.ROLE_USUARIO)
                .senha("123456")
                .cpf("12345678910")
                .email("rodrigo@email.com")
                .empresa(empresa)
                .build();
    }

    public Lancamento obterDadosLancamentos(Funcionario funcionario) {
        return Lancamento.builder()
                .data(new Date())
                .tipo(TipoEnum.INICIO_ALMOCO)
                .funcionario(funcionario)
                .build();
    }
}
