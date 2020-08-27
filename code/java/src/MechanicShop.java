/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.io.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "password");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddCustomer(MechanicShop esql) throws SQLException {//1
		String id = null;
		String fname = null;
		String lname = null;
		String phone = null;
		String addy = null;

		System.out.println("Enter id:");
		try{
			id = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid id");
		}

		System.out.println("Enter first name:");
		try{
			fname = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid first name");
		}

		System.out.println("Enter last name:");
		try{
			lname = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid last name");
		}

		System.out.println("Enter phone:");
		try{
			phone = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid phone number");
		}

		System.out.println("Enter address:");
		try{
			addy = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid address");
		}

		try{
			esql.executeUpdate("INSERT INTO Customer (id, fname, lname, phone, address) VALUES("+
					id + ",'" +
					fname + "', '" +
					lname + "', '" +
					phone + "','" +
					addy + "');"
			);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public static void AddMechanic(MechanicShop esql){//2
		// TODO: take input
		// TODO: check validity of input
		String query = ""; //
		esql.executeQueryAndPrintResult(query);
	}
	
	public static void AddCar(MechanicShop esql){//3
		String vin = null;
		String make = null;
		String model = null;
		String year = null;

		System.out.println("Enter vin:");
		try{
			vin = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid vin");
		}

		System.out.println("Enter make:");
		try{
			make = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid make");
		}

		System.out.println("Enter model:");
		try{
			model = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid model");
		}

		System.out.println("Enter year:");
		try{
			year = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid year");
		}

		try{
			esql.executeUpdate("INSERT INTO Car VALUES("+ "'"
					+ vin + "'" + ",'"
					+ make + "', '"
					+ model + "', '"
					+ year + "');"
			);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}
	
	public static void InsertServiceRequest(MechanicShop esql){//4
		//add GUI
	}
	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		String rid = null;
		String mid = null;
		String comment = null;
		String bill = null;
		String datestring = null;
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		System.out.println("Enter service request id:");
		try{
			rid = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid service request id");
		}

		System.out.println("Enter mechanic id:");
		try{
			mid = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid mechanic id");
		}

		System.out.println("Enter today's date and current time (dd/MM/yyyy HH:mm:ss):");
		try{
			datestring = in.readLine();
			date = formatter.parse(datestring);
		} catch (IOException e) {
			System.out.println("Invalid date and time");
		}

		System.out.println("Enter a comment:");
		try{
			comment = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid comment");
		}

		System.out.println("Enter bill amount:");
		try{
			bill = in.readLine();
		} catch (IOException e) {
			System.out.println("Invalid amount");
		}

		int mrows = esql.executeQuery("SELECT * FROM Mechanic WHERE id =  "+mid+";");
		if (mrows != 1) {
			System.out.println("Mechanic does not exist!");
			return;
		}

		List<List<String>> result = esql.executeQueryAndReturnResult("SELECT date FROM Service_Request WHERE rid =  "+rid+";");
		if (result.isEmpty()) {
			System.out.println("Service request does not exist!");
			return;
		}

		String rstdatestring = result.get(0).get(0);
		Date rstdate = formatter.parse(rstdatestring);
		if(rstdate.before(date)) {
			esql.executeUpdate("INSERT INTO Closed_Request VALUES("+
					rid + "," +
					rid + "," +
					mid + ", '" +
					datestring + "', '" +
					comment + "'," +
					bill + ");"
			);
		}
		else {
			System.out.println("Invalid date bro");
		}

	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		String query = "SELECT * "
				+ "FROM Customer C"
					+ "WHERE SELECT *, * "
					+ "FROM Service_Request SR Closed_Request CR "
					+ "WHERE CR.bill < 100 "
						+ "and SR.rid = CR.rid"
						+ "and SR.customer_id = C.id";
		esql.executeQueryAndPrintResult(query);
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql) throws SQLException {//7
		esql.executeQueryAndReturnResult("SELECT C.fname, C.lname " +
                "FROM Customer C, Owns O " +
                "WHERE C.customer_id = O.customer_id AND C.customer_id IN (" +
                "SELECT customer_id " +
                "FROM Owns " +
                "WHERE COUNT(car_vin) > 20 " +
                "GROUP BY customer_id);"
        );
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		String query = "SELECT DISTINCT C.make, C.model, C.year "
				+ "FROM Car	C "
					+ "WHERE SELECT * "
					+ "FROM Service_Request SR"
						+ "WHERE C.vin = SR.carvin "
							+ "and year < 1995"
							+ "and odometer < 50000";
		esql.executeQueryAndPrintResult(query);
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql) throws SQLException {//9
        List<List<String>> oldList = esql.executeQueryAndReturnResult("SELECT C.make, C.model, COUNT(S.rid)" +
                "FROM Car C, Service_Request S" +
                "WHERE C.vin = S.car_vin" +
                "GROUP BY C.vin" +
                "ORDER BY COUNT(S.rid) DESC;"
        );

        int k = 0;
        System.out.println("Enter value for k:");
        try{
            k = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            System.out.println("Invalid value for k");
        }

        List<List<String>> newList = new ArrayList<List<String>>();
        for (int i = 0; i < k; i++) {
            newList.add(oldList.get(k));
        }

        StringBuilder result = new StringBuilder();
        for (List<String> strings : newList) {
            for (String string : strings) {
                result.append(string);
            }
            result.append("\n");
        }
        System.out.println(result);
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//10
		String query = "SELECT DISTINCT C.fname, C.lname, SUM (CR.bill) AS total bill " // distinct?
				+ "FROM Customer C Closed_Request CR Service_Request SR "
				+ "WHERE CR.rid = SR.rid"
					+ "and SR.customer_id = C.id";
		esql.executeQueryAndPrintResult(query);
	}
	
}