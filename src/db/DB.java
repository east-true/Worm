package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DB {
	private final String URL="jdbc:oracle:thin:@net.yju.ac.kr:1521:orcl";
	private final String ID = "s1501201";
	private final String PWD = "p1501201";
	
	private static DB instance = new DB();

    private DB() {
        try {
        	Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DB getInstance() {
        return instance;
    }

    private Connection getConnection() throws Exception {
        Connection conn = null;
        conn = DriverManager.getConnection(URL, ID, PWD);
        return conn;
    }
	 
	 public List<WormVo> select() throws Exception {
		 List<WormVo> rankList = new ArrayList<WormVo>();
		 String query = "select rownum, nickname, score " + 
		 			"from (select * " + 
		 			"from worm " + 
		 			"order by score desc) " + 
		 			"where rownum < 11";
		 
		 try(Connection conn = getConnection();
			 PreparedStatement pstmt= conn.prepareStatement(query);			
			 ResultSet rs = pstmt.executeQuery();) {
			 	
				while(rs.next()) {
					String nickname = rs.getString("nickname");
					int score = rs.getInt("score");
					
					WormVo worm = new WormVo(nickname, score);
					rankList.add(worm);
				}
				
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rankList;
	 }
	 
	 public void insert(String nickname, int score) {
		 Connection conn = null;
		 PreparedStatement pstmt = null;
		 try {
			 	conn = getConnection();
			 	String query = "insert into worm values(wno_seq.nextval, ?, ?)";
			 	
			 	pstmt= conn.prepareStatement(query);
				pstmt.setString(1, nickname);
				pstmt.setInt(2, score);
				
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(pstmt != null) try {pstmt.close();} catch(Exception e) {e.printStackTrace();}
				if(conn != null) try {conn.close();} catch(Exception e) {e.printStackTrace();}
			}
	 }
}
