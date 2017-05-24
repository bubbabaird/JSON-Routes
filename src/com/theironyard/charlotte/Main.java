// h2 database connection:
// http://localhost:8082/

// Display:
// http://localhost:4567/

package com.theironyard.charlotte;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void createTables(Connection conn) throws SQLException {
        //statements are used to execute sql
        Statement stmt = conn.createStatement();
        // stmt.execute - when you want the sql to run against the database, but you don't need anything to return, it's for changing the data:
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, address VARCHAR, email VARCHAR)");
    }

//    public static void insertMessage(Connection conn, String author, String text) throws SQLException {
//        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?)");
//        stmt.setString(1, author);
//        stmt.setString(2, text);
//        stmt.execute();
//    }
    public static void insertUser(Connection conn, String username, String address, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, address);
        stmt.setString(3, email);
        stmt.execute();
    }
    public static void updateUser(Connection conn, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET username = ?, address = ?, email = ? where id = ?"); //need to know how to write this sql
        stmt.setString(1, user.username);
        stmt.setString(2, user.address);
        stmt.setString(3, user.email);
        stmt.setInt(4, user.id);
        stmt.execute();
    }
    public static void deleteUser(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("delete from users where id = ?"); //need to know how to write this sql
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static ArrayList<User> selectUsers(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        // while the results has a next row, get these columns, now we have all of the things we need to build out user object.
        while (results.next()) {
            int id = results.getInt("id");
            String username = results.getString("username");
            String address = results.getString("address");
            String email = results.getString("email");
            // add it to our messages ArrayList
            users.add(new User(id, username, address, email));
        }
        // now return our messages ArrayList.
        return users;
    }

    //read the messages from the database over our connection
//    public static ArrayList<Message> selectMessages(Connection conn) throws SQLException {
//        ArrayList<Message> messages = new ArrayList<>();
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages");
//        ResultSet results = stmt.executeQuery();
//        // while the results has a next row, get the these columns, now we have all of the things we need to build out message object.
//        while (results.next()) {
//            int id = results.getInt("id");
//            String author = results.getString("author");
//            String text = results.getString("text");
//            // add it to our messages ArrayList
//            messages.add(new Message(id, author, text));
//        }
        // now return our messages ArrayList.
//        return messages;
//    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Spark.staticFileLocation("/public");
        Spark.init();
        // if you don't have any routes (get requests), you need spark init.
        // spark asks: do i have  a file at this location? if yes,
        // return file contents, if no, do i have a request mapping,
        // if yes, run lambda, if no, return 404.
        Connection conn = DriverManager.getConnection("jdbc:h2:./main"); //h2 is the driver
        createTables(conn);

        Spark.get(
                "/user",
                ((request, response) -> {
                    ArrayList<User> users = selectUsers(conn);
                    JsonSerializer t = new JsonSerializer();
                    return t.serialize(users);
                })
        );

//        Spark.get(
//                "/get-messages",
//                ((request, response) -> {
//                    ArrayList<Message> messages = selectMessages(conn);
//                    JsonSerializer s = new JsonSerializer();
//                    return s.serialize(messages);
//                })
//        );
//        Spark.post(
//                "/add-message",
//                ((request, response) -> {
//                    String body = request.body();
//                    JsonParser p = new JsonParser();
//                    Message msg = p.parse(body, Message.class);
//                    insertMessage(conn, msg.author, msg.text);
//                    return "";
//                })
//        );
        Spark.post(
                "/user",
                ((request, response) -> {

                    // when we receive a post request for a user
                    // the body of the request will contain the user data
                    // in json.
                    String body = request.body();

                    // we should convert that json into a user object
                    JsonParser p = new JsonParser();
                    User usrp = p.parse(body, User.class);

                    // once we have our user object, we should insert it.
                    insertUser(conn, usrp.username, usrp.address, usrp.email);
                    return "";
                })
        );

        Spark.put(
                "/user",
                ((request, response) -> {
                    String body = request.body();
                    JsonParser r = new JsonParser();
                    User usrr = r.parse(body, User.class);
                    updateUser(conn, usrr);
                    return "";
                })
        );

        Spark.delete(
                "/user/:id",
                ((request, response) -> {
                    int id = Integer.valueOf(request.params("id"));
                    deleteUser(conn, id);
                    return "";
                })
        );
    }
}