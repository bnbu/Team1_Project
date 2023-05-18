package util;

import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;
// ConnectionHelper의 문제
// 매번 드라이버 로드 -> connection 생성
public class ConnectionSingletonHelper {
	private static Connection conn;
	private ConnectionSingletonHelper() {	}
	public static Connection getConnection() throws Exception {
		if (conn != null) return conn;
		Properties properties = new Properties();
        Reader reader = new FileReader("./src/lib/oracle.properties"); // 읽어올 파일 지정
        properties.load(reader);
        String driverName = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String pwd = properties.getProperty("password");
        Class.forName(driverName);
        return conn = DriverManager.getConnection(url, user, pwd);
	}
//	public static Connection getConnection(String dsn) {
//		if (conn != null) return conn;
//		
//		try {
//			if (dsn.equalsIgnoreCase("mysql")) {
//				Class.forName("jdbc:mysql://localhost:3306/???"); // mysql 로드
//			}
//			else if (dsn.equalsIgnoreCase("oracle")){	// oracle 로드
//				Class.forName("jdbc:oracle:thin:@V7LAPSS78F8HACJD_high?TNS_ADMIN=C:/Users/KOSA/Documents/Wallet_V7LAPSS78F8HACJD"); // oracle 로드
//			}
//			conn = DriverManager.getConnection("", "", "");
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		finally {
//			return conn;
//		}
//	}
	
		public static void close() {
			if( conn != null)
				try {
					conn.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
		}
		
		public static void close(Statement stmt) {
			if( stmt != null)
				try {
					stmt.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
		}
		
		public static void close(PreparedStatement pstmt) {
			if( pstmt != null)
				try {
					pstmt.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
		}
		
		public static void close(ResultSet rs) {
			if( rs != null)
				try {
					rs.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
		}
	}
