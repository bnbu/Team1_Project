package service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import model.MemberVO;

public interface IMemberService {
	void register() throws IOException, NoSuchAlgorithmException;
	MemberVO login() throws IOException;
	void logout();
	
	void myProfile() throws SQLException, IOException;
  void editProfile() throws IOException;
  void removeMember() throws IOException;
}