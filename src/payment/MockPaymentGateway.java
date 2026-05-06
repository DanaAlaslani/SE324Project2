package payment;

import java.util.UUID;

class MockPaymentGateway implements PaymentGateway {

    @Override
    public String processPayment(Payment payment) {
        System.out.printf("Processing %.2f SAR via %s...%n",
                payment.getAmount(), payment.getPaymentType());
        boolean success = Math.random() < 0.90;
        if (success) {
            String ref = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Payment approved! Ref: " + ref);
            return ref;
        }
        System.out.println("Payment declined.");
        return null;
    }

    @Override
    public void sendReceipt(Payment payment) {
        System.out.printf("Receipt sent for Payment #%d | %.2f SAR%n",
                payment.getPaymentId(), payment.getAmount());
    }
}
