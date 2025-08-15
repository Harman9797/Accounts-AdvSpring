package ca.dhali.learn.accounts.dto;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AccountsDTO {

    private long accountNumber;

    private String accountType;

    private String branchAddress;
}
