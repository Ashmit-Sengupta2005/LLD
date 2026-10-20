
class EagerLoading{
    private static final EagerLoading instance = new EagerLoading();
    private EagerLoading(){
        // private constructor which prevents creation of objects via new keyword
    }
    public static EagerLoading getInstance(){
        return instance; // Always returns the same instance
    }
}

class Main{
    public static void main(String args[]){
        EagerLoading object1= EagerLoading.getInstance();
        EagerLoading object2= EagerLoading.getInstance();
        System.out.println(object1);
        System.out.println(object2);
    }
}