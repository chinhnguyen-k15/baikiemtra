import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ServerGUI extends JFrame {
    private JTextArea chatArea;
    private PrintWriter out;
    private Socket clientSocket;
    private boolean isLoggedIn = false;
    private JPanel sendPanel;

    public ServerGUI() {
        setTitle("Chat Application - Server");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel authPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel usernameLabel = new JLabel("Username:");
        authPanel.add(usernameLabel);
        JTextField usernameField = new JTextField();
        usernameField.setColumns(10);
        authPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Password:");
        authPanel.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setColumns(10);
        authPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.equals("admin") && password.equals("admin")) {
                    isLoggedIn = true;
                    panel.remove(authPanel);
                    panel.add(createSendPanel(), BorderLayout.SOUTH);
                    panel.revalidate();
                    panel.repaint();
                } else {
                    JOptionPane.showMessageDialog(ServerGUI.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        authPanel.add(loginButton);

        panel.add(authPanel, BorderLayout.NORTH);

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(chatPanel, BorderLayout.WEST);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        JTextField messageField = new JTextField();
        messageField.setColumns(30);
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.CENTER);

        sendPanel = new JPanel(new CardLayout());

        JButton logOutButton = new JButton("LogOut");
        logOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logOut();
            }
        });
        sendPanel.add(logOutButton, "logout");

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
        sendPanel.add(changePasswordButton, "changepassword");

        panel.add(sendPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);

        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            chatArea.append("Connected successfully...\n");

            clientSocket = serverSocket.accept();
            chatArea.append("Client connected: " + clientSocket + "\n");
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            Thread readThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String message;
                        while ((message = in.readLine()) != null) {
                            chatArea.append("Client: " + message + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createSendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton sendMsgButton = new JButton("Send Message");
        sendMsgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendMsgButton);

        JButton sendFileButton = new JButton("Send File");
        sendFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        panel.add(sendFileButton);

        JButton sendImageButton = new JButton("Send Image");
        sendImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendImage();
            }
        });
        panel.add(sendImageButton);

        JButton logOutButton = new JButton("LogOut");
        logOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logOut();
            }
        });
        panel.add(logOutButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
        panel.add(changePasswordButton);

        return panel;
    }

    private void sendMessage(String message) {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        chatArea.append("Server: " + message + "\n");
        out.println(message);
    }

    private void sendMessage() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String message = messageField.getText();
        chatArea.append("Server: " + message + "\n");
        out.println(message);
    }

    private void sendImage() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            chatArea.append("You sent an image: " + selectedFile.getName() + "\n");

            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFile() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            chatArea.append("You sent a file: " + selectedFile.getName() + "\n");
            out.println("FILE:" + selectedFile.getName());

            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    clientSocket.getOutputStream().write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logOut() {
        isLoggedIn = false;
        CardLayout cardLayout = (CardLayout) sendPanel.getLayout();
        cardLayout.show(sendPanel, "logout");
    }

    private void changePassword() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel();
        JTextField newPasswordField = new JTextField(15);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = newPasswordField.getText();
            // Thay đổi mật khẩu ở đây
            JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
