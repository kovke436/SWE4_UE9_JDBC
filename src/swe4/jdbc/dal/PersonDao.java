package swe4.jdbc.dal;

import java.util.Collection;

// DAO interface for accessing Person table
public interface PersonDao extends AutoCloseable {
	int getCount() throws DataAccessException;
	Person getById(int id) throws DataAccessException;
	Collection<Person> getByLastName(String lastName) throws DataAccessException;
	Collection<Person> getAll() throws DataAccessException;
	void store(Person person) throws DataAccessException;
	void delete(int id) throws DataAccessException;
	void update(Person person) throws DataAccessException;
}