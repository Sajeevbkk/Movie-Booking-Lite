package payment;

public class CardPayment extends Payment {
    private static final double CARD_FEE_PERCENT = 2.0;

    public CardPayment() {
        super("Card");
    }

    @Override
    public double getFinalAmount(double baseAmount) {
        return baseAmount + (baseAmount * CARD_FEE_PERCENT / 100);   // 2% card fee
    }

    @Override
    public String pay(double amount) {
        return "Rs. " + String.format("%.2f", amount) + " charged to your card (includes 2% card fee).";
    }
}
