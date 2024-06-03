package lab.dataBaseTools;

import lab.tools.TicketFactory;
import lab.tools.subjects.Ticket;

import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Hashtable;

public class DatabaseReader {
    private  final DatabaseConnection sqlConnection;
    public String[] builder = new String[14];

    public DatabaseReader() {
        this.sqlConnection = new DatabaseConnection();
    }
    public Hashtable<Integer, Ticket> read() {
        Hashtable<Integer, Ticket> tickets = new Hashtable<>();
        String query = "SELECT t.id, t.owner, t.name, c.x, c.y, t.creationDate, "+
                "t.price, t.discount,t.refundable, t.type AS typeString, v.id AS idVenue, v.name AS nameVenue, v.capacity, v.type AS typeVenueString " +
                "FROM Tickets t "+
                "LEFT JOIN Coordinates c ON t.coordinates = c.id " +
                "LEFT JOIN Venue v ON t.venue = v.id;";

        try (Connection connection = sqlConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                builder[0] = resultSet.getString("owner");
                builder[1] = resultSet.getString("x");
                builder[2] = resultSet.getString("y");
                builder[3] = resultSet.getString("idVenue");
                builder[4] = resultSet.getString("nameVenue");
                builder[5] = resultSet.getString("capacity");
                builder[6] = resultSet.getString("typeVenueString");
                builder[7] = resultSet.getString("id");
                builder[8] = resultSet.getString("name");
                Timestamp timestamp = resultSet.getTimestamp("creationDate");
                ZonedDateTime creationDate = convertToZonedDateTime(timestamp, ZoneId.systemDefault());
                builder[9] = creationDate.toString();
                builder[10] = resultSet.getString("price");
                builder[11] = resultSet.getString("discount");
                builder[12] = resultSet.getString("refundable");
                builder[13] = resultSet.getString("typeString");
                tickets.put(TicketFactory.createTicket(builder).getId(),TicketFactory.createTicket(builder));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }
    public static ZonedDateTime convertToZonedDateTime(Timestamp timestamp, ZoneId zoneId) {
        Instant instant = timestamp.toInstant();
        return ZonedDateTime.ofInstant(instant, zoneId);
    }
}








