package service;

import java.io.IOException;
import java.sql.SQLException;

public interface IMemberService {
	void register();
	void login();
	void logout();
	
	void myProfile() throws SQLException, IOException;
    void editProfile() throws IOException;
    void removeMember() throws IOException;
}