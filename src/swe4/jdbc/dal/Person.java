package swe4.jdbc.dal;

public class Person {
	private int id;
	private String firstName, lastName, address, phoneNumber;
	
	public Person(int id, String firstName, String lastName, String address, String phoneNumber) {
		setId(id);
		setFirstName(firstName);
		setLastName(lastName);
		setAddress(address);
		setPhoneNumber(phoneNumber);
	}	
	public Person(String firstName, String lastName, String address, String phoneNumber) {
		this(/*object not stored in Database ==*/-1, firstName, lastName, address, phoneNumber);
	}
	
	@Override
	public String toString() { return String.format("(%s) %s, %s: %s; %s", id, lastName, firstName, address, phoneNumber); }

	public int getId() { return id; }
	public void setId(int value) { id = value; }

	public String getFirstName() { return firstName; }
	public void setFirstName(String value) { firstName = value; }

	public String getLastName() { return lastName; }
	public void setLastName(String value) { lastName = value; }

	public String getAddress() { return address; }
	public void setAddress(String value) { address = value; }

	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(String value) { phoneNumber = value; }
}