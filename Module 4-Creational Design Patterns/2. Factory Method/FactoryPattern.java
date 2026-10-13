interface Logistics {
    void send();
}

class Road implements Logistics {
    @Override
    public void send() {
        System.out.println("Road Logic here \n");
    }
}

class Air implements Logistics {
    @Override
    public void send() {
        System.out.println("Air logic here \n");
    }
}

// Main Factory class producing objects
class LogisticFactory {
    public static Logistics getLogistics(String mode) {
        if (mode.equalsIgnoreCase("Air")) {
            return new Air();
        } else if (mode.equalsIgnoreCase("Road")) {
            return new Road();
        }
        throw new IllegalArgumentException("Unknown logistics mode: " + mode);
    }
}

// Service class implementing factory services

class LogisticService {
    public void send(String mode) {
        Logistics logistics = LogisticFactory.getLogistics(mode);
        logistics.send(); // businss logic here
    }
}

class Main {
    public static void main(String[] args) {
        LogisticService service = new LogisticService();
        service.send("Air");
        service.send("Road");
    }
}