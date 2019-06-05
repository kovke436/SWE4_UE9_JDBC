package swe4.jdbc.dal;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PersonDaoJdbc implements PersonDao {
    private Connection connection;
    private String connectionString, userName, password;

    public PersonDaoJdbc(String connectionString, String userName, String password) {
        this.connectionString = connectionString;
        this.userName = userName;
        this.password = password;
    }

    private Connection getConnection() throws DataAccessException {
        if (connection == null)
            try {
                connection = DriverManager.getConnection(connectionString, userName, password);
            } catch (SQLException exc) {
                throw new DataAccessException("Can't establish connection to database. SQLException: " + exc.getMessage());
            }
        return connection;
    }

    private List<Person> getWhere(String query, Object... objects) throws DataAccessException {
        try (PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM Person " + query + ";"))
        {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
            List<Person> result = new ArrayList<>();

            try(ResultSet resultSet = statement.executeQuery()){
                while (statement.getResultSet().next())
                    result.add(new Person(resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("address"),
                            resultSet.getString("phone_number"))
                    );
            } catch (SQLException exc) {
                throw new DataAccessException("SQLException: " + exc.getMessage());
            }

            return result;
        } catch (SQLException exc) {
            throw new DataAccessException("SQLException: " + exc.getMessage());
        }
    }


    @Override
    public void close() throws DataAccessException {
        if (connection != null) try {
            connection.close();
            connection = null;
        } catch (SQLException exc) {
            throw new DataAccessException("SQLException: " + exc.getMessage());
        }
    }

    @Override
    public int getCount() throws DataAccessException {
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM Person;")) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException exc) {
            throw new DataAccessException("SQLException: " + exc.getMessage());
        }

    }

    @Override
    public Person getById(int id) throws DataAccessException {

        List<Person> result = this.getWhere(" id = ?", id);
        return result.isEmpty() ? null : result.get(0);

    }

    @Override
    public Collection<Person> getByLastName(String lastName) throws DataAccessException {
        return getWhere("WHERE last_name LIKE ?", lastName);
    }

    @Override
    public Collection<Person> getAll() throws DataAccessException {
        return getWhere("");
    }

    @Override
    public void store(Person person) throws DataAccessException {
        if (person.getId() != -1) throw new DataAccessException("Object cant be stored twice!");

        try (Statement statement = getConnection().createStatement()) {
            //Insert
            statement.executeUpdate(
                    String.format("INSERT INTO Person (first_name, last_name, address, phone_number) VALUES ('%s', '%s', '%s', '%s');",
                            person.getFirstName(),
                            person.getLastName(),
                            person.getAddress(),
                            person.getPhoneNumber()),
                    Statement.RETURN_GENERATED_KEYS);

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet != null && resultSet.next()) person.setId(resultSet.getInt(1));
                else throw new DataAccessException("Auto-generated keys are not supported!");
            }

        } catch (SQLException exc) {
            throw new DataAccessException("SQLException: " + exc.getMessage());
        }
        // TODO
    }

    @Override
    public void delete(int id) throws DataAccessException {
        try (PreparedStatement statement = getConnection().prepareStatement("DELETE FROM Person WHERE id = ?;"))
        {
            statement.setInt(1, id);
            int count = statement.executeUpdate();
            System.out.printf("(%d) Datensätze gelöscht!\n", count);
        } catch (SQLException exc) {
            throw new DataAccessException("SQLException: " + exc.getMessage());
        }
    }

    @Override
    public void update(Person person) throws DataAccessException {
        if(person.getId() == -1) throw new DataAccessException("Can't update non-existant entry.");
        try(PreparedStatement statement = getConnection().prepareStatement(
                "UPDATE Person SET first_name = ?, last_name = ?, address = ?, phone_number = ? WHERE id = ?;")) {
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setString(3, person.getAddress());
            statement.setString(4, person.getPhoneNumber());
            statement.setInt(5, person.getId());
            statement.executeUpdate();
        }
        catch(SQLException e) {
            throw new DataAccessException("SQLException: " + e.getMessage());
        }
    }
}