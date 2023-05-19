package controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import service.IAdminService;
import util.ConnectionSingletonHelper;

public class AdminController implements IAdminService {
	private BufferedReader br;
	private ResultSet rs;
	 private StringBuilder sb;
	private PreparedStatement pstmtTotalMovie, pstmtSearchMovie, pstmtInsertScreenInfo, pstmtSearchMovieByNo,pstmtInsertMovie;
	private final String sqlTotalMovie = "SELECT * FROM MOVIE",
						 sqlSearchMovie = "SELECT COUNT(*) FROM MOVIE WHERE MOVIE_NO = ?",
						 sqlInsertScreenInfo = "INSERT INTO SCREENING_INFO VALUES (?||?||SCREENING_INFO_SEQ.NEXTVAL, ?, ?, TO_DATE(?||' '||?, 'YYYY-MM-DD HH24:MI'), TO_DATE(?||' '||?, 'YYYY-MM-DD HH24:MI'), ?)",
						 sqlSearchMovieByNo = "SELECT * FROM MOVIE WHERE MOVIE_NO = ?",
	sqlInsertMovie = "insert into movie values(movie_seq.nextval,?,?,?,?,?,?)";
						 
	
	public AdminController() {
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			Connection conn = ConnectionSingletonHelper.getConnection();
			sb = new StringBuilder();
			pstmtTotalMovie = conn.prepareStatement(sqlTotalMovie);
			pstmtSearchMovie = conn.prepareStatement(sqlSearchMovie);
			pstmtInsertScreenInfo = conn.prepareStatement(sqlInsertScreenInfo);
			pstmtSearchMovieByNo = conn.prepareStatement(sqlSearchMovieByNo);
			pstmtInsertMovie = conn.prepareStatement(sqlInsertMovie);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public void addMovie() {
      
        String title, author, genre, start;
        int length, rank;
        try {
            showMovies();
            System.out.println("───────────────────────────────────────────────");
            System.out.println("                  영화 추가");
            System.out.println("───────────────────────────────────────────────");
            System.out.print("타이틀: "); title = br.readLine().trim();
            System.out.print("상영시간: "); length = Integer.parseInt(br.readLine().trim());
            System.out.print("감독: "); author = br.readLine().trim();
            System.out.print("장르: "); genre = br.readLine().trim();
            System.out.print("상영등급(0: 전연령,1: 12세 ,2: 15세,3: 18세): "); rank = Integer.parseInt(br.readLine().trim());
            System.out.print("개봉일: "); start = br.readLine().trim();
            
            pstmtInsertMovie.setString(1,title); // 타이틀
            pstmtInsertMovie.setInt(2,length);// 상영시간
            pstmtInsertMovie.setString(3,author); // 감독
            pstmtInsertMovie.setString(4,genre); // 장르
            pstmtInsertMovie.setInt(5,rank); // 상영등급
            pstmtInsertMovie.setString(6,start); // 개봉일
            
            pstmtInsertMovie.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("잘못된 입력입니다 다시 확인해주세요.");
        }
        
    }

	private String getRankString(int i) {
		if (i == 0) return "전체";
		else if (i == 1) return "12세";
		else if (i == 2) return "15세";
		else return "18세";
	}
	
    private void showMovies() {
		try {
			rs = pstmtTotalMovie.executeQuery();
			System.out.println("────────┬────────────┬────────────┬───────────────────────────────────────────────┐");
			System.out.println(String.format("%-6s│%-10s│%-10s│%-45s│",
					"번호", "장르", "연령", "제목"));
			System.out.println("────────┼────────────┼────────────┼───────────────────────────────────────────────┤");
			Pattern pattern = Pattern.compile("[ㄱ-힣]");
			while ( rs.next() ) {
				Matcher matcher = pattern.matcher(rs.getString(2));
				int cnt = 0;
				while (matcher.find()) cnt++;
				
				
				int len1 = 12 - rs.getString(5).length(),
					len2 = 16 - getRankString(rs.getInt(6)).getBytes().length,
					len3 = 47 - cnt;
				
				System.out.println(String.format("%-8s│%-" + len1 + "s│%-" + len2 + "s│%-" + len3 + "s│",
						rs.getString(1), rs.getString(5), getRankString(rs.getInt(6)), rs.getString(2)));
			}
			System.out.println("────────┴────────────┴────────────┴───────────────────────────────────────────────┘");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
    private String timeCalc(String start) {
    	StringBuilder ret = new StringBuilder();
    	try {
    		String[] s = start.split(":");
    		int h = Integer.parseInt(s[0]),
    				m = Integer.parseInt(s[1]);
    		
    		int movieLength = rs.getInt(3);
    		h += movieLength / 60;
    		m += movieLength % 60;
    		h += m / 60;
    		m %= 60;
    		h %= 24;
    		
    		return ret.append(String.format("%02d:%02d", h, m)).toString();
    	}
    	catch (Exception e) {
    		return "";
    	}
    }
    @Override
    public void addScreeningInfo() {
        showMovies();
        
        try {
        	String str, date, start, end;
        	int movieNum = -1;
        	System.out.println("메뉴로 돌아가기 : q");
        	while (true) {
        		try {
        			System.out.print("영화의 번호를 입력 : ");
        			str = br.readLine();
        			if (str.equalsIgnoreCase("q")) return;
        			movieNum = Integer.parseInt(str);
        			
        			pstmtSearchMovie.setInt(1, movieNum);
        			rs = pstmtSearchMovie.executeQuery();
        			rs.next();
        			if (rs.getInt(1) < 1) {
        				System.out.println("존재하지 않는 영화입니다, 다시 확인해주세요\n");
        				continue;
        			}
        			pstmtInsertScreenInfo.setInt(1, movieNum);
        			pstmtInsertScreenInfo.setInt(3, movieNum);
        			
        			pstmtSearchMovieByNo.setInt(1, movieNum);
            		rs = pstmtSearchMovieByNo.executeQuery();
            		rs.next();
        			break;
        		}
        		catch (Exception e) {
        			e.printStackTrace();
        			System.out.println("잘못된 입력입니다\n");
        		}
        	}
        	
        	int theaterNum = -1;
        	while (true) {
        		try {
        			System.out.print("상영관 번호를 입력: ");
        			str = br.readLine();
        			if (str.equalsIgnoreCase("q")) return;
        			theaterNum = Integer.parseInt(str);
        			if (theaterNum >= 5) {
        				System.out.println("없는 상영관입니다");
        				continue;
        			}
        			pstmtInsertScreenInfo.setInt(4, theaterNum);
        			break;
        		}
        		catch (Exception e) {
        			System.out.println("잘못된 입력입니다");
        		}
        	}
        	
        	while (true) {
        		try {
        			System.out.print("년/월/일 순으로 날짜를 입력: ");
        			date = br.readLine();
        			if (str.equalsIgnoreCase("q")) return;
        			if (!Pattern.matches("[0-9]{4}/[0-9]{2}/[0-9]{2}", date)) {
        				System.out.println("잘못된 입력입니다");
        				continue;
        			}
        			pstmtInsertScreenInfo.setString(5, date);
        			pstmtInsertScreenInfo.setString(7, date);
        			pstmtInsertScreenInfo.setString(9, date);
        			break;
        		}
        		catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        	
        	while (true) {
        		try {
        			System.out.print("(시작시간 종료시간) 순으로 입력 ex) 10:00 : ");
        			start = br.readLine();
        			if (start.equalsIgnoreCase("q")) return;
        			if (!Pattern.matches("[0-9]{1,2}:[0-9]{2}", start)) {
        				System.out.println("잘못된 입력입니다");
        				continue;
        			}
        			end = timeCalc(start);
        			pstmtInsertScreenInfo.setString(6, start);
        			pstmtInsertScreenInfo.setString(8, end);
        			pstmtInsertScreenInfo.setString(2, start.replace(":", ""));
        			break;
        		}
        		catch (Exception e) {
        			System.out.println("잘못된 입력입니다");
        		}
        	}
        	
        	try {
        		System.out.println("───────────────────────────────────────────────┬────────────┬────────────┬────────────┐");
        		System.out.println(String.format("%-45s│%-10s│%-10s│%-10s│", "제목", "날짜", "시작", "종료"));
        		System.out.println("───────────────────────────────────────────────┼────────────┼────────────┼────────────┤");
        		
        		Pattern pattern = Pattern.compile("[ㄱ-힣]");
    			Matcher matcher = pattern.matcher(rs.getString(2));
    			int cnt = 0;
    			while (matcher.find()) cnt++;
    			int len = 47 - cnt;
        		System.out.println(String.format("%-" + len + "s│%-12s│%-12s│%-12s│", rs.getString(2), date, start, end));
        		System.out.println("───────────────────────────────────────────────┴────────────┴────────────┴────────────┘");
        		System.out.print("추가하시겠습니까? y/n: ");
        		if (br.readLine().equalsIgnoreCase("y")) {
        			pstmtInsertScreenInfo.executeUpdate();
        			System.out.println("추가에 성공했습니다");
        		}
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("추가에 실패");
        }
    }
    

    @Override
    public void sales() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void menu() {
    	addScreeningInfo();
//        addMovie();
    }

}
