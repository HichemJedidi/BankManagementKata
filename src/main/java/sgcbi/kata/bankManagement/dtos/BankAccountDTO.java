package sgcbi.kata.bankManagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgcbi.kata.bankManagement.enums.BankAccountStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDTO {
    private Long id;
    private double balance;
    private BankAccountStatus status;
    private String currency;
    private List<OperationDTO> operations;
}
