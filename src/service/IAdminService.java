package service;

import util.LoginManager;

public interface IAdminService {
    //영화추가
    void addMovie();
    //상영정보
    void addScreeningInfo();
    //매출
    void sales();
    //권능취소
    void cancel();
    
    void menu(LoginManager lm);
}
