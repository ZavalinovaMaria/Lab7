package lab.dataBaseTools;

import lab.client.OnlineUser;
import lab.tools.Checking;
import lab.tools.subjects.Coordinates;
import lab.tools.subjects.Ticket;
import lab.tools.subjects.Venue;

import java.sql.*;

public class DatabaseProvider implements Checking {
    private final DatabaseConnection sqlConnection;
    private final OnlineUser onlineUser;
    public DatabaseProvider(OnlineUser onlineUser){
        this.onlineUser = onlineUser;
        this.sqlConnection = new DatabaseConnection();
    }
    public Integer generateNextId() {
        String query = "SELECT nextval('ticket_id_seq')";
        try (Connection connection = sqlConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {
            if (resultSet.next()) {
                addNewKey(resultSet.getInt(1));
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка");
        }
        return null;
    }



    public boolean canModifyProduct(Integer productId) {
        String query = "SELECT owner FROM Tickets WHERE id = ?";
        try (Connection connection = sqlConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, productId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String createdBy = resultSet.getString("owner");
                return createdBy.equals(onlineUser.getUserName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
    public boolean removeElementFromBase(Ticket ticket){
        String query = "DELETE FROM Tickets WHERE id = ? AND owner = ?";
        try (Connection connection = sqlConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ticket.getId());
            stmt.setString(2, onlineUser.getUserName());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;}
        } catch (SQLException e) {
            System.out.println("Ошибка");
        }
        return false;
    }
    public boolean addElementToBase(Ticket ticket){
        Integer nextId = generateNextId();
        if (nextId == null) return false ;

        int coordinatesId = ensureCoordinatesExist(ticket.getCoordinates());
        if (coordinatesId == -1) return false ;

        Integer venueId = null;
        if (ticket.getUser() != null) {
            venueId = ensureVenueExists((ticket.getVenue()));
            if (venueId == -1);
        }
        String query = "INSERT INTO Tickets (id, owner, name, coordinates, creationDate, price, discount, refundable, type, venue) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = sqlConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nextId);
            stmt.setString(2, onlineUser.getUserName());
            stmt.setString(3, ticket.getName());
            stmt.setInt(4, coordinatesId);
            stmt.setTimestamp(5, Timestamp.from(ticket.getCreationDate().toInstant()));
            stmt.setFloat(6, ticket.getPrice());
            stmt.setDouble(7, ticket.getDiscount());
            stmt.setBoolean(8, ticket.getRefundable());
            stmt.setString(9, ticket.getType().toString());
            stmt.setObject(10, venueId, java.sql.Types.INTEGER);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ticket.setId(nextId);
                ticket.setUser(onlineUser.getUserName());
                ticket.getVenue().setId(venueId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка");
        }
        return false;
    }
    public boolean updateElementInBase(Integer id, Ticket updatedticket) {
        int coordinatesId = ensureCoordinatesExist(updatedticket.getCoordinates());
        if (coordinatesId == -1) return false ;
        Integer venueId = null;
        if (updatedticket.getUser() != null) {
            venueId = ensureVenueExists((updatedticket.getVenue()));
            if (venueId == -1);
        }

        String query = "UPDATE Ticket SET name = ?, coordinates = ?,creationDate = ?, price = ?, discount = ?, refundable = ?, type = ?, venue = ? " +
                "WHERE id = ?";
        try (Connection connection = sqlConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, updatedticket.getName());
            stmt.setInt(2,  coordinatesId);
            stmt.setTimestamp(3, Timestamp.from(updatedticket.getCreationDate().toInstant()));
            stmt.setFloat(4, updatedticket.getPrice());
            stmt.setDouble(5, updatedticket.getDiscount());
            stmt.setBoolean(6, updatedticket.getRefundable());
            stmt.setString(7, updatedticket.getType().toString());
            stmt.setObject(8, venueId, java.sql.Types.INTEGER);
            stmt.setLong(9, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                updatedticket.getVenue().setId(venueId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка");
        }
        return false;
    }
    private int ensureCoordinatesExist(Coordinates coordinates) {
        String selectQuery = "SELECT id FROM Coordinates WHERE x = ? AND y = ?";
        String insertQuery = "INSERT INTO Coordinates (x, y) VALUES (?, ?) RETURNING id";
//ТИПЫ ПРОВЕРИТЬ
        try (Connection connection = sqlConnection.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setFloat(1, coordinates.getX());
            selectStmt.setFloat(2, coordinates.getY());

            ResultSet resultSet = selectStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setFloat(1, coordinates.getX());
                    insertStmt.setFloat(2, coordinates.getY());
                    ResultSet insertResultSet = insertStmt.executeQuery();
                    if (insertResultSet.next()) {
                        return insertResultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка");
        }
        return -1;
    }
    private int ensureVenueExists(Venue venue) {
        String selectQuery = "SELECT id FROM Venue WHERE name = ? AND capacity = ? AND type = ? ";
        String insertQuery = "INSERT INTO Venue (name,capacity,type) VALUES (?, ?, ?) RETURNING id";

        try (Connection connection = sqlConnection.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, venue.getName());
            selectStmt.setFloat(2, venue.getCapacity());
            selectStmt.setString(3, venue.getType().name());

            ResultSet resultSet = selectStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, venue.getName());
                    insertStmt.setFloat(2, venue.getCapacity());
                    insertStmt.setString(3,venue.getType().name());
                    ResultSet insertResultSet = insertStmt.executeQuery();
                    if (insertResultSet.next()) {
                        return insertResultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка");
        }
        return -1;
    }
/*
    public void saveDatabase() {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write("Key,Id,Name,Coordinates,CreationDate,Price,PartNumber,ManufactureCost,UnitOfMeasure,Owner\n");
            for (Map.Entry<Integer, Product> entry : dataBase.entrySet()) {
                Integer key = entry.getKey();
                Product product = entry.getValue();
                writer.write(key + "," + product.toCSV() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error while writing to file: " + e.getMessage());
        }
    }

 */


}

