import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;

public class ClientGUI extends JFrame {
    private JTextField messageField;
    private JTextArea chatArea;
    private PrintWriter out;
    private Socket socket;
    private boolean isLoggedIn = false;
    private JLabel imagePreviewLabel;

    public ClientGUI() {
        setTitle("Ứng dụng chat của Trần Quý Nhất");
        setSize(1000, 185);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel authPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        JLabel usernameLabel = new JLabel("Username:");
        authPanel.add(usernameLabel);
        JTextField usernameField = new JTextField();
        authPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Password:");
        authPanel.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        authPanel.add(passwordField);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Thực hiện xác thực thông tin đăng nhập ở đây
                if (username.equals("quynhat") && password.equals("14052003")) {
                    isLoggedIn = true;
                    panel.remove(authPanel);
                    panel.add(createMainPanel(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else if (username.equals("1") && password.equals("1")) {
                    isLoggedIn = true;
                    panel.remove(authPanel);
                    panel.add(createMainPanel(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else if (username.equals("2") && password.equals("2")) {
                    isLoggedIn = true;
                    panel.remove(authPanel);
                    panel.add(createMainPanel(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(ClientGUI.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
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
        scrollPane.setPreferredSize(new Dimension(350, 350));
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(chatPanel, BorderLayout.WEST);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        JButton sendButton = new JButton("Send Message");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        buttonPanel.add(sendButton);

        JButton sendImageButton = new JButton("Send Image");
        sendImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendImage();
            }
        });
        buttonPanel.add(sendImageButton);

        JButton sendFileButton = new JButton("Send File");
        sendFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        buttonPanel.add(sendFileButton);

        JButton deleteButton = new JButton("Delete Message");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMessage();
            }
        });
        buttonPanel.add(deleteButton);

        JButton logOutButton = new JButton("LogOut");
        logOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logOut();
            }
        });
        buttonPanel.add(logOutButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.CENTER);

        add(panel);
        setVisible(true);

        try {
            socket = new Socket("localhost", 12345);
            chatArea.append("Bạn đã truy cập\n");
            out = new PrintWriter(socket.getOutputStream(), true);

            Thread readThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String message;
                        while ((message = in.readLine()) != null) {
                            if (message.startsWith("FILE:")) {
                                receiveFile();
                            } else {
                                chatArea.append("Server: " + message + "\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                private void receiveFile() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel hiển thị trò chuyện
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(350, 350));
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(chatPanel, BorderLayout.WEST);

        // Panel nhập tin nhắn
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        JButton sendButton = new JButton("Send Message");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        buttonPanel.add(sendButton);

        JButton sendImageButton = new JButton("Send Image");
        sendImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendImage();
            }
        });
        buttonPanel.add(sendImageButton);

        JButton sendFileButton = new JButton("Send File");
        sendFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        buttonPanel.add(sendFileButton);

        JButton deleteButton = new JButton("Delete Message");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMessage();
            }
        });
        buttonPanel.add(deleteButton);

        JButton logOutButton = new JButton("LogOut");
        logOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logOut();
            }
        });
        buttonPanel.add(logOutButton);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void logOut() {
        isLoggedIn = false;
        getContentPane().removeAll();
        getContentPane().add(createAuthPanel(), BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private JPanel createAuthPanel() {
        JPanel authPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        JLabel usernameLabel = new JLabel("Username:");
        authPanel.add(usernameLabel);
        JTextField usernameField = new JTextField();
        authPanel.add(usernameField);
        JLabel passwordLabel = new JLabel("Password:");
        authPanel.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        authPanel.add(passwordField);
        JLabel newPasswordLabel = new JLabel("New Password:");
        authPanel.add(newPasswordLabel);
        JPasswordField newPasswordField = new JPasswordField();
        authPanel.add(newPasswordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Thực hiện xác thực thông tin đăng nhập ở đây
                if (username.equals("quynhat") && password.equals("14052003")) {
                    isLoggedIn = true;
                    getContentPane().remove(authPanel);
                    getContentPane().add(createMainPanel(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(ClientGUI.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        authPanel.add(loginButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newPassword = new String(newPasswordField.getPassword());
                if (!isLoggedIn) {
                    JOptionPane.showMessageDialog(ClientGUI.this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(ClientGUI.this, "Please enter a new password.", "Change Password", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Thực hiện thay đổi mật khẩu ở đây
                JOptionPane.showMessageDialog(ClientGUI.this, "Password changed successfully.", "Change Password", JOptionPane.INFORMATION_MESSAGE);
                passwordField.setText(newPassword);
                newPasswordField.setText("");
            }
        });
        authPanel.add(changePasswordButton);

        return authPanel;
    }

    private void sendMessage() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String message = messageField.getText();
        chatArea.append("You: " + message + "\n");
        out.println(message);
        messageField.setText("");
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
                // Hiển thị trước ảnh
                BufferedImage img = ImageIO.read(selectedFile);
                JFrame frame = new JFrame("Image Preview");
                JLabel label = new JLabel(new ImageIcon(img));
                frame.getContentPane().add(label, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
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
                    socket.getOutputStream().write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteMessage() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please login first.", "Not Logged In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] options = { "Yes", "No" };
        int choice = JOptionPane.showOptionDialog(this, "Are you sure you want to delete the last message?", "Delete Message",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        if (choice == JOptionPane.YES_OPTION) {
            String text = chatArea.getText();
            int lastIndex = text.lastIndexOf("\nYou:");
            if (lastIndex >= 0) {
                int startIndex = text.substring(0, lastIndex).lastIndexOf("\nYou:");
                chatArea.setText(text.substring(0, startIndex));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int numberOfClients = 3; // Số lượng người dùng muốn tạo
                for (int i = 0; i < numberOfClients; i++) {
                    new ClientGUI();
                }
            }
        });
    }
}
