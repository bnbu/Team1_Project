package service;

import java.io.IOException;

public interface ITicketService {
	void ticketing();
	void ticketHistory( String str );
	void ticketingCancel( String str ) throws IOException;
	void showScreens();
}
