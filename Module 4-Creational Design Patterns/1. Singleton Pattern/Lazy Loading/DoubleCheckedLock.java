public class DoubleCheckedLock {
    private static volatile DoubleCheckedLock instance;

    private DoubleCheckedLock() {
    }

    public static DoubleCheckedLock getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedLock.class) {
                if (instance == null) {
                    instance = new DoubleCheckedLock();
                }
            }
        }
        return instance;
    }
}
