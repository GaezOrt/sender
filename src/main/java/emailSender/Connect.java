package emailSender;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Connect {

    static Connection con;
    static String whereToAddTheDatabase = "jdbc:sqlite:xyz.db";

    public static void main(String[] args) {
        try {
            createTable();
            loadEmailsFromCsvAndInsertemailsToDatabase("1.csv");
        }
        catch (Exception ex ) {
            ex.printStackTrace();
        }
    }

    static void createTable() throws SQLException {
        File file = new File(whereToAddTheDatabase);
        if (file.mkdir()) {
            System.out.println("Se creo directorio");
        } else {
            System.out.println("No se creo directorio");
        }
        String sql = "CREATE TABLE IF NOT EXISTS email (\n"
                + "	id text,\n"
                + "	name boolean NOT NULL\n"
                + ");";

        System.out.println(whereToAddTheDatabase);
        con = DriverManager.getConnection(whereToAddTheDatabase);
        Statement stmt = con.createStatement();
        stmt.execute(sql);
        System.out.println("Created table");
        stmt.close();
    }

    static void loadEmailsFromCsvAndInsertemailsToDatabase(String getCSVFilePath) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(getCSVFilePath), StandardCharsets.ISO_8859_1)) {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO email VALUES (?,?)");
            String line;
            while ( ( line = br.readLine() ) != null) {
                stmt.setString(1, line.trim() );
                stmt.setBoolean(2, false);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int getAmountOfEmails() {
        try {
            // con = DriverManager.getConnection(URL_ADDRESS);
            PreparedStatement stmt = con.prepareStatement("SELECT * from email");
            ResultSet rs = stmt.executeQuery();
            int counter = 0;
            while (rs.next()) {
                counter++;
            }
            System.out.println("Largo de base de datos" + counter);
            return counter;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
