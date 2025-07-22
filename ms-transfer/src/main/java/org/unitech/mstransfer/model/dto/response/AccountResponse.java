package org.unitech.mstransfer.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long id;
    private Long userId;
    private String cartNumber;
    private BigDecimal balance;
    private String accountStatus;
    private String currency;
}