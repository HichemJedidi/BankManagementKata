package sgcbi.kata.bankManagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import sgcbi.kata.bankManagement.ObjectMapper.ObjectMapper;
import sgcbi.kata.bankManagement.dtos.BankAccountDTO;
import sgcbi.kata.bankManagement.dtos.OperationDTO;
import sgcbi.kata.bankManagement.entities.BankAccount;
import sgcbi.kata.bankManagement.enums.OperationType;
import sgcbi.kata.bankManagement.exceptions.AmountNegativeException;
import sgcbi.kata.bankManagement.exceptions.BalanceNotSufficientException;
import sgcbi.kata.bankManagement.exceptions.NoSuchAccountException;
import sgcbi.kata.bankManagement.repositories.BankAccountRepository;
import sgcbi.kata.bankManagement.repositories.OperationRepository;
import sgcbi.kata.bankManagement.service.OperationService;


import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private OperationRepository operationRepository;



    @Spy
    @InjectMocks
    private OperationService operationService;

    private BankAccount account ;

    private static Stream<Arguments> provideParametersForNominalOperations() {
        BankAccountDTO accountDTO = new BankAccountDTO();
        accountDTO.setBalance(5000);
        accountDTO.setId(123L);
        OperationDTO withdrawOp  = OperationDTO.builder().
                date(Instant.now()).amount(1000).type(OperationType.DEPOSIT).build();
        OperationDTO depositOp  = OperationDTO.builder().
                date(Instant.now()).amount(1000).type(OperationType.DEPOSIT).build();
        return Stream.of(
                Arguments.of(depositOp,6000d),
                Arguments.of(withdrawOp,6000d)
        );
    }

    @BeforeEach
    public void setUp(){
        account = new BankAccount();
        account.setBalance(5000);
        account.setId(123L);
    }

    @ParameterizedTest
    @MethodSource("provideParametersForNominalOperations")
    public void shouldPreformNominalOperations(OperationDTO operationDto,Double expectedBalance) throws NoSuchAccountException {

        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(ObjectMapper.map(account, BankAccount.class)));
        BankAccountDTO postOperationBankAccount = new BankAccountDTO();
       if(operationDto.getType().equals(OperationType.DEPOSIT))
         postOperationBankAccount = operationService.deposit(account.getId(),operationDto.getAmount());
       else
           postOperationBankAccount = operationService.withdraw(account.getId(),operationDto.getAmount());
        assertThat(postOperationBankAccount.getBalance()).isEqualTo(expectedBalance);
        //account for test are without operations, having 1 operation on the list after adding deposit or withdraw operation assertion
        assertThat(postOperationBankAccount.getOperations().size()).isEqualTo(1);

    }


    @Test
    public void testDoWithdrawInsufficientBalance() throws BalanceNotSufficientException {
        when(bankAccountRepository.findById(123L)).thenReturn(Optional.of(account))
        ;
        assertThrows(BalanceNotSufficientException.class, () -> operationService.withdraw(123L, 500000));
    }
    @Test
    public void testNegativeAmountDoDeposit() throws AmountNegativeException {

        when(bankAccountRepository.findById(123L)).thenReturn(Optional.of(account));
        assertThrows(AmountNegativeException.class, () -> operationService.deposit(123L, -100));
    }

    @Test
    public void withdraw_should_throw_NoSuchAccountException() throws NoSuchAccountException {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchAccountException.class, () -> {
            operationService.withdraw(123L,0);
        });
    }

    @Test
    public void doDeposit_should_throw_NoSuchAccountException() throws NoSuchAccountException {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchAccountException.class, () -> {
            operationService.deposit(123L,1200);
        });
    }
}
