package sgcbi.kata.bankManagement.exceptions;

public class AmountNegativeException extends RuntimeException {

    public AmountNegativeException(String message) {
        super(message);
    }
}
