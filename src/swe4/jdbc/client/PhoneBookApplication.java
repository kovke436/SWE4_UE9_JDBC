package swe4.jdbc.client;

import static java.lang.System.out;
import static java.lang.System.err;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import swe4.jdbc.dal.DataAccessException;
import swe4.jdbc.dal.Person;
import swe4.jdbc.dal.PersonDao;
import swe4.jdbc.dal.PersonDaoJdbc;

public class PhoneBookApplication {
	private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	private static final Map<String, Function> commands = createCommands();
	
	private static final String SERVER = "0.0.0.0";//TODO can be queried from docker via: "docker-machine ip"
	private static final String USERNAME = "root";
	private static final String PASSWORD = null;
	private static final String CONNECTION_STRING = "jdbc:mysql://" + SERVER + "/PhoneBookDb?autoReconnect=true&useSSL=false";

	private interface Function {
		void call(PersonDao dao) throws Exception;
	}

	private static String promptFor(String p) {
		out.print(p + "> ");

		try { return in.readLine(); }
		catch(Exception exc) { return promptFor(p); }
	}	
	
	private static Map<String, Function> createCommands() {
		Map<String, Function> result = new HashMap<>();
		result.put("insert", (dao) -> { insert(dao);     });
		result.put("list",   (dao) -> { list(dao);       });
		result.put("find",   (dao) -> { find(dao);       });
		result.put("update", (dao) -> { update(dao);     });
		result.put("delete", (dao) -> { delete(dao);     });
		result.put("quit",   (dao) -> { /*never called*/ });
		return result;
	}

	private static void insert(PersonDao dao) throws DataAccessException {
		Person person = new Person(
			promptFor("  first name "),
			promptFor("  last name "),
			promptFor("  address "),
			promptFor("  phone number ")	
		);
		dao.store(person);
		out.printf("inserted new person %s%n", person);
	}
	
	private static void list(PersonDao dao) throws DataAccessException {
		for(Person person : dao.getAll())
			out.println(person);
	}
	
	private static void find(PersonDao dao) throws DataAccessException {
		String lastName = promptFor("  last name ");
		Collection<Person> persons = dao.getByLastName(lastName);
		if(persons.isEmpty()) out.printf("  no entries with last name '%s' found%n", lastName);
		else for(Person person : persons) out.println(person);
	}
	
	private static void update(PersonDao dao) throws DataAccessException {
		int id = Integer.parseInt(promptFor("  id "));
		Person person = dao.getById(id);
		if(person == null) out.println("  no entry with id " + id);
		else {
			out.printf("  %s%n", person);
			person.setAddress(promptFor("  new address "));
			dao.update(person);
		}
	}
	
	private static void delete(PersonDao dao) throws NumberFormatException, DataAccessException {
		dao.delete(Integer.parseInt(promptFor("  id ")));
	}
	
	public static void main(String args[]) {
		try(PersonDao personDao = new PersonDaoJdbc(CONNECTION_STRING, USERNAME, PASSWORD)) {
			out.printf("%ncurrently %d entries in phone book%n", personDao.getCount());			
			out.printf("%ncommands: %s%n%n", commands.keySet());
			for(String cmd = promptFor(""); !cmd.equals("quit"); cmd = promptFor("")) {
				Function func = commands.get(cmd.trim());
				if(func == null) out.printf("ERROR: invalid command; commands: %s%n", commands.keySet());
				else try { func.call(personDao); }
				     catch(Exception exc) { err.printf("ERROR: %s%n", exc.getMessage()); }
			}		
		} catch(Exception exc) { exc.printStackTrace(); }
	}
}
