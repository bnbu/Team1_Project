package controll;

import java.io.*;
import java.sql.*;
import java.util.*;

import service.IMemberService;
import service.IProfileService;
import service.ITicketService;
import util.ConnectionSingletonHelper;

public class MainController { 
	private IMemberService ms;
	private IProfileService ps;
	private ITicketService ts;
	
	private Connection conn;
	private BufferedReader br;
	
	public MainController() {
		try {
			conn = ConnectionSingletonHelper.getConnection();
			br = new BufferedReader(new InputStreamReader(System.in));
			
			// Service 할당
			ts = new TicketController(conn, br);
			
			// pstmt 할당
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void menu() {
		ts.showScreens();
	}
}
