package org.unitech.mstransfer.controllertest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.unitech.mstransfer.controller.TransferController;
import org.unitech.mstransfer.model.dto.request.TransferRequest;
import org.unitech.mstransfer.model.dto.response.TransferResponse;
import org.unitech.mstransfer.service.TransferService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransfer_shouldReturnTransferResponse() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(BigDecimal.valueOf(100));
        request.setDescription("Test Transfer");

        TransferResponse response = new TransferResponse();
        response.setId(1L);
        response.setFromAccountId(1L);
        response.setToAccountId(2L);
        response.setAmount(BigDecimal.valueOf(100));

        when(transferService.createTransfer(request)).thenReturn(response);

        ResponseEntity<TransferResponse> result = transferController.createTransfer(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1L, result.getBody().getId());
    }

    @Test
    void getTransferById_shouldReturnTransferResponse() {
        TransferResponse response = new TransferResponse();
        response.setId(1L);

        when(transferService.getTransferById(1L)).thenReturn(response);

        ResponseEntity<TransferResponse> result = transferController.getTransferById(1L);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1L, result.getBody().getId());
    }
    @Test
    void getTransfersByAccount_shouldReturnListOfTransfers() {
        TransferResponse transfer1 = new TransferResponse();
        TransferResponse transfer2 = new TransferResponse();
        when(transferService.getTransfersByAccount(1L)).thenReturn(Arrays.asList(transfer1, transfer2));

        ResponseEntity<List<TransferResponse>> result = transferController.getTransfersByAccount(1L);

        assertEquals(2, result.getBody().size());
    }

    @Test
    void getRecentTransfersByUser_shouldReturnLimitedTransfers() {
        TransferResponse transfer1 = new TransferResponse();
        TransferResponse transfer2 = new TransferResponse();
        List<TransferResponse> responseList = Arrays.asList(transfer1, transfer2);

        when(transferService.getRecentTransfersByUser(1L, 2)).thenReturn(responseList);

        ResponseEntity<List<TransferResponse>> result = transferController.getRecentTransfersByUser(1L, 2);

        assertEquals(2, result.getBody().size());
        verify(transferService).getRecentTransfersByUser(1L, 2);
    }
}



