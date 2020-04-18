package emailSender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Interface extends JFrame {

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
    private JLabel enviandoMailA;
    private JPanel infoPanel;
    public Interface() {

        Timer timer = new Timer(1, action);
        timer.setInitialDelay(0);
        setVisible(true);
        setContentPane(panell);
        setSize(1000, 1500);

        enviarButton.addActionListener(e -> {

            Main.setStatus("Enviando mails");
            System.out.println("Executing");

            timer.start();
            Main.MaxMailsPerHour = Integer.parseInt(cantMaxima.getText());
            Main.setEMAIL_TO(emailsAenviar.getText());
            Main.setUSERNAME(emailDeEnviadorTextField.getText());
            Main.setPASSWORD(contraseniaDelEnviadorTextField.getText());
            Main.setMessageToSend(textArea1.getText());
            if (Main.getUSERNAME().contains("iorl")) {
                System.out.println("Setting iorl");
                Main.setIorl(true);
            }
            if (Main.getUSERNAME().contains("gmail")) {
                System.out.println("Gmail");
                Main.setGmail(true);

            }

            Main main = new Main();
            try {
                main.execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }


        });

        agregarFotoButton.addActionListener(e -> textArea1.append("<img src=\"your url image here\">"));
    }

    ActionListener action = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {

            try {

                TimeElapsedRound.setText("Tiempo pasado en la ronda: " + Main.chronometer.getElapsedTime() / 1000 / 60 + " : " + Main.chronometer.getElapsedTime() / 1000 % 60);
                timesPerRound.setText("Mails mandados en la ronda: " + Main.mailsEnviadosEnLaRonda);
                MailsTotales.setText("Mails totales enviados : " + Main.pointer);
                timeToWaitUntilFinished.setText(" Tiempo a esperar si se termina antes: " + ((3600000 - Main.chronometer.getElapsedTime()) / 60 / 1000) + " : " + (3600000 - (Main.chronometer.getElapsedTime()) / 1000) % 60);
                erroresTotal.setText("Errores en envio: " + Main.errores);
                tiempoTotal.setText("Tiempo total corriendo: " + Main.tiempoTotal.getElapsedTime() / 60 / 1000 + " : " + (Main.tiempoTotal.getElapsedTime() / 1000) % 60);
                statuss.setText("Estado: " + Main.status);
                totales.setText("Cantidad de mails a enviar: " + Main.mailsTotales);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();


            }
        }

    };

}
