package sgcbi.kata.bankManagement.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sgcbi.kata.bankManagement.ObjectMapper.ObjectMapper;
import sgcbi.kata.bankManagement.dtos.BankAccountDTO;
import sgcbi.kata.bankManagement.dtos.OperationDTO;
import sgcbi.kata.bankManagement.entities.BankAccount;
import sgcbi.kata.bankManagement.entities.Operation;
import sgcbi.kata.bankManagement.enums.BankAccountStatus;
import sgcbi.kata.bankManagement.exceptions.NoSuchAccountException;
import sgcbi.kata.bankManagement.repositories.BankAccountRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class BankAccountService{

    private BankAccountRepository bankAccountRepository;

    public BankAccountDTO createAccount(BankAccountDTO accountDTO){
        BankAccount account = ObjectMapper.map(accountDTO, BankAccount.class);
        account.setStatus(BankAccountStatus.CREATED);
        return ObjectMapper.map(bankAccountRepository.save(account), BankAccountDTO.class);

    }

    public List<BankAccountDTO> getAllAccounts(){
        List<BankAccount> accounts = bankAccountRepository.findAll();
        List<BankAccountDTO> accountDTOs = new ArrayList<>();
        for (BankAccount account : accounts) {
            BankAccountDTO accountDTO = ObjectMapper.map(account, BankAccountDTO.class);
            accountDTOs.add(accountDTO);
        }

        return accountDTOs;
    }

    public void activateAccount(Long accountId) throws NoSuchAccountException {
        Optional<BankAccount> optionalAccount = bankAccountRepository.findById(accountId);

        if (!optionalAccount.isPresent()) {
            throw new NoSuchAccountException("no bank account with the id: " + accountId);
        }

        BankAccount account = optionalAccount.get();
        account.setStatus(BankAccountStatus.ACTIVATED);

        bankAccountRepository.save(account);
    }

    public BankAccountDTO printStatement(Long accountId) throws NoSuchAccountException {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(accountId);
        if(!optionalBankAccount.isPresent()){
            throw new NoSuchAccountException(": "+accountId);
        }
        return ObjectMapper.map(optionalBankAccount.get(),BankAccountDTO.class);
    }

    public List<OperationDTO> listAllAccountOperations(Long accountId) throws NoSuchAccountException {
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(accountId);
        if(!optionalBankAccount.isPresent()){
            throw new NoSuchAccountException(": "+accountId);
        }
        List<OperationDTO> operationDTOs =  new ArrayList<>();

        for (Operation operation : optionalBankAccount.get().getOperations()) {
            OperationDTO operationDTO = ObjectMapper.map(operation, OperationDTO.class);
            operationDTOs.add(operationDTO);
        }

        return operationDTOs;

    }

}
