package org.unitech.msaccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.unitech.msaccount.domain.entity.Account;
import org.unitech.msaccount.domain.repo.AccountRepository;
import org.unitech.msaccount.exception.InsufficientFundsException;
import org.unitech.msaccount.exception.InvalidPinException;
import org.unitech.msaccount.exception.NotFoundException;
import org.unitech.msaccount.mapper.AccountMapper;
import org.unitech.msaccount.model.dto.AccountDto;
import org.unitech.msaccount.model.dto.request.CreateAccountRequest;
import org.unitech.msaccount.model.dto.request.DepositRequest;
import org.unitech.msaccount.model.dto.request.UpdatePinRequest;
import org.unitech.msaccount.model.dto.response.AccountResponse;
import org.unitech.msaccount.model.enums.AccountStatus;
import org.unitech.msaccount.model.enums.Currency;
import org.unitech.msaccount.service.AccountService;
import org.unitech.msaccount.service.CartGenerationService;
import org.unitech.msaccount.service.UserService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private UserService userService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private CartGenerationService cartGenerationService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAccount_Success() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(1L);
        request.setCurrency(Currency.USD);

        AccountDto dto = new AccountDto();
        dto.setCartNumber("12345678");
        dto.setCurrency(Currency.USD);
        dto.setBalance(BigDecimal.ZERO);

        Account account = new Account();
        account.setId(1L);
        account.setCartNumber("12345678");
        account.setUserId(1L);
        account.setCurrency(Currency.USD);

        when(cartGenerationService.generateCart(Currency.USD)).thenReturn(dto);
        when(accountMapper.toEntity(dto)).thenReturn(account);
        when(accountRepository.existsByCartNumber(anyString())).thenReturn(false);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toResponse(account)).thenReturn(new AccountResponse());

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("account.events"), anyString());
    }

    @Test
    public void testBlockAccount_Success() {
        Account account = new Account();
        account.setId(1L);
        account.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.blockAccount(1L);

        assertEquals(AccountStatus.BLOCKED, account.getStatus());
        verify(accountRepository).save(account);
    }

    @Test
    public void testUpdatePin_InvalidOldPin() {
        Account account = new Account();
        account.setId(1L);
        account.setPin("1234");

        UpdatePinRequest request = new UpdatePinRequest();
        request.setOldPin("0000");
        request.setNewPin("4321");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InvalidPinException.class, () -> accountService.updatePin(1L, request));
    }

    @Test
    public void testGetUserAccounts_ReturnList() {
        Account account = new Account();
        account.setId(1L);
        when(accountRepository.findByUserId(1L)).thenReturn(Collections.singletonList(account));
        when(accountMapper.toResponseList(anyList())).thenReturn(Collections.singletonList(new AccountResponse()));

        assertEquals(1, accountService.getUserAccounts(1L).size());
    }

    @Test
    public void testDeposit_Success() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);

        DepositRequest request = new DepositRequest();
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toResponse(account)).thenReturn(new AccountResponse());

        AccountResponse response = accountService.deposit(1L, request);

        assertNotNull(response);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("account.events"), anyString());
    }

    @Test
    public void testUpdateBalance_InsufficientFunds() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.updateBalance(1L, BigDecimal.valueOf(-100)));
    }
}
