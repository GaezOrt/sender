package emailSender;


import java.io.File;
import java.sql.*;

public class Connect {

    static Connection con;
    static String URL_ADDRESS = "jdbc:sqlite:C:/sqlite/db/email.db";

    public void createTable() {
        File file = new File("C:/sqlite/db");
        if (file.mkdir()) {
            System.out.println("Se creo directorio");
        } else {
            System.out.println("No se creo directorio");
        }
        String sql = "CREATE TABLE IF NOT EXISTS email (\n"
                + "	id text PRIMARY KEY,\n"
                + "	name boolean NOT NULL\n"
                + ");";
        try {
            con = DriverManager.getConnection(URL_ADDRESS);
            Statement stmt = con.createStatement();
            stmt.execute(sql);
            System.out.println("Created table");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertIntoTable(Email email) {

        try {
            con = DriverManager.getConnection(URL_ADDRESS);
            PreparedStatement stmt = con.prepareStatement("INSERT INTO email VALUES (?,?)");
            stmt.setString(1, email.reciever);
            stmt.setBoolean(2, email.sent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
