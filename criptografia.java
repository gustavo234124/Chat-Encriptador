import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.crypto.*;
import java.io.*;
import javax.crypto.spec.*;
import java.security.Key;

public class criptografia {
    private JFrame frame;
    private JFileChooser fileChooser;
    private JTextArea logArea;
    private JButton encryptButton;
    private JButton decryptButton;
    private JComboBox<String> algorithmSelector;
    private Cipher cipher;

    public criptografia() {
        frame = new JFrame("3 Encriptaciones");
        fileChooser = new JFileChooser();
        logArea = new JTextArea(10, 40);
        encryptButton = new JButton("Encriptar");
        decryptButton = new JButton("Desencriptar");
        algorithmSelector = new JComboBox<>(new String[]{"DES", "AES", "3DES"});

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel("Selecciona el Algoritmo:"));
        frame.add(algorithmSelector);
        frame.add(buttonPanel); 
        frame.add(logArea);

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performCrypto(Cipher.ENCRYPT_MODE);
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performCrypto(Cipher.DECRYPT_MODE);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void performCrypto(int mode) {
        int returnVal = fileChooser.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fileToProcess = fileChooser.getSelectedFile();

            try {
                cipher = getCipher(algorithmSelector.getSelectedItem().toString(), mode);
                FileInputStream fis = new FileInputStream(fileToProcess);
                byte[] fileData = new byte[(int) fileToProcess.length()];
                fis.read(fileData);
                fis.close();

                byte[] processedData = cipher.doFinal(fileData);

                String outputFileName = (mode == Cipher.ENCRYPT_MODE) ? "encrypted_" : "decrypted_";
                outputFileName += fileToProcess.getName();
                FileOutputStream fos = new FileOutputStream(outputFileName);
                fos.write(processedData);
                fos.close();

                log("Operación completada, archivo guardado como:  " + outputFileName);
            } catch (Exception ex) {
                log("Error: " + ex.getMessage());
            }
        }
    }

    private Cipher getCipher(String algorithm, int mode) throws Exception {
        Key key;
        String transformation;

        if ("DES".equals(algorithm)) {
            key = new SecretKeySpec("01234567".getBytes(), "DES");
            transformation = "DES/ECB/PKCS5Padding";
        } else if ("AES".equals(algorithm)) {
            key = new SecretKeySpec("0123456789abcdef0123456789abcdef".getBytes(), "AES");
            transformation = "AES/ECB/PKCS5Padding";
        } else if ("3DES".equals(algorithm)) {
            key = new SecretKeySpec("0123456789abcdef01234567".getBytes(), "DESede");
            transformation = "DESede/ECB/PKCS5Padding";
        } else {
            throw new IllegalArgumentException("Algoritmo invalido");
        }

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(mode, key);

        return cipher;
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new criptografia();
            }
        });
    }
}
