package service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface IMemberService {
	void register() throws IOException, NoSuchAlgorithmException;
	void login() throws IOException;
	void logout();
	
	void myProfile();
    void editProfile();
    void removeMember();
}