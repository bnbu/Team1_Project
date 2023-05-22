package service;

import java.io.IOException;

public interface ITicketService {
	void ticketing(String str);
	void ticketHistory( String str );
	void ticketingCancel( String str ) throws IOException;
	void showScreens();
	void AllClose();
}
