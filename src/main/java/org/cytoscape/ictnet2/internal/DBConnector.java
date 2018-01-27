package org.cytoscape.ictnet2.internal;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnector {
	private static boolean conOK = false;
	private static String CON_url;
	//user name for remote MySQL database;
	private static String CON_NAME;
	//password for user
	private static String CON_PWD;
    private static String CON_DATABASE;
	
	private Connection connect = null;	
	
	public DBConnector(){
		init();
	}
	
	public boolean getConOK(){
		return conOK;
	}	
	
	private void init(){				
		try{
			Class.forName("com.mysql.jdbc.Driver");			
			connect = DriverManager.getConnection(CON_url + CON_DATABASE, CON_NAME, CON_PWD);			
			conOK = true;	
			System.out.println("iCTNet App: Connection to mySQL database is OK!");
		}catch(ClassNotFoundException ex){
			System.out.println("iCTNet App: com.mysql.jdbc.Driver class not found!");			
			conOK = false;
		}catch(Exception ex){	
			System.out.println(ex.toString());
			conOK = false;
		}//try-catch		
	}//
	
	public Connection getConnection(){		
		try{
			if (connect == null){
				init();				
			}else if (connect.isClosed()){
				Class.forName("com.mysql.jdbc.Driver");			
				connect = DriverManager.getConnection(CON_url + CON_DATABASE, CON_NAME, CON_PWD);				
				conOK = true;
			}//if-else	
			return connect;
		}catch(ClassNotFoundException ex){
				System.out.println("iCTNet App: com.mysql.jdbc.Driver class not found!");			
				conOK = false;
				return null;
		}catch(Exception ex){	
				System.out.println(ex.toString());
				conOK = false;
				return null;
		}//try-catch		
	}
	
	public void shutDown(){
		if (connect != null){
			try{
			    if (!connect.isClosed()){
					connect.close();				
			    }//if
			}catch(Exception ex){
				System.out.println("Error to shut down the database connection: " + ex.toString());
			}//try-catch
		}//if		
	}//
}
