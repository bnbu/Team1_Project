package service;

public interface IMemberService {
	void register();
	void login();
	void logout();
	
	void myProfile();
    void editProfile();
    void removeMember();
}