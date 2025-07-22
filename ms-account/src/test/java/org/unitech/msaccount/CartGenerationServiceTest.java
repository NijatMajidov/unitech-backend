package org.unitech.msaccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitech.msaccount.model.dto.AccountDto;
import org.unitech.msaccount.model.enums.AccountStatus;
import org.unitech.msaccount.model.enums.Currency;
import org.unitech.msaccount.domain.repo.AccountRepository;
import org.unitech.msaccount.service.CartGenerationService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CartGenerationServiceTest {
    @InjectMocks
    private CartGenerationService cartGenerationService;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateCart_shouldReturnValidAccountDto() {
        when(accountRepository.existsByCartNumber(anyString())).thenReturn(false);

        AccountDto dto = cartGenerationService.generateCart(Currency.USD);

        assertNotNull(dto);
        assertEquals(Currency.USD, dto.getCurrency());
        assertEquals(BigDecimal.ZERO, dto.getBalance());
        assertEquals(AccountStatus.ACTIVE, dto.getAccountStatus());
        assertNotNull(dto.getCartNumber());
        assertNotNull(dto.getPin());
        assertNotNull(dto.getCvv());
    }

    @Test
    void generateCart_whenDuplicateCartGenerated_shouldRetry() {
        when(accountRepository.existsByCartNumber(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        AccountDto dto = cartGenerationService.generateCart(Currency.AZN);

        assertNotNull(dto);
        assertEquals(Currency.AZN, dto.getCurrency());
        verify(accountRepository, times(2)).existsByCartNumber(anyString());
    }
}


