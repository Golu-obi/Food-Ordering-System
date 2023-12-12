// model/Order.java
package model;

public class Order {
    private FoodItem item;
    private int quantity;

    public Order(FoodItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public FoodItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }


}
