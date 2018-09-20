package za.co.wethinkcode.mnepfumb.fixme.market.managemessages;

import org.hibernate.Session;
import org.hibernate.Transaction;
import za.co.wethinkcode.mnepfumb.fixme.core.entity.BuyList;
import za.co.wethinkcode.mnepfumb.fixme.core.entity.SellList;
import za.co.wethinkcode.mnepfumb.fixme.market.database.HibernateUtil;
import za.co.wethinkcode.mnepfumb.fixme.core.entity.Currency;

import java.util.List;

public class MarketStorage
{

    public static  boolean checkingCurrency( String currencyName )
    {
        List<Currency> currencies = getCurrencyList();
        for (Currency var : currencies)
            if ((var.getName()).equalsIgnoreCase(currencyName))
                return ( true );

        return ( false );
    }

    public static  boolean checkingPrice( String pricing, String currencyName, String category )
    {
        double price = Double.parseDouble( pricing );
        List<Currency> currencies = getCurrencyList();
        if ( category.trim().equalsIgnoreCase( "buy") )
        {
            for ( Currency var : currencies )
            {
                if ( var.getName().equals( currencyName ) )
                    if ( price >= var.getPrice() )
                        return ( true );
            }
        }
        else  if ( category.trim().equalsIgnoreCase( "sell" ) )
        {
            for ( Currency var : currencies )
            {
                if ( var.getName().equals( currencyName ) )
                    if ( price <= var.getPrice() )
                        return ( true );
            }
        }
        return ( false );
    }

    public static boolean checkingQTY( String quantity, String currencyName )
    {
        int qty = Integer.parseInt( quantity );
        List<Currency> currencies = getCurrencyList();
        for ( Currency var : currencies )
        {
            if ( var.getName().equals( currencyName ) )
                if ( qty <= var.getQuantity() )
                    return ( true );
        }
        return  ( false );
    }

    public static void processSell( String u_id, String category, String quantity, String currencyName, String pricing )
    {
        double price = Double.parseDouble( pricing );
        int qty = Integer.parseInt( quantity );
        //update Currency Table
        UpdateCurrency( category, currencyName, qty );
        //Add transaction to sell table
        SellList seller = new SellList( u_id, currencyName, price, qty );
        AddSellList( seller );
    }

    public static void processBuy( String u_id, String category, String quantity, String currencyName, String pricing )
    {
        double price = Double.parseDouble( pricing );
        int qty = Integer.parseInt( quantity );
        //update Currency Table
        UpdateCurrency( category, currencyName, qty );
        //Add transaction to buy table
        BuyList buyer = new BuyList( u_id, currencyName, price, qty );
        AddBuyList( buyer );
    }

    public static  List<Currency> getCurrencyList()
    {
        Session session = HibernateUtil.getInstance().session;
        List<Currency> currencies;

        Transaction transaction = session.beginTransaction();
        currencies = session.createCriteria( Currency.class ).list();
        transaction.commit();
        return  ( currencies );
    }

    public static void AddBuyList( BuyList buyer )
    {
        Session session = HibernateUtil.getInstance().session;
        Transaction transaction = session.beginTransaction();
        session.save( buyer );
        transaction.commit();
    }

    public static void AddSellList( SellList seller )
    {
        Session session = HibernateUtil.getInstance().session;
        Transaction transaction = session.beginTransaction();
        session.save( seller );
        transaction.commit();
    }

    public static void UpdateCurrency( String category, String currencyName, int qty )
    {
        Session session = HibernateUtil.getInstance().session;
        List<Currency> currencies = getCurrencyList();
        for ( Currency var: currencies )
        {
            if ( var.getName().equalsIgnoreCase( currencyName ) )
            {
                if ( category.equalsIgnoreCase( "buy" ) )
                {
                    Transaction transaction = session.beginTransaction();
                    var.setQuantity( var.getQuantity() - qty );
                    session.update( var );
                    transaction.commit();
                }
                else if ( category.equalsIgnoreCase( "sell" ) )
                {
                    Transaction transaction = session.beginTransaction();
                    var.setQuantity( var.getQuantity() + qty );
                    session.update( var );
                    transaction.commit();
                }
            }
        }
    }
}