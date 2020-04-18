package emailSender;


import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.time.Instant;
import java.util.*;

public class Main extends SwingWorker<Void, Void> {

    static Chronometer chronometer = new Chronometer();
    static List<String> emails;
    static ArrayList<String> emailsFallidos = new ArrayList<>();
    private static final String SMTP_SERVER = "Mailer.iorlarauz.com.ar";
    private static boolean gmail;


    public static void setMessageToSend(String messageToSend) {
        Main.messageToSend = messageToSend;
    }

    private static String messageToSend;
    public static boolean isGmail() {
        return gmail;
    }

    public static void setGmail(boolean gmail) {
        Main.gmail = gmail;
    }

    public static boolean isIorl() {
        return iorl;
    }

    public static void setIorl(boolean iorl) {
        Main.iorl = iorl;
    }

    private static boolean iorl;

    public static String getUSERNAME() {
        return USERNAME;
    }
    static void setUSERNAME(String USERNAME) {
        Main.USERNAME = USERNAME;
    }

    private static String USERNAME = "";

    static void setPASSWORD(String PASSWORD) {
        Main.PASSWORD = PASSWORD;
    }
    public static String getPASSWORD() {
        return PASSWORD;
    }

    private static String PASSWORD = "";


    static void setEMAIL_TO(String EMAIL_TO) {
        Main.EMAIL_TO = EMAIL_TO;
    }

    private static String EMAIL_TO = "gaston7eze@yahoo.com,gaezort@gmail.com";


    private static final String EMAIL_SUBJECT = "Guardia Otorrinolaringologica 24 hs";

    private static int amountOfEmailsInList;
    static int pointer;
    static int mailsTotales;
    static int mailsEnviadosEnLaRonda;
    static int MaxMailsPerHour = 0;

    public static void setStatus(String status) {
        Main.status = status;
    }

    public static String getStatus() {
        return status;
    }

    static String status;

    static int errores;

    static Chronometer tiempoTotal = new Chronometer();

    private Session session;
    private Message message;
    private SMTPTransport t;

    public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {


        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Interface gui = new Interface();
                gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            }
        });

    }

    @Override
    protected Void doInBackground() throws Exception {
        init();
        return null;
    }


    /**
     * Send emails to Iorl
     */
    private void init() {

        try {
            tiempoTotal.start();
            System.out.println("Empezando a mandar mails");

            getListFromTextField();

            mailsTotales = emails.size();
            amountOfEmailsInList = emails.size();

            System.out.println(isIorl());
            while (pointer < emails.size()) {

                if (mailsEnviadosEnLaRonda == 0) {
                    System.out.println("Empezando ronda");
                    chronometer.start();
                }

                createSesion();
                t = null;
                try {
                    t = (SMTPTransport) session.getTransport("smtp");

                } catch (NoSuchProviderException n) {
                    pointer++;
                    errores++;
                    n.printStackTrace();
                    continue;
                }
                try {
                    System.out.println("Nuevo email " + emails.get(pointer));
                    setEmailSenderAndReceivers();

                } catch (MessagingException a) {
                    pointer++;
                    errores++;
                    a.printStackTrace();
                    continue;
                }
                try {
                    setEmailSubjectAndBody();
                    sendEmailFinal();
                } catch (NullPointerException | MessagingException r) {

                    //  r.printStackTrace();
                    recieveException(r.getMessage());
                    continue;
                }
                status = "Enviando mails";
                if (mailsEnviadosEnLaRonda == MaxMailsPerHour) {
                    resetEverythingBasedOnEmailsSent();
                }
                if (chronometer.getElapsedTime() > 3600000) {
                    resetEverythingBasedOnChronometer();
                }

                if (emails.size() == pointer) {
                    status = "Terminado";
                    break;
                }
                pointer++;
            }

        } catch (Exception e) {

            errores++;
            pointer++;
            System.out.println(e.getMessage());
            init();
        }

    }
    private void getListFromTextField(){
        emails = Arrays.asList(EMAIL_TO.split("\\s*;\\s*"));
    }
    private void createSesion() {
        if(isIorl()) {
            Properties prop = System.getProperties();
            prop.put("mail.smtp.auth", "false");
            prop.put("mail.smtp.host", SMTP_SERVER);

            session = Session.getInstance(prop, null);
            message = new MimeMessage(session);
            System.out.println("Creating session");
        }
        if(isGmail()){
            // Setup mail server
            System.out.println("Starting");
            Properties prop = System.getProperties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "465");
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.auth", "true");

            // Get the Session object.// and pass username and password
            session = Session.getInstance(prop, new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(USERNAME, PASSWORD);

                }

            });
            message = new MimeMessage(session);
            System.out.println("Creating session");
        }
    }

    /**
     * Reset everything based on chronometer
     */
    private void resetEverythingBasedOnChronometer() {
        System.out.println("Resetting  chronometer");
        chronometer.resetMeanTime();
        mailsEnviadosEnLaRonda = 0;
    }


    /**
     * @throws InterruptedException
     * Reset based on amount of emails sent
     */
    private void resetEverythingBasedOnEmailsSent() throws InterruptedException {
        status = "En espera desde " + Date.from(Instant.now());
        System.out.println(Date.from(Instant.now()));
        Thread.sleep(3600000 - chronometer.getElapsedTime());
        System.out.println("Starting again");
        chronometer.resetMeanTime();
        mailsEnviadosEnLaRonda = 0;
    }

    /**
     * @throws MessagingException
     * Set senderAndReciever
     */
    private void setEmailSenderAndReceivers() throws MessagingException {
        if (isIorl()) {
            message.setFrom(new InternetAddress(USERNAME));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emails.get(pointer), false));
        }
        if(isGmail()){
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(USERNAME));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emails.get(pointer)));

        }
    }

    /**
     * @throws MessagingException
     * sets email subject and body
     */
    private void setEmailSubjectAndBody() throws MessagingException {
        if (isIorl()) {
            message.setSubject(EMAIL_SUBJECT);

           /* message.setContent(""
                            + "Instituto Otorrinolaringologico Arauz  Tel.  011 22061450  <img emailSender=\"http://www.farauzorl.org.ar/imageniorl.jpeg\">" +
                            "Atencion 24 hs ",

                    "text/html");
       */
            message.setContent(messageToSend,"text/html");
        }
        if(isGmail()){

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
            message.setText("This is actual message");
            System.out.println("Sending");
        }
    }
    private void sendEmailFinal() throws MessagingException {
        if(isIorl()) {
            t.connect(SMTP_SERVER, USERNAME, PASSWORD);
            t.sendMessage(message, message.getAllRecipients());
            System.out.println("Mail enviado    Mails enviados:" + (pointer + 1));
            mailsEnviadosEnLaRonda++;
            t.close();
        }
        if(isGmail()){

            Transport.send(message);
            System.out.println("Sent message");;
        }
    }

    /**
     * @param exception
     * @throws InterruptedException recieves exception and acts based on it
     */
    private void recieveException(String exception) throws InterruptedException {
        System.out.println(exception);
        if (exception.equals("Exception reading response") || exception.contains("Couldn't connect to host")) {
            status = "Error de conexion,esperando 5 minutos antes de tratar de nuevo";
            Thread.sleep(20000);
            init();
        } else if (exception.contains("Quota")) {
            System.out.println("Quota exced");
            status = "Esperando por limite de mails por hora";
            Thread.sleep(3600000 - chronometer.getElapsedTime());
        } else {
            System.out.println("Invalid email");
            errores++;
            pointer++;
        }

    }
}
