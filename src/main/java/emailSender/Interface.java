package emailSender;

import javax.swing.*;
import javax.swing.Timer;
import java.util.*;

public class Interface  {

    private JTextField emailDeEnviadorTextField;
    private JTextField contraseniaDelEnviadorTextField;
    private JTextArea emailsAenviar;
    private JButton enviarButton;
    private JPanel panell;
    private JTextField cantMaxima;
    private JLabel TimeElapsedRound;
    private JLabel timesPerRound;
    private JLabel timeToWaitUntilFinished;
    private JLabel MailsTotales;
    private JLabel erroresTotal;
    private JLabel tiempoTotal;
    private JLabel statuss;
    private JLabel totales;
    private JTextArea textArea1;
    private JButton agregarFotoButton;
    private JFrame jframe;
    private JLabel enviandoMailA;
    private JPanel infoPanel;

    public static void main(String args[]) throws Exception {
        new Interface().init();
    }

    private Interface()  {
        jframe = new JFrame();
    }

    private void init() throws Exception  {

        jframe.setVisible(true);
        jframe.setContentPane(panell);
        jframe.setSize(1000, 1000);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final Fondo fondo = new Fondo();

        final Timer timer = new Timer(500,
            x -> {
                try {
                    TimeElapsedRound.setText("Tiempo pasado en la ronda: " + fondo.getChronometer().getElapsedTime() / 1000 / 60 + " : " + fondo.getChronometer().getElapsedTime() / 1000 % 60);
                    timesPerRound.setText("Mails mandados en la ronda: " + fondo.getMailsEnviadosEnLaRonda());
                    MailsTotales.setText("Mails totales enviados : " + fondo.getPointer());
                    timeToWaitUntilFinished.setText(" Tiempo a esperar si se termina antes: " + ((3600000 - fondo.getChronometer().getElapsedTime()) / 60 / 1000) + " : " + (3600000 - (fondo.getChronometer().getElapsedTime()) / 1000) % 60);
                    erroresTotal.setText("Errores en envio: " + fondo.getErrors());
                    tiempoTotal.setText("Tiempo total corriendo: " + fondo.getTiempoTotal().getElapsedTime() / 60 / 1000 + " : " + (fondo.getTiempoTotal().getElapsedTime() / 1000) % 60);
                    statuss.setText("Estado: " + fondo.getStatus());
                    totales.setText("Cantidad de mails a enviar: " + fondo.getMailsTotales());
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        );

        timer.setInitialDelay(0);
        timer.start();
        enviarButton.addActionListener(
            e -> {

            try {
                fondo.setPointer(0);
                fondo.setStatus("Enviando mails");
                System.out.println("Executing");
                timer.start();
                fondo.setMaxMailsPerHour(Integer.parseInt(cantMaxima.getText()));
                final List<String> x = Arrays.asList(emailsAenviar.getText().split("\\s*;\\s*"));
                final ArrayList<Email>emails= new ArrayList<>();
                for(int i=0;x.size()>i;i++){
                    Email email= new Email(x.get(i),false);
                        emails.add(email);
                }
                fondo.setEmailTo(emails);
                fondo.setUsername(emailDeEnviadorTextField.getText());
                fondo.setPassword(contraseniaDelEnviadorTextField.getText());
                fondo.setMessageToSend(textArea1.getText());
                if (fondo.getUsername().contains("iorl")) {
                    System.out.println("Setting iorl");
                    fondo.setServerEnum(Fondo.ServerEnum.iOrl);
                }
                if (fondo.getUsername().contains("gmail")) {
                    System.out.println("Gmail");
                    fondo.setServerEnum(Fondo.ServerEnum.gMail);
                }
                SwingWorker sw= new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        fondo.sendEmails();
                        return null;
                    }
                };
                sw.execute();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        agregarFotoButton.addActionListener(e -> textArea1.append("<img src=\"your url image here\">"));
    }
}
