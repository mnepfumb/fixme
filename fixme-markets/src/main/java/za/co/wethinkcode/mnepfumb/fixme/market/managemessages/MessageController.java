package za.co.wethinkcode.mnepfumb.fixme.market.managemessages;

public class MessageController
{
    //private static String[] currencies = { "USD", "GBP", "EUR"};

    public static String Controller( String msg )
    {
        String[] parts = msg.trim().split("\\|");
        int i = parts.length;
        int j = 0;



        while ( j < i )
        {
            System.out.println(parts[j]);
            j++;
        }
        //check the market
        System.out.println(parts[3]);

        if ( MarketStorage.checkingCurrency(parts[3]) )
        {
            if (MarketStorage.checkingPrice(parts[4], parts[3], parts[1]))
            {
                 //check if it buy or sell
                if (parts[1].trim().equalsIgnoreCase("buy"))
                {
                    if (MarketStorage.checkingQTY(parts[2], parts[3]))
                    {
                        MarketStorage.processBuy(parts[0], parts[1].trim(), parts[2], parts[3], parts[4]);
                        return "Accepted: BUY transaction processed successfully";
                    }
                    else
                    {
                        return "Rejected: Quantity is more than Whats Available";
                    }
                }
                else if (parts[1].trim().equalsIgnoreCase("sell"))
                {
                    MarketStorage.processSell(parts[0], parts[1].trim(), parts[2], parts[3], parts[4]);
                    return "Accepted: SELL transaction processed successfully";
                }
                else
                {
                    return "Rejected: Transaction was unsuccessful";
                }

            }
            else
            {
                return "Rejected: The price is less than market value";
            }
        }
        else 
        {
            return "Rejected: Currency not found.";
        }
    }
}