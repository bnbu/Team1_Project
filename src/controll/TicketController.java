package controll;

import java.util.*;
import java.sql.*;
import service.ITicketService;

public class TicketController implements ITicketService{
	private PreparedStatement pstmtTotalMovie, pstmtTotalScreeningInfo, pstmtInsertTicket, pstmtCancelTicket,
							  pstmtSearchTicketInfo, pstmtSearchSeatInfo;
	private final String sqlTotalMovie = "SELECT * FROM MOVIE",
						 sqlTotalScreeningInfo = "SELECT * FROM SCREENING_INFO",
						 sqlInsertTicket = "INSERT INTO TICKETING VALUES (?, ?, ?, ?, ?, ?, ?, 1)",
						 sqlCancelTicket = "UPDATE TICKETING SET VALID = 0 WHERE TICKETN_NO = ?",
						 sqlSearchTicketInfo = "SELECT * FROM TICKETING WHERE MEMBER_BNO = ?",
						 sqlSearchSeatInfo = "SELECT * FROM  SEAT_INFO SI JOIN TICKETING T ON SI.TICKET_NO = T.TICKET_NO WHERE T.VALID = 1;";	
	
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

	public void ticketHistory() {
		// 1. 유저 ID로 예매 정보를 불러옴
	}

	public void ticketingCancel() {
		// 1. 유저 ID로 예매 정보 불러옴
		// 2. 취소하고자 하는 예매 정보를 선택
		// 3. 해당 예매 정보의 valid를 제거
	}

	public void showScreens() {
		// 1. 상영정보를 불러옴
	}
}
