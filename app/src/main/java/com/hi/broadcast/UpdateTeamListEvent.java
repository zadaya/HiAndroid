package com.hi.broadcast;


public class UpdateTeamListEvent {
    public String message;

    public UpdateTeamListEvent(String message) {
        this.message = message;
    }

    public UpdateTeamListEvent() {
    }

    public String getMessage(){
        return message;
    }
}
