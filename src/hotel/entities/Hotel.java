package hotel.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;

public class Hotel {
	
	private Map<Integer, Guest> guests;
	public Map<RoomType, Map<Integer,Room>> roomsByType;
	public Map<Long, Booking> bookingsByConfirmationNumber;
	public Map<Integer, Booking> activeBookingsByRoomId;
	
	
	public Hotel() {
		guests = new HashMap<>();
		roomsByType = new HashMap<>();
		for (RoomType rt : RoomType.values()) {
			Map<Integer, Room> rooms = new HashMap<>();
			roomsByType.put(rt, rooms);
		}
		bookingsByConfirmationNumber = new HashMap<>();
		activeBookingsByRoomId = new HashMap<>();
	}

	
	public void addRoom(RoomType roomType, int id) {
		IOUtils.trace("Hotel: addRoom");
		for (Map<Integer, Room> rooms : roomsByType.values()) {
			if (rooms.containsKey(id)) {
				throw new RuntimeException("Hotel: addRoom : room number already exists");
			}
		}
		Map<Integer, Room> rooms = roomsByType.get(roomType);
		Room room = new Room(id, roomType);
		rooms.put(id, room);
	}

	
	public boolean isRegistered(int phoneNumber) {
		return guests.containsKey(phoneNumber);
	}

	
	public Guest registerGuest(String name, String address, int phoneNumber) {
		if (guests.containsKey(phoneNumber)) {
			throw new RuntimeException("Phone number already registered");
		}
		Guest guest = new Guest(name, address, phoneNumber);
		guests.put(phoneNumber, guest);		
		return guest;
	}

	
	public Guest findGuestByPhoneNumber(int phoneNumber) {
		Guest guest = guests.get(phoneNumber);
		return guest;
	}

	
	public Booking findActiveBookingByRoomId(int roomId) {
		Booking booking = activeBookingsByRoomId.get(roomId);;
		return booking;
	}


	public Room findAvailableRoom(RoomType selectedRoomType, Date arrivalDate, int stayLength) {
		IOUtils.trace("Hotel: checkRoomAvailability");
		Map<Integer, Room> rooms = roomsByType.get(selectedRoomType);
		for (Room room : rooms.values()) {
			IOUtils.trace(String.format("Hotel: checking room: %d",room.getId()));
			if (room.isAvailable(arrivalDate, stayLength)) {
				return room;
			}			
		}
		return null;
	}

	
	public Booking findBookingByConfirmationNumber(long confirmationNumber) {
		return bookingsByConfirmationNumber.get(confirmationNumber);
	}

	// booking room and get a confirmation number
	public long book(Room room, Guest guest, Date arrivalDate, int stayLength, int occupantNumber, CreditCard creditCard) {
		// creating a Booking object by booking the room
		Booking bookingObj = room.book(guest, arrivalDate, stayLength, occupantNumber, creditCard);
		// get confirmation number for booking
    		long bookConfirmationNumber = bookingObj.getConfirmationNumber();
		// store booking number
    		bookingsByConfirmationNumber.put(Long.valueOf(bookConfirmationNumber), bookingObj);
    		return bookConfirmationNumber;		
	}

	// set state to CHECKED_IN and store booking in activeBookingsByRoomId
	public void checkin(long confirmationNumber) {
		// get booking object by using confirmation number
		Booking bookingObj = (Booking)bookingsByConfirmationNumber.get(Long.valueOf(confirmationNumber));
		// check a booking is exist for the given confirmation number. if not end function with throw a error massage
    		if (booking == null) {
      			String notFoundMassage = String.format("class Hotel, method checkin; can't find booking for given confirmation number %d", new Object[] { Long.valueOf(confirmationNumber) });
      			throw new RuntimeException(notFoundMassage);
    		}
    		int roomId = bookingObj.getRoomId();
    		// set sate of booking to CHECKED_IN
    		bookingObj.checkIn();
		// Store the booking in activeBookingsByRoomId (contains only bookings of acive rooms)
    		activeBookingsByRoomId.put(Integer.valueOf(roomId), bookingObj);
	}

	// add service charge for the booking
	public void addServiceCharge(int roomId, ServiceType serviceType, double cost) {
		// get booking using the room Id
		Booking bookingObj = (Booking)activeBookingsByRoomId.get(Integer.valueOf(roomId));
		// check booking is available for the given room Id. If not show massage
    		if (bookingObj == null) {
      			String notFoundMassage = String.format("class Hotel, method addServiceCharge; Can't find booking for given roomId  : %d", new Object[] { Integer.valueOf(roomId) });
      			throw new RuntimeException(notFoundMassage);
    		}
		// add service charge
    		bookingObj.addServiceCharge(serviceType, cost);
	}

	// relese the room after booking
	public void checkout(int roomId) {
		// get booking using the given roomId
		Booking booking = (Booking)activeBookingsByRoomId.get(Integer.valueOf(roomId));
		// check booking is available for the given room Id. If not show massage
    		if (booking == null) {
      			String mesg = String.format("class Hotel, method addServiceCharge; Can't find booking for given roomId  : %d", new Object[] { Integer.valueOf(roomId) });
      			throw new RuntimeException(mesg);
    		}
		// check out the booking
    		booking.checkOut();
		// romve booking form activeBookingsByRoomId
    		activeBookingsByRoomId.remove(Integer.valueOf(roomId));
	}


}
