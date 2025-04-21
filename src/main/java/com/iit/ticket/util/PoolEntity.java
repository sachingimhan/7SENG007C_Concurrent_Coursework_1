package com.iit.ticket.util;

public interface PoolEntity extends Runnable {

    int getRate();

    void setRate(int rate);

    String getName();

    void stop();

}
