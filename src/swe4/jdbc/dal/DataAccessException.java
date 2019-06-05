package swe4.jdbc.dal;

@SuppressWarnings("serial")
public class DataAccessException extends Exception {
	public DataAccessException(String msg) { super(msg); }
}