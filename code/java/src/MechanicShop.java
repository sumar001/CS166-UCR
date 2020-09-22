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


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
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
		System.out.println(" Enter the customer's id:");
                String c_id = "";

                try{
                        c_id = in.readLine();
                   }
                catch(Exception e) {
                   System.out.println("Wrong input entry");
                        }

		System.out.println(" Enter the customer's first name:");
		String fname = "";
	
		try{
			fname = in.readLine();
		   }
		catch(Exception e) {
		   System.out.println("Wrong input entry");
			}

		System.out.println("Enter the customer's last name:");
		String lname = "";
		
		try{
			lname = in.readLine();
		   }
		catch(Exception e) {
			System.out.println("Wrong input entry");
	       }
		
		System.out.println("Enter the customer's phone numer in this format(xxx)xxx-xxxx:");
		String s_phone = "";
		
		try{
			s_phone = in.readLine();
		   }
	catch(Exception e) {
		System.out.println("Wrong input entry");
		}

	System.out.println("Enter the customer's address:");
                String address = "";

                try{
                        address = in.readLine();
                   }
                catch(Exception e) {
                        System.out.println("Wrong input entry");
               }

		esql.executeUpdate("INSERT INTO Customer VALUES (" + c_id + ",'" + fname + "' , '" + lname + "' , '" + s_phone + "' , '" + address + "')");
		esql.executeQueryAndPrintResult("Select * from Customer;");	
	} 
	
	public static void AddMechanic(MechanicShop esql) throws SQLException{//2
		System.out.println("Enter the first name of the mechanic: ");
		String fname = "";
		
		try {
			fname = in.readLine();
		    }
		catch(Exception e) {
			System.out.println("Wrong Input entry");
			}

		System.out.println("Enter the last name of the mechanic: ");
		String lname = "";
		try {
			lname = in.readLine();
		    }
		catch(Exception e) {
			System.out.println("Wrong input entry");
		       }
			
		int     mech_id;
		System.out.println("Enter the mechanics unique id:");
		String mech = "";
		try{
			mech = in.readLine();
		   }
		catch(Exception e) {
			System.out.println("Wrong input entry");
			}
		mech_id = Integer.parseInt(mech);
		System.out.println("Enter the mechanic's experience (in years):");
			int mech_exp;
			String exp_s = "";
		 try{
			exp_s = in.readLine();
		    }
		 catch(Exception e){
		  	System.out.println("Wrong Input entry");
		   }
		 mech_exp = Integer.parseInt(exp_s);
		esql.executeUpdate("INSERT INTO Mechanic VALUES ('" + mech_id + "' , '"  + fname + "' , '" + lname + "' , '" + mech_exp + "');");
		esql.executeQueryAndPrintResult("Select * from Mechanic;");
       }
		
	public static void AddCar(MechanicShop esql) throws SQLException{//3
		System.out.println("Enter the VIN of the car: ");
                String vin = "";

                try {
                        vin = in.readLine();
                    }
                catch(Exception e) {
                        System.out.println("Wrong Input entry");
                        }

		System.out.println("Enter the make of the car: ");
                String make = "";

                try {
                        make = in.readLine();
                    }
                catch(Exception e) {
                        System.out.println("Wrong Input entry");
                        }
		
		System.out.println("Enter the model of the car: ");
                String model = "";

                try {
                        model = in.readLine();
                    }
                catch(Exception e) {
                        System.out.println("Wrong Input entry");
                        }
		
		System.out.println("Enter the year of the car: ");
                String c = "";

                try {
                        c = in.readLine();
                    }
                catch(Exception e) {
                        System.out.println("Wrong Input entry");
                        }
		
		esql.executeUpdate("INSERT INTO Car VALUES (" + "'" + vin + "' , '" + make + "' , '" + model + "' , '" +  c + "');");  
		esql.executeQueryAndPrintResult("Select * from Car;");
   }

	public static void InsertServiceRequest(MechanicShop esql){//4
    
	try{
			String q_size = String.format("SELECT rid FROM Service_Request");
			List<List<String>> ridsize = esql.executeQueryAndReturnResult(q_size);
			int n_rid = ridsize.size() + 1;

         		System.out.print("Enter the last name of customer: ");
			String s_lname = in.readLine();
			
			String query = String.format("SELECT * FROM Customer WHERE lname = '%s'", s_lname);
			int rowcount = esql.executeQueryAndPrintResult(query);


			if(rowcount == 0) {
				System.out.println("Last name does not exist, would you like to add a new customer? Y/N: ");
				String resp = in.readLine();

				if(resp.equals("y") || resp.equals("Y")) {
					AddCustomer(esql);
				}
				else {
				}
				
				
			}
			else {
				System.out.println("Input the id of the customer you would like to select: ");
				String resp_id = in.readLine();
				int custid = Integer.parseInt(resp_id);
				query = String.format("SELECT Row_Number() OVER ( ORDER BY Owns.car_vin ), Car FROM Customer,Owns,Car WHERE Customer.id = Owns.customer_id AND Car.vin = Owns.car_vin AND Owns.customer_id = '%s'", custid);
				rowcount = esql.executeQueryAndPrintResult(query);
				System.out.println("Please input the Row Number of the car you would like to select, or type 0 to add a new car: ");
				String row0 = in.readLine();
				int rownum = Integer.parseInt(row0);
				
				if(rownum == 0) {
					System.out.print("Enter the new car's VIN: ");
					String newvin = in.readLine();
					System.out.print("Enter the make of the new car: ");
					String newmake = in.readLine();
					System.out.print("Enter new car's model: ");
					String newmodel = in.readLine();
					System.out.print("Enter new car's year: ");
					String year_num = in.readLine();
					int newyear = Integer.parseInt(year_num);

					query = String.format("INSERT INTO Car(vin, make, model, year) VALUES('%s', '%s', '%s', %d)", newvin, newmake, newmodel, newyear);
					esql.executeUpdate(query);
					
					q_size = String.format("SELECT ownership_id FROM Owns");
					List<List<String>> ownsize = esql.executeQueryAndReturnResult(q_size);
					int newown = ownsize.size() + 1;
					query = String.format("INSERT INTO Owns(ownership_id, customer_id, car_vin) VALUES(%d, %d, '%s')", newown, custid, newvin);
					esql.executeUpdate(query);


					System.out.print("New car added to database.\n");

					System.out.print("Enter the odometer reading on the car: ");
					String newodo = in.readLine();

					int odo = Integer.parseInt(newodo);
					System.out.print("Enter customer's complaints with the car: ");
					String newcomplaint = in.readLine();
					

					query = String.format("INSERT INTO Service_Request(rid, customer_id, car_vin, date, odometer, complain) VALUES(%d, %d, '%s', CURRENT_DATE, %d, '%s')", n_rid, custid, newvin, odo, newcomplaint);
					esql.executeUpdate(query);
					System.out.print("Your service request id is: ");
					System.out.print(n_rid);
					System.out.printf("%n"); 
				}
				else {

					query = String.format("SELECT test FROM (Select Row_Number() OVER ( ORDER BY Owns.car_vin ) as rownumber,Car.vin,Car.make,Car.model,Car.year FROM Customer,Owns,Car WHERE Customer.id = Owns.customer_id AND Car.vin = Owns.car_vin AND Owns.customer_id = '%s') AS test WHERE rownumber = %d ", custid,rownum);
					List<List<String>> result  = esql.executeQueryAndReturnResult(query);
					
					String car = result.get(0).get(0);
					System.out.println(car);
					String[] output = car.split(",");
					String newvin = output[1];
					
					System.out.print("Enter the current odometer reading on the car: ");
					String newodo = in.readLine();
					int odo = Integer.parseInt(newodo);
					System.out.print("Enter customer's complaints with the car: ");
					String newcomplaint = in.readLine();

					query = String.format("INSERT INTO Service_Request(rid, customer_id, car_vin, date, odometer, complain) VALUES(%d, %d, '%s', CURRENT_DATE, %d, '%s')", n_rid, custid, newvin, odo, newcomplaint);
					esql.executeUpdate(query);
					System.out.print("Your service request id is: ");
					System.out.print(n_rid);
					System.out.printf("%n"); 

				}

			}


         		
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}

	}	

	//Function 5 is done by Jeeavn
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5

 /*		try{

            System.out.println("The rid is: "+args[0]);
            System.out.println("The mid is: "+args[1]);

            System.out.print("Enter the date the request was closed: ");
            String cr_date = in.readLine();

            System.out.print("Enter the comments on the closed request: ");
            String cr_comment = in.readLine();

            System.out.print("Enter the bill of the closed request: ");
            String cr_bill = in.readLine();

            String query1 = "INSERT INTO Closed_Request (rid, mid, date, comment, bill) VALUES ( " + "'" + args[0] + "' , '" + args[1] + "' , '" + cr_date + "' , '" + cr_comment + "' , " + cr_bill +")";

            esql.executeUpdate(query1);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }	
	}
 */
	}
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		try{
         		String query6 = "SELECT Customer.fname, Customer.lname, Closed_Request.bill, Service_Request.date, Closed_Request.comment FROM Customer,Closed_Request,Service_Request WHERE Closed_Request.bill < 100 AND Closed_Request.rid = Service_Request.rid AND Service_Request.customer_id = Customer.id";
         		int rowCount = esql.executeQueryAndPrintResult(query6);
         		System.out.println ("total row(s): " + rowCount);
      		}
		catch(Exception e){
         		System.err.println (e.getMessage());
      		}
	}	

	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
	try{
 		String query7 = "SELECT cars.fname, cars.lname, cars.numCars FROM (SELECT Owns.customer_id, Customer.fname, Customer.lname, COUNT(*) numCars FROM Owns,Customer WHERE Customer.id = Owns.customer_id GROUP BY Owns.customer_id, Customer.fname, Customer.lname) AS cars WHERE numCars > 20";
		
 		int rowCount = esql.executeQueryAndPrintResult(query7);
 		System.out.println ("total row(s): " + rowCount);
	}catch(Exception e){
 		System.err.println (e.getMessage());
	}

	}	

	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		try{
         		String query8 = "SELECT Car.make, Car.model, Car.year, Service_Request.odometer FROM Car,Service_Request WHERE Service_Request.car_vin = Car.vin AND Service_Request.odometer < 50000 AND Car.year < 1995";
         		int rowCount = esql.executeQueryAndPrintResult(query8);
         		System.out.println ("total row(s): " + rowCount);
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}
	}	

	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
	try{
			System.out.print("The number of cars you would like to see (k): ");
			String resp9 = in.readLine();
			String query9 = "SELECT make, model, R.sreq FROM Car AS C, ( SELECT car_vin, COUNT(rid) AS sreq FROM Service_Request GROUP BY car_vin ) AS R WHERE R.car_vin = C.vin ORDER BY R.sreq DESC LIMIT "+resp9+";";
			int rowCount = esql.executeQueryAndPrintResult(query9);
			System.out.println("total row(s): " + rowCount);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}			
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//10
		//
		try{
			String query10 = "SELECT C.fname , C.lname, Total FROM Customer AS C, (SELECT sr.customer_id, SUM(CR.bill) AS Total FROM Closed_Request AS CR, Service_Request AS SR WHERE CR.rid = SR.rid GROUP BY SR.customer_id) AS A WHERE C.id=A.customer_id ORDER BY A.Total DESC;";
			int rowCount = esql.executeQueryAndPrintResult(query10);
			System.out.println("total row(s): " + rowCount);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	}
}
