package payment;

// Inheritance: CashPayment IS-A Payment
public class CashPayment extends Payment {

    public CashPayment() {
        super("Cash");               // super keyword: calls Payment constructor
    }

    @Override                        // Method overriding
    public double getFinalAmount(double baseAmount) {
        return baseAmount;           // no extra charge for cash
    }

    @Override
    public String pay(double amount) {
        return "Please pay Rs. " + String.format("%.2f", amount) + " in cash at the counter.";
    }
}
