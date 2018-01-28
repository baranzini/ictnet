package org.cytoscape.ictnet2.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnector {
	private boolean conOK = false;
	private String CON_URL;
	//user name for remote MySQL database;
	private String CON_NAME;
	//password for user
	private String CON_PWD;
    private String CON_DATABASE;
	
	private Connection connect = null;	
	
	public DBConnector(){
		init();
	}
	
	public boolean getConOK(){
		return conOK;
	}	
	
	private void init(){				
        Properties prop = new Properties();
		try {
        	prop.load(getClass().getClassLoader().getResourceAsStream("credentials.properties"));
        } catch (IOException ex) {
			ex.printStackTrace();
		}
		CON_URL = prop.getProperty("CON_URL");
		CON_NAME = prop.getProperty("CON_NAME");
		CON_PWD = prop.getProperty("CON_PWD");
		CON_DATABASE = prop.getProperty("CON_DATABASE");
		try{
			Class.forName("com.mysql.jdbc.Driver");			
			connect = DriverManager.getConnection(CON_URL + CON_DATABASE, CON_NAME, CON_PWD);			
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
				connect = DriverManager.getConnection(CON_URL + CON_DATABASE, CON_NAME, CON_PWD);				
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
