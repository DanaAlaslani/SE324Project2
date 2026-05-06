package payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final List<Payment> payments = new ArrayList<>();
    private final PaymentGateway gateway;
    private int nextId = 1;

    public PaymentService() {
        this.gateway = new MockPaymentGateway();
    }

    public PaymentService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public Payment makePayment(int traineeId, int trainerId, String paymentType, double amount)
            throws PaymentException {
        if (amount <= 0)
            throw new PaymentException("Amount must be greater than 0.");

        Payment payment = new Payment(traineeId, trainerId, paymentType, amount);
        payment.setPaymentId(nextId++);

        String ref = gateway.processPayment(payment);
        if (ref != null) {
            payment.setStatus("COMPLETED");
            payment.setTransactionRef(ref);
            gateway.sendReceipt(payment);
        } else {
            payment.setStatus("FAILED");
            payments.add(payment);
            throw new PaymentException("Payment was declined by the gateway.");
        }
        payments.add(payment);
        return payment;
    }

    public List<Payment> getPaymentsForTrainee(int traineeId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments)
            if (p.getTraineeId() == traineeId) result.add(p);
        return result;
    }

    public List<Payment> getPaymentsForTrainer(int trainerId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments)
            if (p.getTrainerId() == trainerId) result.add(p);
        return result;
    }

    public List<Payment> getAllPayments() { return payments; }

    public double getTotalRevenue() {
        double total = 0;
        for (Payment p : payments)
            if ("COMPLETED".equals(p.getStatus())) total += p.getAmount();
        return total;
    }

    public void printSummary() {
        double revenue = getTotalRevenue();
        System.out.println("\n=== Financial Summary ===");
        System.out.printf("Total Revenue    : %.2f SAR%n", revenue);
        System.out.printf("Platform Fees 5%% : %.2f SAR%n", revenue * 0.05);
        System.out.printf("Paid to Trainers : %.2f SAR%n", revenue * 0.95);
    }
}
