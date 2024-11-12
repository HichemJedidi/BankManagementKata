package sgcbi.kata.bankManagement.controllers;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sgcbi.kata.bankManagement.dtos.BankAccountDTO;
import sgcbi.kata.bankManagement.dtos.OperationDTO;
import sgcbi.kata.bankManagement.exceptions.NoSuchAccountException;
import sgcbi.kata.bankManagement.service.BankAccountService;
import sgcbi.kata.bankManagement.service.OperationService;


import java.util.List;

@RestController
@RequestMapping("/api/bankAccount")
@AllArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final OperationService operationService;
    @PostMapping("/createAccount")
    public ResponseEntity<BankAccountDTO> createAccount(@RequestBody BankAccountDTO BankAccountDTO) throws NoSuchAccountException {
        BankAccountDTO result = bankAccountService.createAccount(BankAccountDTO);
        bankAccountService.activateAccount(result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/{accountId}")
    public BankAccountDTO printAccountState(@PathVariable Long accountId) throws NoSuchAccountException {
        return bankAccountService.printStatement(accountId);
    }
    @GetMapping("/AllAccounts")
    public List<BankAccountDTO> getAllAccounts()   {
        return bankAccountService.getAllAccounts();
    }
    @GetMapping("{accountId}/history")
    public List<OperationDTO> showOperationsList(@PathVariable Long accountId) throws NoSuchAccountException {
        return bankAccountService.listAllAccountOperations(accountId);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exeptionHandler(Exception exeption){
        ResponseEntity<String> entity = new ResponseEntity<>(
                exeption.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return entity;
    }
    @PutMapping(path = "/deposit")
    public ResponseEntity<BankAccountDTO> depositMoney(@RequestParam Long accountId, @RequestParam double amount){

        try {
            BankAccountDTO updatedAccount = operationService.deposit(accountId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (NoSuchAccountException e) {
            return ResponseEntity.notFound().build();
        }

    }
    @PutMapping(path = "/withdraw")
    public ResponseEntity<BankAccountDTO> withdrawMoney(@RequestParam Long accountId, @RequestParam double amount){

        try {
            BankAccountDTO updatedAccount = operationService.withdraw(accountId, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (NoSuchAccountException e) {
            return ResponseEntity.notFound().build();
        }

    }
}
