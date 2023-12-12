// exception/InvalidPaymentMethodException.java
package exception;

public class InvalidPaymentMethodException extends Exception {
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}