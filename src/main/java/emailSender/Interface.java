package emailSender;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class Interface {

    private JTextField emailDeEnviadorTextField;
    private JTextField contraseniaDelEnviadorTextField;
    private JTextArea emailsAenviar;
    private JButton enviarButton;
    private JPanel panells;
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
    private JTextField pathToFileTextField;
    private JButton selectFileButton;

    public static void main(String args[]) throws Exception {
        new Interface().init();
    }

    Interface() {
        jframe = new JFrame();
    }

    private void init() throws Exception {
        jframe.setVisible(true);
        jframe.setContentPane(panells);
        jframe.setSize(1000, 1000);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final Fondo fondo = new Fondo();

        final Timer timer = new Timer(500,
                x -> {
                    try {

                        TimeElapsedRound.setText("Tiempo pasado en la ronda: " + fondo.getComienzoDeRonda().getElapsedTime() / 1000 / 60 + " : " + fondo.getComienzoDeRonda().getElapsedTime() / 1000 % 60);
                        timesPerRound.setText("Mails mandados en la ronda: " + fondo.getMailsEnviadosEnLaRonda());
                        MailsTotales.setText("Mails totales enviados : " + fondo.getPointer());
                        timeToWaitUntilFinished.setText(" Tiempo a esperar si se termina antes: " + ((3600000 - fondo.getComienzoDeRonda().getElapsedTime()) / 60 / 1000) + " : " + (3600000 - (fondo.getComienzoDeRonda().getElapsedTime()) / 1000) % 60);
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
        selectFileButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            jFileChooser.setDialogTitle("Choose your Path!");

            if (jFileChooser.showOpenDialog(selectFileButton) == JFileChooser.APPROVE_OPTION) {

            }
            Connect.getCSVFilePath = jFileChooser.getSelectedFile().getAbsolutePath();
            System.out.println(Connect.getCSVFilePath);
        });
        enviarButton.addActionListener(
                e -> {
                    Connect.whereToAddTheDatabase = "jdbc:sqlite:" + pathToFileTextField.getText();
                    try {

                        if (textArea1.getText().isEmpty()) {
                            // definir
                            fondo.setStatus("?");
                        } else {
                            final Thread thread = new Thread(
                                    () ->
                                    {
                                        fondo.setStatus("Enviando mails");
                                        System.out.println("Executing");
                                        fondo.setMaxMailsPerHour(Integer.parseInt(cantMaxima.getText()));
                                        final ArrayList<Email> emails = new ArrayList<>();
                                        for (String s : Arrays.asList(emailsAenviar.getText().split("\\s*;\\s*"))) {
                                            emails.add(new Email(s, false));
                                        }
                                        fondo.setURL(pathToFileTextField.getText());
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
                                        fondo.setPointer(0);
                                        try {
                                            fondo.sendEmails(pathToFileTextField.getText());
                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                            );

                            System.out.println("isDaemon()" + thread.isDaemon());

                            thread.setDaemon(false);
                            thread.start();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        agregarFotoButton.addActionListener(e -> textArea1.append("<img src=\"your url image here\">"));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panells = new JPanel();
        panells.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panells.add(scrollPane1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(133, 17), null, 0, false));
        emailsAenviar = new JTextArea();
        emailsAenviar.setText("Emails a enviar");
        emailsAenviar.setWrapStyleWord(false);
        scrollPane1.setViewportView(emailsAenviar);
        emailDeEnviadorTextField = new JTextField();
        emailDeEnviadorTextField.setText("email de enviador");
        panells.add(emailDeEnviadorTextField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        contraseniaDelEnviadorTextField = new JTextField();
        contraseniaDelEnviadorTextField.setText("contraseña del enviador");
        panells.add(contraseniaDelEnviadorTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Emails a enviar:");
        panells.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Contenido del email");
        panells.add(label2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panells.add(scrollPane2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textArea1 = new JTextArea();
        textArea1.setText("");
        scrollPane2.setViewportView(textArea1);
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.setBackground(new Color(-16777216));
        panells.add(infoPanel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        timeToWaitUntilFinished = new JLabel();
        timeToWaitUntilFinished.setForeground(new Color(-1));
        timeToWaitUntilFinished.setText("Label");
        infoPanel.add(timeToWaitUntilFinished, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timesPerRound = new JLabel();
        timesPerRound.setForeground(new Color(-1));
        timesPerRound.setText("Label");
        infoPanel.add(timesPerRound, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        MailsTotales = new JLabel();
        MailsTotales.setForeground(new Color(-1));
        MailsTotales.setText("Label");
        infoPanel.add(MailsTotales, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        erroresTotal = new JLabel();
        erroresTotal.setForeground(new Color(-65528));
        erroresTotal.setText("Label");
        infoPanel.add(erroresTotal, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tiempoTotal = new JLabel();
        tiempoTotal.setForeground(new Color(-1));
        tiempoTotal.setText("Label");
        infoPanel.add(tiempoTotal, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(28, 32), null, 0, false));
        statuss = new JLabel();
        statuss.setForeground(new Color(-11206886));
        statuss.setText("Label");
        infoPanel.add(statuss, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        totales = new JLabel();
        totales.setForeground(new Color(-1));
        totales.setText("Label");
        infoPanel.add(totales, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enviandoMailA = new JLabel();
        enviandoMailA.setForeground(new Color(-1));
        enviandoMailA.setText("Label");
        infoPanel.add(enviandoMailA, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TimeElapsedRound = new JLabel();
        TimeElapsedRound.setText("Label");
        infoPanel.add(TimeElapsedRound, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cantMaxima = new JTextField();
        cantMaxima.setText("Cant. max. mails/hora");
        panells.add(cantMaxima, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        agregarFotoButton = new JButton();
        agregarFotoButton.setText("Agregar foto");
        panells.add(agregarFotoButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enviarButton = new JButton();
        enviarButton.setText("Enviar");
        panells.add(enviarButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathToFileTextField = new JTextField();
        pathToFileTextField.setText("pathToFile");
        panells.add(pathToFileTextField, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        selectFileButton = new JButton();
        selectFileButton.setText("Select file");
        panells.add(selectFileButton, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panells;
    }

}
