
/*
 * Basically concept is suppose you have few pizza bases like A,B,C etc as
 * classes
 * and some ingredients like x,y,z
 * 
 * Now the thing is it would be a bad idea to create a subclass for every pizza
 * type
 * 
 * Like suppose main base classes are classA, class B and class C
 * and we dont follow the decorator pattern so our brute force would be creating
 * classes like
 * class A_withIngredients_x
 * class A_withIngredients_y
 * class A_withIngredients_z
 * class A_withIngredients_xz
 * class A_withIngredients_xy
 * class A_withIngredients_xyz
 * class A_withIngredients_xyz
 * Similarly for class B and C
 * As you see class explodes exponentially(n toppings requires 2^n classes ) which is not good -> design and maintenance problem
 * (this is compile time decision as you already decide the classes)
 * 
 * So we create a class for each ingredient class x, class y and class z
 * which are named decorator classes which accept pizza as an input and add
 * topings one by one using the classes (run time decision)
 * 
 */

interface Pizza {
    String getDescription();

    int getCost();
}

// Pizza base types - Plain and Thick
class PlainPizza implements Pizza {
    public String getDescription() {
        return "Plain Pizza ";
    }

    public int getCost() {
        return 200;
    }
}

class ThickPizza implements Pizza {
    public String getDescription() {
        return "Thick Pizza ";
    }

    public int getCost() {
        return 400;
    }
}

// Ingredients - Cheese, Olives, Stuffings (all are pizza decorators)

abstract class PizzaDecorator implements Pizza {
    Pizza basePizza;

    PizzaDecorator(Pizza pizza) {
        basePizza = pizza;
    }
}

class Cheese extends PizzaDecorator {

    Cheese(Pizza pizza) {
        super(pizza);
    }

    public String getDescription() {
        return this.basePizza.getDescription() + " + Cheese";
    }

    public int getCost() {
        return this.basePizza.getCost() + 40;
    }
}

class Olives extends PizzaDecorator {
    Olives(Pizza pizza) {
        super(pizza);
    }

    public String getDescription() {
        return this.basePizza.getDescription() + " + Olives";
    }

    public int getCost() {
        return this.basePizza.getCost() + 30;
    }
}

class Stuffings extends PizzaDecorator {
    Stuffings(Pizza pizza) {
        super(pizza);
    }

    public String getDescription() {
        return this.basePizza.getDescription() + " + Stuffings";
    }

    public int getCost() {
        return this.basePizza.getCost() + 80;
    }
}

public class Decorator {
    /*
     * Our goals:
     * 1. Plain Pizza + Cheese + Stuffings (pizza 1) cost: 320
     * 2. Cheese pizza + Cheese + Olives + Stuffings ( pizza 2) cost: 550
     */
    public static void main(String args[]) {
        Pizza pizza1 = new PlainPizza();
        pizza1 = new Cheese(pizza1);
        pizza1 = new Stuffings(pizza1);
        System.out.println("Pizza 1 \n");
        System.out.println(pizza1.getDescription() + "\n");
        System.out.println(pizza1.getCost() + "\n");
        Pizza pizza2 = new ThickPizza();
        pizza2 = new Cheese(pizza2);
        pizza2 = new Olives(pizza2);
        pizza2 = new Stuffings(pizza2);
        System.out.println("Pizza 2 \n");
        System.out.println(pizza2.getDescription() + "\n");
        System.out.println(pizza2.getCost() + "\n");
    }
}