package com.hi.broadcast;


public class UpdateVillageEvent {
    public String message;

    public UpdateVillageEvent(String message) {
        this.message = message;
    }

    public UpdateVillageEvent() {
    }

    public String getMessage(){
        return message;
    }
}
