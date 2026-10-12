package payment;

// Abstraction: a general "Payment" cannot be created on its own.
// Child classes must say how they calculate the final amount.
public abstract class Payment implements Payable {
    protected String methodName;     // protected: visible to child classes

    public Payment(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    // Abstract method: every payment type must override it
    public abstract double getFinalAmount(double baseAmount);
}
