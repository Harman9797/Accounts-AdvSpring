package ca.dhali.learn.accounts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Setter @Getter @ToString @NoArgsConstructor @AllArgsConstructor
public class Accounts extends BaseEntity {

    private long customerId;

    @Id
    private long accountNumber;

    private String accountType;

    private String branchAddress;
}
