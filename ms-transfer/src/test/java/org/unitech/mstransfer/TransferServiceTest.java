package org.unitech.mstransfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.unitech.msaccount.service.AccountService;
import org.unitech.mstransfer.domain.entity.Transfer;
import org.unitech.mstransfer.domain.repo.TransferDao;
import org.unitech.mstransfer.mapper.TransferMapper;
import org.unitech.mstransfer.model.dto.request.TransferRequest;
import org.unitech.mstransfer.model.dto.response.AccountResponse;
import org.unitech.mstransfer.model.dto.response.CurrencyResponse;
import org.unitech.mstransfer.service.TransferService;
import org.unitech.service.CurrencyService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private TransferMapper transferMapper;
    @Mock
    private TransferDao transferDao;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void createTransfer_shouldReturnResponse_whenSuccessful() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100"));

        AccountResponse fromAccount = new AccountResponse(1L, 1L, "USD", new BigDecimal("500"), "ACTIVE");
        AccountResponse toAccount = new AccountResponse(2L, 2L, "EUR", new BigDecimal("200"), "ACTIVE");

        CurrencyResponse currencyResponse = new CurrencyResponse("USD", "EUR", new BigDecimal("0.9"));
        Transfer mockTransfer = new Transfer();
        mockTransfer.setId(1L);

        when(accountService.getAccountById(1L)).thenReturn(fromAccount);
        when(accountService.getAccountById(2L)).thenReturn(toAccount);
        when(currencyService.getExchangeRate("USD", "EUR")).thenReturn(currencyResponse);
        when(transferMapper.toEntity(request)).thenReturn(mockTransfer);
        when(transferDao.save(any(Transfer.class))).thenReturn(mockTransfer);
        when(transferMapper.toResponse(mockTransfer)).thenReturn(any());

        assertDoesNotThrow(() -> transferService.createTransfer(request));

        verify(accountService, times(2)).updateBalance(anyLong(), any());
        verify(rabbitTemplate, atLeastOnce()).convertAndSend(anyString(), anyString());
    }
}


