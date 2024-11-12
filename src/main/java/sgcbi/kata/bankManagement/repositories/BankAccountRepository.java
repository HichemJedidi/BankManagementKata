package sgcbi.kata.bankManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sgcbi.kata.bankManagement.entities.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
}
