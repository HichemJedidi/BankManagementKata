package sgcbi.kata.bankManagement.exceptions;

public class BalanceNotSufficientException extends RuntimeException {

    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
