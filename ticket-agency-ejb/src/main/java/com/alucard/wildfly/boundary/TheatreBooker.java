package com.alucard.wildfly.boundary;

import com.alucard.wildfly.control.TheatreBox;
import com.alucard.wildfly.exception.NoSuchSeatException;
import com.alucard.wildfly.exception.NotEnoughMoneyException;
import com.alucard.wildfly.exception.SeatBookedException;

import org.jboss.logging.Logger;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(TheatreBookerRemote.class)
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class TheatreBooker implements TheatreBookerRemote {
  private static final Logger logger = Logger.getLogger(TheatreBooker.class);

  @EJB
  private TheatreBox theatreBox;
  private int money;

  @PostConstruct
  public void createCustomer() {
    this.money = 100;
  }

  @Override
  public int getAccountBalance() {
    return money;
  }

  @Override
  public String bookSeat(int seatId) throws SeatBookedException, NotEnoughMoneyException, NoSuchSeatException {
    final int seatPrice = theatreBox.getSeatPrice(seatId);
    if (seatPrice > money) {
      throw new NotEnoughMoneyException("You don't have enough money to buy this " + seatId + " seat.");
    }
    theatreBox.buyTicket(seatId);
    money = money - seatPrice;
    logger.infov("Seat {0} booked.", seatId);
    return "Seat booked.";
  }
}
