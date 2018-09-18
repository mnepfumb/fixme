package za.co.wethinkcode.mnepfumb.fixme.core.entity;

import javax.persistence.*;

@Entity
public class Currency {
    @Id
    @Column(updatable = false, nullable = false, length = 100)
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;

    String name;

    double price;

    int quantity;

    public Currency(){

    }

    public Currency(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void setName(String name)
    {
        this.name = name;
    } 

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }

    public double getPrice()
    {
        return price;
    }

    public int getQuantity()
    {
        return quantity;
    }

}