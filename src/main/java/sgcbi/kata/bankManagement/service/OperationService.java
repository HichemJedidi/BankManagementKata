package sgcbi.kata.bankManagement.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sgcbi.kata.bankManagement.ObjectMapper.ObjectMapper;
import sgcbi.kata.bankManagement.dtos.BankAccountDTO;
import sgcbi.kata.bankManagement.dtos.OperationDTO;
import sgcbi.kata.bankManagement.entities.BankAccount;
import sgcbi.kata.bankManagement.entities.Operation;
import sgcbi.kata.bankManagement.enums.OperationType;
import sgcbi.kata.bankManagement.exceptions.AmountNegativeException;
import sgcbi.kata.bankManagement.exceptions.BalanceNotSufficientException;
import sgcbi.kata.bankManagement.exceptions.NoSuchAccountException;
import sgcbi.kata.bankManagement.repositories.BankAccountRepository;
import sgcbi.kata.bankManagement.repositories.OperationRepository;


import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Transactional
@AllArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ReentrantLock lock = new ReentrantLock();

    public BankAccountDTO deposit(Long accountId, double amount) throws NoSuchAccountException {
        return performOperation(accountId, amount, OperationType.DEPOSIT);
    }

    public BankAccountDTO withdraw(Long accountId, double amount) throws NoSuchAccountException {
        return performOperation(accountId, amount, OperationType.WITHDRAW);
    }

    private BankAccountDTO performOperation(Long accountId, double amount, OperationType type) throws NoSuchAccountException {
        lock.lock();
        try {
            BankAccount bankAccount = findAccount(accountId);
            OperationDTO operationDTO = createOperation(bankAccount, amount, type);
            if (bankAccount.getOperations() == null) {
                bankAccount.setOperations(new ArrayList<>());
            }
            bankAccount.getOperations().add(ObjectMapper.map(operationDTO, Operation.class));
            return ObjectMapper.map(bankAccount, BankAccountDTO.class);
        } finally {
            lock.unlock();
        }
    }

    private BankAccount findAccount(Long accountId) throws NoSuchAccountException {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchAccountException("Account not found: " + accountId));
    }

    private OperationDTO createOperation(BankAccount account, double amount, OperationType type) {
        validateAmount(amount, type, account.getBalance());
        double newBalance = type == OperationType.WITHDRAW ? account.getBalance() - amount : account.getBalance() + amount;
        account.setBalance(newBalance);

        Operation operation = new Operation();
        operation.setAmount(amount);
        operation.setType(type);
        operation.setDate(Instant.now());
        operation.setAccount(account);
        operationRepository.save(operation);

        return ObjectMapper.map(operation, OperationDTO.class);
    }

    private void validateAmount(double amount, OperationType type, double balance) {
        if (amount < 0) throw new AmountNegativeException("Amount must not be negative.");
        if (type == OperationType.WITHDRAW && balance < amount)
            throw new BalanceNotSufficientException("Insufficient balance: " + balance);
    }
}
