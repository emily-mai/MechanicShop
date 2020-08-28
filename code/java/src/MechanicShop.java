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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
				 * NOTE: I have commented this because my front end handles this
				 */
//				switch (readChoice()){
//					case 1: AddCustomer(esql); break;
//					case 2: AddMechanic(esql); break;
//					case 3: AddCar(esql); break;
//					case 4: InsertServiceRequest(esql); break;
//					case 5: CloseServiceRequest(esql); break;
//					case 6: ListCustomersWithBillLessThan100(esql); break;
//					case 7: ListCustomersWithMoreThan20Cars(esql); break;
//					case 8: ListCarsBefore1995With50000Milles(esql); break;
//					case 9: ListKCarsWithTheMostServices(esql); break;
//					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
//					case 11: keepon = false; break;
//				}
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


	/* Purpose of this function is to add a customer and to check that inputted info is valid based on the constraints
	of the database schema by using INSERT INTO query*/
	public static void AddCustomer(MechanicShop esql, String fname, String lname, String phone, String addy) {//1
		try{
			esql.executeUpdate("INSERT INTO Customer (fname, lname, phone, address) VALUES("+ "'" +
					fname + "', '" +
					lname + "', '" +
					phone + "','" +
					addy + "');"
			);
			System.out.println("Customer inserted!");
			esql.executeQueryAndPrintResult("SELECT * FROM Customer ORDER BY id DESC LIMIT 1;");
		} catch (SQLException throwables) {
			System.out.println("Insert failed; invalid date. Please try again!");
			throwables.printStackTrace();
		}
	}

	/* Purpose of this function is to add a mechanic and to check that inputted info is valid based on the constraints
	of the database schema by using INSERT INTO query*/
	public static void AddMechanic(MechanicShop esql, String fname, String lname, String experience) {//2
		String query = "INSERT INTO "
				+ "Mechanic (fname, lname, experience)"
				+ "VALUES(" + "'"
				+ fname + "', '"
				+ lname + "', '"
				+ experience + "');";
		try {
			esql.executeUpdate(query);
			System.out.println("Mechanic inserted!");
			esql.executeQueryAndPrintResult("SELECT * FROM Mechanic ORDER BY id DESC LIMIT 1;");
		} catch (SQLException throwables) {
			System.out.println("Insert failed; invalid date. Please try again!");
			throwables.printStackTrace();
		}
	}

	/* Purpose of this function is to add a car and to check that inputted info is valid based on the constraints
	of the database schema by using INSERT INTO query*/
	public static void AddCar(MechanicShop esql, String vin, String make, String model, String year){//3
		try{
			esql.executeUpdate("INSERT INTO Car VALUES("+ "'"
					+ vin + "'" + ",'"
					+ make + "', '"
					+ model + "', '"
					+ year + "');"
			);
			System.out.println("Car inserted!");
		} catch (SQLException throwables) {
			System.out.println("Insert failed; invalid date. Please try again!");
		}
	}

	/* My partner was suppose to do this function. However, the code she sent for this function is incorrect and I did
	not get the time to redo/fix this function completely. Would have wanted the function to, given
	a last name, search for existing customers. If there are existing customers let me view all the cars of the customer and
	provide the option to initiate the service request for one of the listed cars; otherwise prompt user to add the car and
	service request. Additionally, I would want to be able to check the status of a car(open or closed) */
	public static void InsertServiceRequest(MechanicShop esql, String rid, String cid, String vin, LocalDate currentLocalDate, String odometer, String complain){//4
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date currentDate = Date.from(currentLocalDate.atStartOfDay(defaultZoneId).toInstant());
		try{
			esql.executeUpdate("INSERT INTO Service_Request VALUES("
					+ rid + ","
					+ cid + ", '"
					+ vin + "', '"
					+ currentDate.toString() + "',"
					+ odometer + ",'"
					+ complain + "');"
			);
		} catch (SQLException throwables) {
			System.out.println("Insert failed; invalid date. Please try again!");
			throwables.printStackTrace();
		}
	}

	/* Given a service request id and and mechanic id, the client application should verify the information provided
	and attempt to create a closing request record. */
	public static void CloseServiceRequest(MechanicShop esql, String wid, String rid, String mid, String comment, String bill, LocalDate currentLocalDate) {//5
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Date currentDate = Date.from(currentLocalDate.atStartOfDay(defaultZoneId).toInstant());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
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
			Date rstdate = null;
			try {
				rstdate = formatter.parse(rstdatestring);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			assert rstdate != null;
			if(rstdate.before(currentDate)) {
				esql.executeUpdate("INSERT INTO Closed_Request VALUES(" +
						wid + "," +
						rid + "," +
						mid + ", '" +
						currentDate.toString() + "', '" +
						comment + "'," +
						bill + ");"
				);
				System.out.println("Closed Service Request!");
			}
			else {
				System.out.println("Invalid date! Please try again.");
			}
		} catch (SQLException throwables) {
			System.out.println("Insert failed. Please try again!");
			throwables.printStackTrace();
		}
	}
	
	public static List<List<String>> ListCustomersWithBillLessThan100(MechanicShop esql) throws SQLException {//6
		String query = "SELECT date, comment, bill "
				+ "FROM Closed_Request CR "
				+ "WHERE CR.bill < 100 ";
		return esql.executeQueryAndReturnResult(query);
	}
	
	public static List<List<String>> ListCustomersWithMoreThan20Cars(MechanicShop esql) throws SQLException {//7
		return esql.executeQueryAndReturnResult("SELECT DISTINCT C.fname, C.lname " +
                "FROM Customer C, Owns O " +
                "WHERE C.id = O.customer_id AND C.id IN (" +
                "SELECT customer_id " +
                "FROM Owns " +
                "GROUP BY customer_id " +
				"HAVING COUNT(car_vin) > 20);"
        );
	}
	
	public static List<List<String>> ListCarsBefore1995With50000Milles(MechanicShop esql) throws SQLException {//8
		String query = "SELECT C.make, C.model, C.year "
				+ "FROM Car	C, Service_Request SR "
				+ "WHERE C.vin = SR.car_vin "
				+ "AND C.year < 1995 AND SR.odometer < 50000;";
		return esql.executeQueryAndReturnResult(query);
	}
	
	public static List<List<String>> ListKCarsWithTheMostServices(MechanicShop esql, int k) throws SQLException {//9
        List<List<String>> oldList = esql.executeQueryAndReturnResult("SELECT C.make, C.model, COUNT(S.rid) " +
                "FROM Car C, Service_Request S " +
                "WHERE C.vin = S.car_vin " +
                "GROUP BY C.make, C.model, C.vin " +
                "ORDER BY COUNT(S.rid) DESC;"
        );

        List<List<String>> newList = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            newList.add(oldList.get(i));
        }
        return newList;
	}
	
	public static List<List<String>> ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql) throws SQLException {//10
		String query = "SELECT C.fname, C.lname, SUM(CR.bill) " // distinct?
				+ "FROM Customer C, Closed_Request CR, Service_Request SR "
				+ "WHERE CR.rid = SR.rid AND SR.customer_id = C.id "
				+ "GROUP BY C.fname, C.lname "
				+ "ORDER BY SUM(CR.bill) DESC;";
		return esql.executeQueryAndReturnResult(query);
	}
	
}