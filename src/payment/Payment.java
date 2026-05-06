package payment;

public class Payment {
    private int paymentId;
    private int traineeId;
    private int trainerId;
    private String paymentType;
    private double amount;
    private String status;
    private String transactionRef;

    public Payment(int traineeId, int trainerId, String paymentType, double amount) {
        this.traineeId   = traineeId;
        this.trainerId   = trainerId;
        this.paymentType = paymentType;
        this.amount      = amount;
        this.status      = "PENDING";
    }

    public double getPlatformFee()   { return amount * 0.05; }
    public double getTrainerAmount() { return amount - getPlatformFee(); }

    public int    getPaymentId()     { return paymentId; }
    public void   setPaymentId(int id) { this.paymentId = id; }
    public int    getTraineeId()     { return traineeId; }
    public int    getTrainerId()     { return trainerId; }
    public String getPaymentType()   { return paymentType; }
    public double getAmount()        { return amount; }
    public String getStatus()        { return status; }
    public void   setStatus(String s){ this.status = s; }
    public String getTransactionRef()         { return transactionRef; }
    public void   setTransactionRef(String r) { this.transactionRef = r; }

    @Override
    public String toString() {
        return String.format("Payment #%d | %.2f SAR | %s | %s",
                paymentId, amount, paymentType, status);
    }
}
