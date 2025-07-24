package org.unitech.mstransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.unitech.msaccount.service.AccountService;
import org.unitech.mstransfer.client.AccountClient;
import org.unitech.mstransfer.model.dto.response.AccountResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {
    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountById_shouldReturnAccount() {
        AccountResponse mockResponse = new AccountResponse();
        mockResponse.setId(1L);
        mockResponse.setBalance(new BigDecimal("500"));

        when(accountClient.getAccountById(1L)).thenReturn(mockResponse);

        org.unitech.msaccount.model.dto.response.AccountResponse response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("500"), response.getBalance());
    }

    @Test
    void updateBalance_shouldCallClient() {
        accountService.updateBalance(1L, new BigDecimal("100"));
        verify(accountClient, times(1)).updateBalance(1L, new BigDecimal("100"));
    }
}


