package controll;

import java.util.*;

import javax.annotation.processing.AbstractProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import service.ITicketService;

public class TicketController implements ITicketService{
	private PreparedStatement pstmtTotalMovie, pstmtTotalScreeningInfo, pstmtSearchValidTicketInfo, pstmtInsertTicket, pstmtCancelTicket,
	pstmtSearchTicketInfo, pstmtSearchSeatInfo;
	private final String sqlTotalMovie = "SELECT * FROM MOVIE",
			sqlTotalScreeningInfo = "SELECT * FROM SCREENING_INFO",
			sqlInsertTicket = "INSERT INTO TICKETING VALUES (?, ?, ?, ?, ?, ?, ?, 1)",
			sqlSearchValidTicketInfo = "SELECT * FROM TICKETING WHERE MEMBER_BNO = ? AND VALID = 1",
			sqlCancelTicket = "UPDATE TICKETING SET VALID = 0 WHERE TICKETN_NO = ?",
			sqlSearchTicketInfo = "SELECT * FROM TICKETING WHERE MEMBER_BNO = ?",
			sqlSearchSeatInfo = "SELECT * FROM  SEAT_INFO SI JOIN TICKETING T ON SI.TICKET_NO = T.TICKET_NO WHERE T.VALID = 1;";	

	ResultSet rs;
	ResultSetMetaData rsmd ;

	public TicketController(Connection conn) {

	}

	public void ticketing() {
		// 0. 영화를 선택
		// 1.선택한 영화로 상영정보를 가져온다
		// 2. 상영정보를 선택
		// 3. 인원수 (청소년, 일반 등등)을 결정
		// 4. 상영정보번호로 존재하는 좌석을 가져와서, 좌석의 여부를 알려줌
		// 		=> 좌석정보의 에매 번호로부터 join을 통해 vaild로 좌석 계산에 포함할지 말지 계산
		// 5. 좌석을 인원수만큼 선택
		// 6. 예매 완료
	}

	/*
	1 string 예매번호
	2 string 아이디
	3 string 상여정보번호
	4 number 예매인원
	5 number 예매 총 가격
	6 date 예매 날짜
	7 date 예매 취소 날짜
	8 number 유효
	 */

	public void ticketHistory( String ID ) {
		// 1. 유저 ID로 예매 정보를 불러옴
		try {
			pstmtSearchTicketInfo.setString(1, ID);
			rs = pstmtSearchTicketInfo.executeQuery();
			rsmd = rs.getMetaData(); // 해당 테이블에 대한 정보

			while( rs.next() ) {
					int valid = rs.getInt(8);
					switch ( valid  ) {
					case 0: // 취소
						System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
						System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
						System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
						System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
						System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
						System.out.print(rsmd.getColumnName(7) + " : " + rs.getDate(7) + " ");
						break;
					case 1: // 
						System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
						System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
						System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
						System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
						System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
						break;
					} // end switch
			} // end while
		} catch (Exception e) { e.printStackTrace(); }
	} // end ticketHistory

	public void ticketingCancel(String ID) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// 1. 유저 ID로 예매 정보 불러옴
		try {
			pstmtSearchValidTicketInfo.setString(1, ID);
			rs = pstmtSearchValidTicketInfo.executeQuery();
			rsmd = rs.getMetaData(); // 해당 테이블에 대한 정보
			
			while( rs.next() ) {
				System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
				System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
				System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
				System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
				System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
			};
			
		} catch (SQLException e) { e.printStackTrace(); }
		// 2. 취소하고자 하는 예매 정보를 선택
		// 3. 해당 예매 정보의 valid를 제거
		try {
			String TicketNo = br.readLine();
			pstmtCancelTicket.setString(1, TicketNo);
			rs = pstmtCancelTicket.executeQuery();
			rsmd = rs.getMetaData();
			
			while( rs.next() ) {
				System.out.print(rsmd.getColumnName(1) + " : " + rs.getString(1) + " ");
				System.out.print(rsmd.getColumnName(3) + " : " + rs.getString(3) + " ");
				System.out.print(rsmd.getColumnName(4) + " : " + rs.getInt(4) + " ");
				System.out.print(rsmd.getColumnName(5) + " : " + rs.getInt(5) + " ");
				System.out.print(rsmd.getColumnName(6) + " : " + rs.getDate(6) + " ");
				System.out.print(rsmd.getColumnName(7) + " : " + rs.getDate(7) + " ");
			};
			
		} catch (SQLException e) { e.printStackTrace(); }
	}

	public void showScreens() {
		// 1. 상영정보를 불러옴
	}
}
