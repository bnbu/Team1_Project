package service;

import model.MemberVO;
import util.LoginManager;

public interface IMemberService {
	void register();
	MemberVO login();
	MemberVO logout(LoginManager lm);
	
	void myProfile();
    void editProfile();
    void loginMenu(LoginManager lm, IMemberService ms, ITicketService ts);
    void memberMenu(LoginManager lm);
    void removeMember(LoginManager lm);
    void AllClose();
}