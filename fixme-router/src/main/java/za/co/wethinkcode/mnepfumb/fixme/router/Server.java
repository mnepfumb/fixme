package za.co.wethinkcode.mnepfumb.fixme.router;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hibernate.Session;

import za.co.wethinkcode.mnepfumb.fixme.core.entity.Currency;
import za.co.wethinkcode.mnepfumb.fixme.router.database.HibernateUtil;
import org.hibernate.Transaction;

import za.co.wethinkcode.mnepfumb.fixme.router.serverservises.*;

public class Server 
{
	public static void main(String[] args)
	{
		ExecutorService executePortService = Executors.newCachedThreadPool();
		initDatabase();
		executePortService.submit(new RunServer(5000));
		executePortService.submit(new RunServer(5001));
		executePortService.shutdown();
	}

	public static void initDatabase()
	{
		ArrayList<Currency> currencies = getCurrencies();

		for (Currency currency : currencies) 
		{
			Session session = HibernateUtil.getInstance().session;
			Transaction transaction = session.beginTransaction();
			session.save(currency);
			transaction.commit();
		}

	}

	public static ArrayList<Currency> getCurrencies()
	{
		ArrayList<Currency> currencies = new ArrayList<>();

		currencies.add(new Currency("USD", 10.8, 10));
		currencies.add(new Currency("ZAR", 9.5, 78));
		currencies.add(new Currency("GDP", 4.7, 89));
		currencies.add(new Currency("EUR", 2.9, 100));
		return currencies;
	}

	
	
}