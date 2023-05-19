package service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import model.MemberVO;
import util.LoginManager;

public interface IMemberService {
	void register() throws IOException, NoSuchAlgorithmException;
	MemberVO login() throws IOException;
	MemberVO logout();
	
	void myProfile() throws SQLException, IOException;
    void editProfile() throws IOException;
    void loginMenu(LoginManager lm, IMemberService ms, ITicketService ts) throws NumberFormatException, IOException;
    void memberMenu(LoginManager lm) throws NumberFormatException, IOException;
    void removeMember(LoginManager lm) throws IOException;
    void AllClose();
}