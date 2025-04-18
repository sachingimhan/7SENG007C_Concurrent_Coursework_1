package com.iit.ticket.util;

public interface PoolEntity extends Runnable {

    void setRate (int rate);
    String getName();
    void stop();

}
