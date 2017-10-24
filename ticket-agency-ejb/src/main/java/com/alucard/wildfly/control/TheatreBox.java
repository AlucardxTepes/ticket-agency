package com.alucard.wildfly.control;

import com.alucard.wildfly.entity.Seat;
import com.alucard.wildfly.exception.NoSuchSeatException;
import com.alucard.wildfly.exception.SeatBookedException;

import org.jboss.logging.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

@Singleton
@Startup
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class TheatreBox {
	private static final Logger logger = Logger.getLogger(TheatreBox.class);

	private Map<Integer, Seat> seats;

	@PostConstruct
	public void setupTheatre() {
		seats = new HashMap<>();
		int id = 0;
		for (int i = 0; i < 5; i++) {
			addSeat(new Seat(++id, "Stalls", 40));
			addSeat(new Seat(++id, "Circle", 42));
			addSeat(new Seat(++id, "Balcony", 10));
		}
		logger.info("Seat Map Constructed");
	}

	private void addSeat(Seat seat) {
		seats.put(seat.getId(), seat);
	}

	@Lock(READ)
	public Collection<Seat> getSeats() {
		return Collections.unmodifiableCollection(seats.values());
	}

	@Lock(READ)
	public int getSeatPrice(int seatId) throws NoSuchSeatException {
		return getSeat(seatId).getPrice();
	}

	@Lock(WRITE)
	public void buyTicket(int seatId) throws SeatBookedException, NoSuchSeatException {
		final Seat seat = getSeat(seatId);
		if (seat.isBooked()) {
			throw new SeatBookedException("Seat " + seatId + " already booked.");
		}
	}

	@Lock(READ)
	private Seat getSeat(int seatId) throws NoSuchSeatException {
		final Seat seat = seats.get(seatId);
		if (seat == null) {
			throw new NoSuchSeatException("Seat " + seatId + " does not exist!");
		}
		return seat;
	}

}
