package payment;

// Interface: only says WHAT a payable thing must do, not HOW.
public interface Payable {
    String pay(double amount);
}
