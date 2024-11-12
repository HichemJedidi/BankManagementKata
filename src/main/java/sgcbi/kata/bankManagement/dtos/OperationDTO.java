package sgcbi.kata.bankManagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sgcbi.kata.bankManagement.enums.OperationType;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationDTO {
    private Long id;
    private Instant date;
    private double amount;
    private OperationType type;

}
