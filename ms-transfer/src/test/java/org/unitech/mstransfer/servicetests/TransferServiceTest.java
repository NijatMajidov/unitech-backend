package org.unitech.mstransfer.servicetests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.unitech.mstransfer.domain.entity.Transfer;
import org.unitech.mstransfer.domain.repository.TransferRepository;
import org.unitech.mstransfer.mapper.TransferMapper;
import org.unitech.mstransfer.model.dto.request.TransferRequest;
import org.unitech.mstransfer.model.dto.response.AccountResponse;
import org.unitech.mstransfer.model.dto.response.CurrencyResponse;
import org.unitech.mstransfer.model.dto.response.TransferResponse;
import org.unitech.mstransfer.service.AccountService;
import org.unitech.mstransfer.service.CurrencyService;
import org.unitech.mstransfer.service.TransferService;

import java.math.BigDecimal;

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
    private TransferRepository transferRepository;
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

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100"), "Borc");


        AccountResponse fromAccount = AccountResponse.builder()
                .id(1L)
                .userId(1L)
                .balance(new BigDecimal("500"))
                .accountStatus("ACTIVE")
                .build();

        AccountResponse toAccount = AccountResponse.builder()
                .id(2L)
                .userId(2L)
                .balance(new BigDecimal("200"))
                .accountStatus("ACTIVE")
                .build();

        CurrencyResponse currencyResponse = new CurrencyResponse("USD", "EUR", new BigDecimal("0.9"));

        Transfer mockTransfer = new Transfer();
        mockTransfer.setId(1L);
        mockTransfer.setFromAccountId(1L);
        mockTransfer.setToAccountId(2L);
        mockTransfer.setAmount(new BigDecimal("100"));
        mockTransfer.setDescription("Borc");

        TransferResponse expectedResponse = TransferResponse.builder()
                .id(1L)
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(new BigDecimal("100"))
                .description("Borc")
                .build();


        when(accountService.getAccountById(1L)).thenReturn(fromAccount);
        when(accountService.getAccountById(2L)).thenReturn(toAccount);
        when(currencyService.getExchangeRate("USD", "EUR")).thenReturn(currencyResponse);
        when(transferMapper.toEntity(request)).thenReturn(mockTransfer);
        when(transferRepository.save(any(Transfer.class))).thenReturn(mockTransfer);
        when(transferMapper.toResponse(mockTransfer)).thenReturn(expectedResponse);

        assertDoesNotThrow(() -> transferService.createTransfer(request));

        verify(accountService, times(2)).updateBalance(anyLong(), any());
        verify(rabbitTemplate, atLeastOnce()).convertAndSend(anyString(), anyString());
    }
}


