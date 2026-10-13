class BillPughSingleton {

    private static class Holder {
        private static final BillPughSingleton instance = new BillPughSingleton();
    }

    private BillPughSingleton() {
    }

    public static BillPughSingleton getInstance() {
        return Holder.instance;
    }
}
