package DTO;

import Domain.Receipt;

public class ExitResult {
    private boolean success;
    private Receipt receipt;
    private String message;

    public ExitResult(boolean success, Receipt receipt, String message) {
        this.success = success;
        this.receipt = receipt;
        this.message = message;
    }

    public static ExitResult success(Receipt receipt) {
        return new ExitResult(true, receipt, "Payment done, slot freed. You may leave.");
    }

    public static ExitResult failure(String reason) {
        return new ExitResult(false, null, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public String getMessage() {
        return message;
    }
}
