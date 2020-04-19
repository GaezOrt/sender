package emailSender;

import com.sun.mail.smtp.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.time.*;
import java.util.*;

import static javax.mail.Session.getInstance;

public class Fondo  {

    private static final String SMTP_SERVER = "Mailer.iorlarauz.com.ar";

    public enum ServerEnum { iOrl, gMail };

    private Chronometer chronometer;
    private List<String> emails;
    private String messageToSend;
    private ServerEnum serverEnum;
    private String username = "";
    private String password = "";
    private List<String> emailTo;
    private int pointer;
    private int mailsTotales;
    private int mailsEnviadosEnLaRonda;
    private int MaxMailsPerHour = 0;
    private String status;
    private int errors;
    private Chronometer tiempoTotal = new Chronometer();
    private Session session;
    private Message message;
    private SMTPTransport t;

    public void setServerEnum(ServerEnum serverEnum) {
        this.serverEnum = serverEnum;
    }

    private int amountOfEmailsInList;

    public void setMaxMailsPerHour(int v) {
        MaxMailsPerHour = v;
    }

    public void setStatus(String v) {
        status = v;
    }

    public String getStatus() {
        return status;
    }
    
    public void setEmailTo(List<String> newValue) {
        emailTo = newValue;
    }

    void setPassword(String v) {
        password = v;
    }

    public void setMessageToSend(String v) {
        messageToSend = v;
    }

    public void setUsername(String v) {
        username = v;
    }

    public String getUsername() {
        return username;
    }

    public int getPointer() {
        return pointer;
    }

    public int getMailsTotales() {
        return mailsTotales;
    }

    public int getMailsEnviadosEnLaRonda() {
        return mailsEnviadosEnLaRonda;
    }

    public int getErrors() {
        return errors;
    }

    public Chronometer getTiempoTotal() {
        return tiempoTotal;
    }

    public Chronometer getChronometer() {
        return chronometer;
    }

    public Fondo() {
        chronometer = new Chronometer();
    }

    public void sendEmails() {

        try {
            System.out.println("Empezando a mandar mails");

            tiempoTotal.start();
            mailsTotales = emailTo.size();
            amountOfEmailsInList = emailTo.size();

            System.out.println(serverEnum);

            while (pointer < emailTo.size()) {

                if (mailsEnviadosEnLaRonda == 0) {
                    System.out.println("Empezando ronda");
                    chronometer.start();
                }

                createSesion();
                t = null;

                try {
                    t = (SMTPTransport) session.getTransport("smtp");
                    System.out.println("Nuevo email " + emailTo.get(pointer));
                    setEmailSenderAndReceivers();
                    System.out.println("1");
                } catch (MessagingException a) {
                    pointer++;
                    errors++;
                    a.printStackTrace();
                    continue;
                }

                try {
                    System.out.println("0");
                    setEmailSubjectAndBody();
                    System.out.println("2");
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

                if (emailTo.size() == pointer) {
                    status = "Terminado";
                    break;
                }
                pointer++;
            }

        } catch (Exception e) {
            errors++;
            pointer++;
            System.out.println(e.getMessage());
            // FIXME
        }
    }

    private void createSesion() {
        if( serverEnum == ServerEnum.iOrl ) {
            Properties prop = System.getProperties();
            prop.put("mail.smtp.auth", "false");
            prop.put("mail.smtp.host", SMTP_SERVER);

            session = getInstance(prop, null);
            message = new MimeMessage(session);
            System.out.println("Creating session");
        }
        else if( serverEnum == ServerEnum.gMail ) {
            // Setup mail server
            System.out.println("Starting");
            Properties prop = System.getProperties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "465");
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.auth", "true");

            // Get the Session object.// and pass username and password
            session = getInstance(prop, new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
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
        if( serverEnum == ServerEnum.iOrl ) {
            message.setFrom(new InternetAddress(username));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo.get(pointer), false));
        }
        else if( serverEnum == ServerEnum.gMail ) {
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(username));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo.get(pointer)));

        }
    }

    private void setEmailSubjectAndBody() throws MessagingException {

        if( serverEnum == ServerEnum.iOrl ) {
            message.setSubject("Guardia Otorrinolaringologica 24 hs");
            message.setContent(messageToSend,"text/html");
        }

        if( serverEnum == ServerEnum.gMail ) {
            // Set Subject: header field
            message.setSubject("This is the Subject Line!");
            // Now set the actual message
            message.setText("This is actual message");
            System.out.println("Sending");
        }
    }

    private void sendEmailFinal() throws MessagingException {
        if( serverEnum == ServerEnum.iOrl ) {
            t.connect(SMTP_SERVER, username, password);
            t.sendMessage(message, message.getAllRecipients());
            System.out.println("Mail enviado    Mails enviados:" + (pointer + 1));
            mailsEnviadosEnLaRonda++;
            t.close();
        }
        if( serverEnum == ServerEnum.gMail ) {
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
            // FIXME init();
        } else if (exception.contains("Quota")) {
            System.out.println("Quota exced");
            status = "Esperando por limite de mails por hora";
            Thread.sleep(3600000 - chronometer.getElapsedTime());
        } else {
            System.out.println("Invalid email");
            errors++;
            pointer++;
        }
    }
}
