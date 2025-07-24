package org.unitech.mstransfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.unitech.mstransfer.client.CurrencyClient;
import org.unitech.mstransfer.model.dto.response.CurrencyResponse;
import org.unitech.service.CurrencyService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CurrencyServiceTest {

    @Mock
    private CurrencyClient currencyClient;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getExchangeRate_shouldReturnCurrencyResponse() {
        CurrencyResponse mockResponse = new CurrencyResponse("USD", "EUR", new java.math.BigDecimal("0.85"));

        when(currencyClient.getExchangeRate("USD", "EUR")).thenReturn(mockResponse);

        org.unitech.dto.CurrencyResponse result = currencyService.getExchangeRate("USD", "EUR");

        assertNotNull(result);
        assertEquals("USD", result.getFrom());
        assertEquals("EUR", result.getTo());
        assertEquals("0.85", result.getRate().toString());
    }
}

