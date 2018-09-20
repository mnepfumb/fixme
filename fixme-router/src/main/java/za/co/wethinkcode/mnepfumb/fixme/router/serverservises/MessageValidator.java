package za.co.wethinkcode.mnepfumb.fixme.router.serverservises;

public class MessageValidator
{
    public static boolean checkFixMessages( String msg )
    {
        String[] parts = msg.trim().split( "\\|" );
        int i = 0;
        for ( String var: parts )
        {
            System.out.println( var + " " + i );
            i++;
        }

        if ( i < 4 )
            return ( false );

        if (i > 4)
            return ( false );

        if ( !checkBuySell( parts[0] ) )
            return ( false );

        if ( !CheckInteger( parts[1] ) )
            return ( false );
        
        if ( !CheckDouble( parts[3] ) )
            return ( false );
        return ( true );
    }

    private static  boolean checkBuySell( String str )
    {
        if ( str.trim().equals("buy") )
            return ( true);
        if ( str.trim().equals("sell") )
            return ( true );
        return  ( false );
    }
    private static boolean CheckInteger( String obj )
    {
        try 
        {
            Integer.parseInt( obj );
        } 
        catch ( NumberFormatException e )
        {
            return ( false );
        }
        return ( true );
    }

    private static boolean CheckDouble( String obj )
    {
        try 
        {
            Double.parseDouble( obj );
        } 
        catch ( NumberFormatException e ) 
        {
            return ( false );
        }
        return ( true );
    }
}