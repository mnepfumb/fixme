package za.co.wethinkcode.mnepfumb.fixme.core.entity;

import javax.persistence.*;

@Entity
public class SellList {
    @Id
    @Column(updatable = false, nullable = false, length = 100)
    @GeneratedValue(strategy = GenerationType.AUTO)
    long Id;

    String userId;
    String name;
    double price;
    int quantity;

    public SellList(){}

    public SellList(String userId, String name, double price, int quantity)
    {
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}