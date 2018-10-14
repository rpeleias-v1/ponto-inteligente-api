package com.rodrigopeleias.pontointeligente.api.utils;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PasswordUtilsTest {

    public static final String SENHA = "123456";
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testSenhaNula() {
        assertNull(PasswordUtils.gerarBCrypt(null));
    }

    @Test
    public void testGerarHashSenha() {
        String hash = PasswordUtils.gerarBCrypt(SENHA);
        assertTrue(bCryptPasswordEncoder.matches(SENHA, hash));
    }
}
