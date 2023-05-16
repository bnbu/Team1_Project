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
	
	public MainController() {
		try {
			conn = ConnectionSingletonHelper.getConnection();
			
			// Service 할당
			
			// pstmt 할당
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void menu() {
		
	}
}
