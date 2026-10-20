/*
 * COMPOSITE PATTERN
 *
 * Recognition signal: the data forms a TREE, and a "group" should behave
 * exactly like a "single item".
 *
 * Cart example: a cart holds Products. But it also holds Bundles. And a Bundle
 * can hold Products AND other Bundles (festival bundle -> iphone combo -> phone).
 *
 * Without the pattern, Product and ProductBundle are unrelated types, so the
 * client must branch on type everywhere:
 *     if (item is Product)  total += product.getPrice();
 *     else                  total += bundle.getPrice();
 * ...and a bundle can never contain another bundle.
 *
 * Composite fixes it with ONE idea:
 *   Leaf and Composite implement the SAME interface,
 *   and the Composite holds a List of that interface (not of Leaf).
 *
 * That single line - List<CartItem>, not List<Product> - is what makes the
 * structure recursive and makes nesting work for free.
 */

import java.util.ArrayList;
import java.util.List;

// ========== COMPONENT ==========
// The common interface. Client only ever talks to this.
interface CartItem {
    double getPrice();

    void display(String indent);
}

// ========== LEAF ==========
// An atomic item. No children. Just does its own job.
class Product implements CartItem {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price; // base case of the recursion
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Product: " + name + " - Rs." + price);
    }
}

// ========== COMPOSITE ==========
// Holds children and DELEGATES the work to them.
class ProductBundle implements CartItem {
    private String bundleName;

    // The key line: List<CartItem>, NOT List<Product>.
    // This is why a bundle can contain another bundle.
    private List<CartItem> items = new ArrayList<>();

    public ProductBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }

    @Override
    public double getPrice() {
        double total = 0;
        // Doesn't know or care whether a child is a Product or another Bundle
        for (CartItem item : items) {
            total += item.getPrice(); // recursive step
        }
        return total;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Bundle: " + bundleName);
        for (CartItem item : items) {
            item.display(indent + "   "); // deeper indent = deeper level
        }
    }
}

// ========== CLIENT ==========

public class Composite {
    public static void main(String[] args) {

        // Leaves
        CartItem book = new Product("Atomic Habits", 499);
        CartItem phone = new Product("iPhone 15", 79999);
        CartItem earbuds = new Product("AirPods", 15999);
        CartItem charger = new Product("20W Charger", 1999);

        // Composite containing only leaves
        ProductBundle iphoneCombo = new ProductBundle("iPhone Essentials Combo");
        iphoneCombo.addItem(phone);
        iphoneCombo.addItem(earbuds);
        iphoneCombo.addItem(charger);

        ProductBundle schoolKit = new ProductBundle("Back to School Kit");
        schoolKit.addItem(new Product("Notebook Pack", 249));
        schoolKit.addItem(new Product("Pen Set", 99));

        // Composite containing LEAVES *and* OTHER COMPOSITES - nesting is free
        ProductBundle megaBundle = new ProductBundle("Mega Festival Bundle");
        megaBundle.addItem(book);
        megaBundle.addItem(iphoneCombo);
        megaBundle.addItem(schoolKit);

        // Cart holds ONE type. No type checks anywhere.
        List<CartItem> cart = new ArrayList<>();
        cart.add(book);
        cart.add(iphoneCombo);
        cart.add(megaBundle);

        System.out.println("Your Cart:\n");
        double total = 0;

        // This loop is identical whether the item is 1 product or a 3-level tree
        for (CartItem item : cart) {
            item.display("   ");
            total += item.getPrice();
        }

        System.out.println("\nTotal: Rs." + total);
    }
}
