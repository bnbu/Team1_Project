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
    private Connection conn;
    private BufferedReader br;
    private StringBuilder sb;

    private PreparedStatement pstmtTotalMovie, pstmtInsertMovie;
    private final String sqlTotalMovie = "SELECT * FROM MOVIE",
            sqlInsertMovie = "insert into movie values(movie_seq.nextval,?,?,?,?,?,?)";
  

    public AdminController() throws Exception {
        conn = ConnectionSingletonHelper.getConnection();
        br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();
        pstmtTotalMovie = conn.prepareStatement(sqlTotalMovie);
        pstmtInsertMovie = conn.prepareStatement(sqlInsertMovie);
    }

    @Override
    public void addMovie() {
      
        String title, author, genre, start;
        int length, rank;
        try {
            sb.setLength(0);
            ResultSet rs = pstmtTotalMovie.executeQuery();
            sb.append("                                영화 목록").append("\n");
            sb.append("────────┬────────────┬────────────┬───────────────────────────────────────────────┐").append("\n");
            sb.append(String.format("%-6s│%-10s│%-10s│%-45s│","번호", "장르", "연령", "제목")).append("\n");
            sb.append("────────┼────────────┼────────────┼───────────────────────────────────────────────┤").append("\n");
            Pattern pattern = Pattern.compile("[ㄱ-힣]");
            while ( rs.next() ) {
                Matcher matcher = pattern.matcher(rs.getString(2));
                int cnt = 0;
                while (matcher.find()) cnt++;


                int len1 = 12 - rs.getString(5).length(),
                        len2 = 16 - getRankString(rs.getInt(6)).getBytes().length,
                        len3 = 47 - cnt;

                sb.append(String.format("%-8s│%-" + len1 + "s│%-" + len2 + "s│%-" + len3 + "s│",
                        rs.getString(1), rs.getString(5), getRankString(rs.getInt(6)), rs.getString(2))).append("\n");
            }
            sb.append("────────┴────────────┴────────────┴───────────────────────────────────────────────┘").append("\n\n");
            System.out.println(sb);
            System.out.println("───────────────────────────────────────────────");
            System.out.println("                  영화 추가");
            System.out.println("─────────2──────────────────────────────────────");
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

    @Override
    public void addScreeningInfo() {

    }

    @Override
    public void sales() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void menu() {
        addMovie();
    }

}
