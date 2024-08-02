import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ContactManager extends JFrame {

    private DefaultListModel<String> contactListModel;
    private JList<String> contactList;
    private JLabel lblname, lblphone, lblemail;
    private JTextField nameField, phoneField, emailField;
    private Map<String, Contact> contacts;
    private final String FILE_NAME = "contacts.txt";

    public ContactManager() {
        setTitle("Contact Management System");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setBackground(new Color(255,255,255));
       
        contacts = new HashMap<>();
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.addListSelectionListener(e -> populateFields());

        // JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);   
        mainPanel.setSize(500,500);
        mainPanel.setBackground(new Color(0,0,153));

        this.add(mainPanel);
        JPanel inputPanel = new JPanel();
        //inputPanel.setBounds(20,20,440,420);
        inputPanel.setSize(400,400);
        
        inputPanel.setLocation(43, 30);

      //  inputPanel.setSize(400, 400);
         
        inputPanel.setLayout(null);   
        inputPanel.setBackground(new Color(204,255,229));

        lblname = new JLabel("Name:");
        lblname.setBounds(20,20,50,25);
        inputPanel.add(lblname);

        nameField = new JTextField();
        nameField.setBounds(100,20,200,25);
        inputPanel.add(nameField);

        lblphone = new JLabel("Phone:");
        lblphone.setBounds(20,55,50,25);
        inputPanel.add(lblphone);

        phoneField = new JTextField();
        phoneField.setBounds(100,55,200,25);
        inputPanel.add(phoneField);

        lblemail = new JLabel("Email:");
        lblemail.setBounds(20,90,50,25);
        inputPanel.add(lblemail);

        emailField = new JTextField();
        emailField.setBounds(100,90,200,25);
        inputPanel.add(emailField);
        
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton viewButton = new JButton("View");

        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        viewButton.addActionListener(e -> viewContact());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);

        mainPanel.add(inputPanel);

        buttonPanel.setBounds(0,135,400,50);
        buttonPanel.setBackground(new Color(204,255,229));

        inputPanel.add(buttonPanel);

        JScrollPane jScrollPane = new JScrollPane(contactList);
        jScrollPane.setBounds(20,195,360,200);
        
        inputPanel.add(jScrollPane);
        
       // add(inputPanel, BorderLayout.NORTH);
       // add(new JScrollPane(contactList), BorderLayout.CENTER);
      
      // add(buttonPanel, BorderLayout.SOUTH);

        loadContacts(); // Automatically load contacts on startup
    }

    private void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (contacts.containsKey(name)) {
            JOptionPane.showMessageDialog(this, "Contact already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Contact contact = new Contact(name, phone, email);
        contacts.put(name, contact);
        contactListModel.addElement(name);
        clearFields();
        saveContacts();
    }

    private void updateContact() {
        String selectedName = contactList.getSelectedValue();
        if (selectedName == null) {
            JOptionPane.showMessageDialog(this, "No contact selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Remove the old contact
        contacts.remove(selectedName);
        contactListModel.removeElement(selectedName);

        // Add the updated contact
        Contact contact = new Contact(name, phone, email);
        contacts.put(name, contact);
        contactListModel.addElement(name);
        clearFields();
        saveContacts();
    }

    private void deleteContact() {
        String selectedName = contactList.getSelectedValue();
        if (selectedName == null) {
            JOptionPane.showMessageDialog(this, "No contact selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        contacts.remove(selectedName);
        contactListModel.removeElement(selectedName);
        clearFields();
        saveContacts();
    }

    private void viewContact() {
        String selectedName = contactList.getSelectedValue();
        if (selectedName == null) {
            JOptionPane.showMessageDialog(this, "No contact selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Contact contact = contacts.get(selectedName);
        JOptionPane.showMessageDialog(this, contact.toString(), "Contact Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadContacts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            contacts = (Map<String, Contact>) ois.readObject();
            contactListModel.clear();
            for (String name : contacts.keySet()) {
                contactListModel.addElement(name);
            }
        } catch (IOException | ClassNotFoundException e) {
            // File might not exist on first run or other exceptions
        }
    }

    private void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving contacts.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        String selectedName = contactList.getSelectedValue();
        if (selectedName != null) {
            Contact contact = contacts.get(selectedName);
            if (contact != null) {
                nameField.setText(contact.getName());
                phoneField.setText(contact.getPhone());
                emailField.setText(contact.getEmail());
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactManager().setVisible(true));
    }

    private static class Contact implements Serializable {
        private String name;
        private String phone;
        private String email;

        public Contact(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "Name: " + name + "\nPhone: " + phone + "\nEmail: " + email;
        }
    }
}
