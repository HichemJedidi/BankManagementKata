package sgcbi.kata.bankManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sgcbi.kata.bankManagement.entities.Operation;

@Repository
public interface OperationRepository extends JpaRepository<Operation,Long> {
}
