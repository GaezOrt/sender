package emailSender;

import com.sun.mail.smtp.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.time.*;
import java.util.*;

import static javax.mail.Session.getInstance;

public class Fondo  {
    final Connect connect = new Connect();
    private static final String SMTP_SERVER = "Mailer.iorlarauz.com.ar";

    public enum ServerEnum { iOrl, gMail };

    private Chronometer chronometer;
    private List<Email> emails;
    private String messageToSend;
    private ServerEnum serverEnum;
    private String username = "";
    private String password = "";
    private List<Email> emailTo;
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
    
    public void setEmailTo(List<Email> newValue) {
        emailTo = newValue;
    }

    void setPassword(String v) {
        password = v;
    }

    void setMessageToSend(String v) {
        messageToSend = v;
    }

    void setUsername(String v) {
        username = v;
    }

    String getUsername() {
        return username;
    }

    int getPointer() {
        return pointer;
    }

     void setPointer(int pointer) {
        this.pointer = pointer;
    }

    int getMailsTotales() {
        return mailsTotales;
    }

    int getMailsEnviadosEnLaRonda() {
        return mailsEnviadosEnLaRonda;
    }

    int getErrors() {
        return errors;
    }

    Chronometer getTiempoTotal() {
        return tiempoTotal;
    }

    Chronometer getChronometer() {
        return chronometer;
    }

    Fondo() {
        chronometer = new Chronometer();
    }

    void sendEmails() {


            System.out.println("Empezando a mandar mails");

            tiempoTotal.start();
            mailsTotales = emailTo.size();
            amountOfEmailsInList = emailTo.size();

            while (pointer < emailTo.size()) {
                try {
                    if (mailsEnviadosEnLaRonda == 0) {
                        System.out.println("Empezando ronda");
                        chronometer.start();
                    }
                    if(emailTo.get(pointer).sent){
                       continue;
                    }
                    createSesion();
                    t = (SMTPTransport) session.getTransport("smtp");
                    System.out.println("Nuevo email " + emailTo.get(pointer).sent);
                    setEmailSenderAndReceivers();
                    setEmailSubjectAndBody();
                    sendEmailFinal();
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


                } catch (Exception e) {
                    emailTo.get(pointer).sent=false;
                    connect.insertIntoTable(emailTo.get(pointer));
                    errors++;
                    pointer++;

                    System.out.println(e.getMessage());
                    continue;
                    // FIXME
                }
            }

            if(errors>0){
                errors=0;
                pointer=0;
                sendEmails();
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

            if (serverEnum == ServerEnum.iOrl) {
                message.setFrom(new InternetAddress(username));

                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(emailTo.get(pointer).reciever, false));
            } else if (serverEnum == ServerEnum.gMail) {
                // Set From: header field of the header.
                message.setFrom(new InternetAddress(username));

                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo.get(pointer).reciever));

            }

    }

    private void setEmailSubjectAndBody() throws MessagingException {


            if (serverEnum == ServerEnum.iOrl) {
                message.setSubject("Guardia Otorrinolaringologica 24 hs");
                message.setContent(messageToSend, "text/html");
            }

            if (serverEnum == ServerEnum.gMail) {
                // Set Subject: header field
                message.setSubject("This is the Subject Line!");
                // Now set the actual message
                message.setText("This is actual message");
                System.out.println("Sending");
            }
    }

    private void sendEmailFinal() throws MessagingException {

            if (serverEnum == ServerEnum.iOrl) {
                t.connect(SMTP_SERVER, username, password);
                t.sendMessage(message, message.getAllRecipients());

                emailTo.get(pointer).sent = true;
                connect.createTable();
                connect.insertIntoTable(emailTo.get(pointer));
                System.out.println("Mail enviado    Mails enviados:" + (pointer + 1));
                mailsEnviadosEnLaRonda++;
                t.close();
            }
            if (serverEnum == ServerEnum.gMail) {
                Transport.send(message);
                System.out.println("Sent message");
            }
    }

}
