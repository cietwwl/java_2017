package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class jdbcUtilTest {
	private static Connection conn;  
	public static void test() throws Exception{
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://119.29.57.138/test?user=dragon&password=dragon" +
				"&useUnicode=true&characterEncoding=utf-8";//ָ��jdbc����Դ
		String tablename = "huodong_czzhl";
		
		Class.forName(driver);
		conn=DriverManager.getConnection(url);
		Statement stmt = null; 
		stmt = conn.createStatement(); 
		
		for(int i = 0;i <10; i++){
			String no = "0000"+i;
			no = no.substring(no.length()-3);
			String s1="INSERT INTO huodong_czzhl (uid , date , count ) VALUES ( "+i+" , '2015-08-01' , '"+i%5+"' );";
//			stmt.executeUpdate(s1); 
			System.out.println(s1);
		}
		


		conn.close(); 
	}
	public static void main(String[] args) {
		try {
			test();
			String s = "012345";
			
			System.out.println(s.substring(s.length()-2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}