package view;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.ConnectionSingletonHelper;

public class JdbcTestSelect {

	public static void main(String args[]) throws Exception {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;

		conn = ConnectionSingletonHelper.getConnection();

		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery("SELECT * FROM emp");

			while (rset.next()) {
				System.out.print(rset.getInt(1) + " ");
				System.out.println(rset.getString(2));
			}
		}

		finally {
			if (rset != null)
				rset.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

}