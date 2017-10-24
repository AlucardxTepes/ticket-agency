package com.alucard.wildfly.boundary;

import com.alucard.wildfly.exception.NoSuchSeatException;
import com.alucard.wildfly.exception.NotEnoughMoneyException;
import com.alucard.wildfly.exception.SeatBookedException;

public interface TheatreBookerRemote {

  int getAccountBalance();

  public String bookSeat(int seatId) throws SeatBookedException, NotEnoughMoneyException, NoSuchSeatException;
}
