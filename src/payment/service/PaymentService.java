package payment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ============================================
// PAYMENT CLASS
// ============================================
 class Payment {
    private int paymentId;
    private int traineeId;
    private int trainerId;
    private String paymentType;
    private double amount;
    private String status;
    private String transactionRef;

    public Payment() {
        this.status = "PENDING";
    }

    public Payment(int traineeId, int trainerId, String paymentType, double amount) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.status = "PENDING";
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getTraineeId() { return traineeId; }
    public void setTraineeId(int traineeId) { this.traineeId = traineeId; }

    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    
    public double getPlatformFee() {
        return amount * 0.05;
    }
    
    public double getTrainerAmount() {
        return amount - getPlatformFee();
    }
}

// ============================================
// PAYMENT EXCEPTION CLASS
// ============================================
class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }
}

// ============================================
// PAYMENT GATEWAY INTERFACE
// ============================================
interface PaymentGateway {
    String processPayment(Payment payment);
    void sendReceipt(Payment payment);
}

// ============================================
// MOCK PAYMENT GATEWAY
// ============================================
class MockPaymentGateway implements PaymentGateway {
    @Override
    public String processPayment(Payment payment) {
        System.out.println("Processing payment of " + payment.getAmount() + " SAR...");
        
        boolean success = Math.random() < 0.90;
        
        if (success) {
            String ref = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Payment approved! Ref: " + ref);
            return ref;
        } else {
            System.out.println("Payment declined.");
            return null;
        }
    }

    @Override
    public void sendReceipt(Payment payment) {
        System.out.println("Receipt sent for payment #" + payment.getPaymentId() +
                           " - Amount: " + payment.getAmount() + " SAR");
    }
}

// ============================================
// PAYMENT SERVICE
// ============================================
public class PaymentService {
    private List<Payment> payments = new ArrayList<>();
    private PaymentGateway gateway;
    private int nextId = 1;

    public PaymentService() {
        this.gateway = new MockPaymentGateway();
    }
    
    public PaymentService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public Payment makePayment(int traineeId, int trainerId, String paymentType, double amount) 
            throws PaymentException {
        
        if (amount <= 0) {
            throw new PaymentException("Amount must be greater than 0.");
        }
        
        Payment payment = new Payment(traineeId, trainerId, paymentType, amount);
        payment.setPaymentId(nextId++);
        
        String ref = gateway.processPayment(payment);
        
        if (ref != null) {
            payment.setStatus("COMPLETED");
            payment.setTransactionRef(ref);
            payments.add(payment);
            gateway.sendReceipt(payment);
        } else {
            payment.setStatus("FAILED");
            payments.add(payment);
            throw new PaymentException("Payment was declined.");
        }
        
        return payment;
    }
    
    public List<Payment> getTraineePayments(int traineeId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getTraineeId() == traineeId) {
                result.add(p);
            }
        }
        return result;
    }
    
    public List<Payment> getTrainerPayments(int trainerId) {
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getTrainerId() == trainerId) {
                result.add(p);
            }
        }
        return result;
    }
    
    public List<Payment> getAllPayments() {
        return payments;
    }
    
    public double getTotalRevenue() {
        double total = 0;
        for (Payment p : payments) {
            if (p.getStatus().equals("COMPLETED")) {
                total += p.getAmount();
            }
        }
        return total;
    }
    
    // ============================================
    // MAIN METHOD FOR TESTING
    // ============================================
    public static void main(String[] args) {
        try {
            PaymentService ps = new PaymentService();
            
            System.out.println("=== GYMANICE PAYMENT SYSTEM ===\n");
            
            // Make payments
            Payment p1 = ps.makePayment(101, 201, "MADA", 299.99);
            System.out.println("✓ Payment 1: " + p1.getStatus() + " - " + p1.getTransactionRef());
            System.out.println("  Platform Fee (5%): " + p1.getPlatformFee() + " SAR");
            System.out.println("  Trainer gets: " + p1.getTrainerAmount() + " SAR\n");
            
            Payment p2 = ps.makePayment(102, 201, "APPLE_PAY", 159.50);
            System.out.println("✓ Payment 2: " + p2.getStatus() + " - " + p2.getTransactionRef());
            System.out.println("  Platform Fee (5%): " + p2.getPlatformFee() + " SAR");
            System.out.println("  Trainer gets: " + p2.getTrainerAmount() + " SAR\n");
            
            Payment p3 = ps.makePayment(103, 202, "CREDIT_CARD", 499.00);
            System.out.println("✓ Payment 3: " + p3.getStatus() + " - " + p3.getTransactionRef() + "\n");
            
            // View payments
            System.out.println("=== ALL PAYMENTS ===");
            for (Payment p : ps.getAllPayments()) {
                System.out.println("  ID: " + p.getPaymentId() + " | Trainee: " + p.getTraineeId() + 
                                   " | Amount: " + p.getAmount() + " SAR | Status: " + p.getStatus());
            }
            
            System.out.println("\n=== TRAINER 201 PAYMENTS ===");
            for (Payment p : ps.getTrainerPayments(201)) {
                System.out.println("  Trainee " + p.getTraineeId() + " paid " + p.getAmount() + " SAR");
                System.out.println("    Trainer earns: " + p.getTrainerAmount() + " SAR");
            }
            
            System.out.println("\n=== FINANCIAL SUMMARY ===");
            System.out.println("  Total Revenue: " + ps.getTotalRevenue() + " SAR");
            System.out.println("  Platform Fees (5%): " + (ps.getTotalRevenue() * 0.05) + " SAR");
            System.out.println("  Paid to Trainers: " + (ps.getTotalRevenue() * 0.95) + " SAR");
            
        } catch (PaymentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}