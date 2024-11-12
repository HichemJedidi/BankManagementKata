package sgcbi.kata.bankManagement;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sgcbi.kata.bankManagement.ObjectMapper.ObjectMapper;
import sgcbi.kata.bankManagement.dtos.BankAccountDTO;
import sgcbi.kata.bankManagement.dtos.OperationDTO;
import sgcbi.kata.bankManagement.entities.BankAccount;
import sgcbi.kata.bankManagement.entities.Operation;
import sgcbi.kata.bankManagement.enums.BankAccountStatus;
import sgcbi.kata.bankManagement.enums.OperationType;
import sgcbi.kata.bankManagement.exceptions.NoSuchAccountException;
import sgcbi.kata.bankManagement.repositories.BankAccountRepository;
import sgcbi.kata.bankManagement.service.BankAccountService;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {
    @InjectMocks()
    private BankAccountService bankAccountService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    private List<Operation> operations;
    private BankAccount account ;
    @BeforeEach
    public  void setUp(){

        account = BankAccount.builder()
                .id(12343L)
                .balance(1000)
                .currency("EUR")
                .status(BankAccountStatus.CREATED)
                .operations(null).build();

        operations  = new ArrayList<>();
        operations.add(new Operation(12L, Instant.now(),10000, OperationType.DEPOSIT,account));
        account.setOperations(operations);
    }
    @Test
    public void TestAccountCreation(){
        when(bankAccountRepository.save(any())).thenReturn(account);
        assertNotNull(bankAccountService.createAccount(ObjectMapper.map(account, BankAccountDTO.class)));
        assertEquals(12343L, (bankAccountService.createAccount(new BankAccountDTO())).getId());

    }
    @Test
    public void printStatement_should_successfully_return_current_account_balance() throws NoSuchAccountException {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        BankAccountDTO accountDto = bankAccountService.printStatement(12343L);
        assertThat(accountDto.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    public void listAllOperations_should_successfully_return_all_account_operations() throws NoSuchAccountException {
        when(bankAccountRepository.findById(12343L)).thenReturn(Optional.of(account));
        List<OperationDTO> operations = bankAccountService.listAllAccountOperations(12343L);
        assertThat(operations).isNotEmpty();
        assertEquals(operations.size(), account.getOperations().size());
    }


    @Test
    public void printStatement_should_throw_exception_for_no_such_account() throws NoSuchAccountException {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

       assertThrows(NoSuchAccountException.class, () -> {
            bankAccountService.printStatement(12343L);
        });

    }

    @Test()
    public void listAllOperations_should_throw_exception_for_no_such_account() throws NoSuchAccountException {
        when(bankAccountRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchAccountException.class, () -> {
            bankAccountService.listAllAccountOperations(12343L);
        });
    }
}
