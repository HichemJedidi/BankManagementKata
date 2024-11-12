package sgcbi.kata.bankManagement.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgcbi.kata.bankManagement.enums.BankAccountStatus;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double balance;
    private BankAccountStatus status;
    private String currency;
    @OneToMany(mappedBy = "account")
    private List<Operation> operations;




}
