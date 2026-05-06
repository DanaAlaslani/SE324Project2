package payment;

public interface PaymentGateway {
    String processPayment(Payment payment);
    void sendReceipt(Payment payment);
}
