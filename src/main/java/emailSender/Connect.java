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
    static String getCSVFilePath;
    static String whereToAddTheDatabase;
    static List<Email> emails = new ArrayList<>();

    public static void main(String[] args) {
        loadEmailsFromCsv();
        insertemailsToDatabase();
         }

    static void createTable() {
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
        try {
            System.out.println(whereToAddTheDatabase);
            con = DriverManager.getConnection(whereToAddTheDatabase);
            Statement stmt = con.createStatement();
            stmt.execute(sql);
            System.out.println("Created table");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void loadEmailsFromCsv() {


        try (BufferedReader br = Files.newBufferedReader(Paths.get(getCSVFilePath), StandardCharsets.ISO_8859_1)) {
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");
                Email email = new Email(attributes[0], false);
                emails.add(email);
                line = br.readLine();
                System.out.println(email.reciever);
            }
            System.out.println("Amount of emailss on loading emails" + emails.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Emails size " + emails.size());
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

    static void insertemailsToDatabase() {
        for (Email email : emails) {
            try {
                PreparedStatement stmt = con.prepareStatement("INSERT INTO email VALUES (?,?)");

                stmt.setString(1, email.reciever);
                stmt.setBoolean(2, false);
                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }


    boolean emailSent(Email email) {
        try {
            //con = DriverManager.getConnection(URL_ADDRESS);
            PreparedStatement stmt = con.prepareStatement("SELECT * from email WHERE id = ?");
            stmt.setString(1, email.reciever);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                return rs.getBoolean(2);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    void updateDatabase(Email email) {
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE  email SET name = ? WHERE id= ?");
            stmt.setString(2, email.reciever);
            stmt.setBoolean(1, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
