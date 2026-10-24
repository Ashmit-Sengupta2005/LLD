package Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Domain.Receipt;

public class ReceiptRepository {
    private final Map<UUID, Receipt> ReceiptRepositoryMap = new HashMap<>();

    public void saveReceipt(Receipt receipt) {
        ReceiptRepositoryMap.put(receipt.getReceiptId(), receipt);
    }

    public void removeReceipt(Receipt receipt) {
        ReceiptRepositoryMap.remove(receipt.getReceiptId());
    }

    public Receipt viewReceipt(UUID ReceiptID) {
        return ReceiptRepositoryMap.get(ReceiptID);
    }
}
