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
        Reader reader = new FileReader("./lib/oracle.properties"); // 읽어올 파일 지정
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
	
	public static void menuHelp() {
		System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=JDBC Query=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		System.out.println("0. rollback \t 1. 전체목록\t 2. 삽입\t\t 3. 수정\n4. 삭제\t\t 5. 조건 검색\t 6. 종료\t\t 9. commit");
	}
}
