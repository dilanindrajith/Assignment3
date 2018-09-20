package hotel.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hotel.credit.CreditCard;
import hotel.utils.IOUtils;
//Dilan's Entity class
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

//implementation of the public long book
	public long book(Room room, Guest guest,
			Date arrivalDate, int stayLength, int occupantNumber,
			CreditCard creditCard) {
			Booking booking = room.book(guest, arrivalDate, stayLength, occupantNumber, creditCard); //access room entity class's implemented code by other grouo member
			long confirmationNumber = booking.getConfirmationNumber(); //get returned value of the long variable
			bookingsByConfirmationNumber.put(Long.valueOf(confirmationNumber), booking); //add values to hash map

			return confirmationNumber; //finaly return the confirmation number
	}


	public void checkin(long confirmationNumber)
  {
    Booking booking = (Booking)bookingsByConfirmationNumber.get(Long.valueOf(confirmationNumber));
    if (booking == null) { //validation of booking confirmationNumber
      String message = String.format("Hotel: checkin: No booking details have been found for confirmation number %d", new Object[] { Long.valueOf(confirmationNumber) });
      throw new RuntimeException(message); //exception handling using throw clause
    }
    int roomId = booking.getRoomId(); //get correspondence room id

    booking.checkIn(); //call checking method of booking class
    activeBookingsByRoomId.put(Integer.valueOf(roomId), booking); //add values to hashmap
  }


	public void addServiceCharge(int roomId, ServiceType serviceType, double cost)
  {
    Booking booking = (Booking)activeBookingsByRoomId.get(Integer.valueOf(roomId));
    if (booking == null) { //validation of room id
      String mesg = String.format("Hotel: addServiceCharge: no booking present for room id : %d", new Object[] { Integer.valueOf(roomId) });
      throw new RuntimeException(mesg); //exception handling using throw clause
    }
    booking.addServiceCharge(serviceType, cost); //call addServiceCharge method for calculate service charge
  }


	public void checkout(int roomId)
  {
    Booking booking = (Booking)activeBookingsByRoomId.get(Integer.valueOf(roomId));
    if (booking == null) { //validate for a valid room id
      String mesg = String.format("Hotel: checkout: no booking present for room id : %d", new Object[] { Integer.valueOf(roomId) });
      throw new RuntimeException(mesg); //handle exception
    }
    booking.checkOut(); //call checkOut() method
    activeBookingsByRoomId.remove(Integer.valueOf(roomId)); //make those checkout rooms available by removing them  activeBookingsByRoomId hashmap
  }



}
