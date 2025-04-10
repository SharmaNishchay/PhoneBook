import javax.swing.*;
import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.List;

public class PhoneBook extends JFrame {
    private static final String FILE_PATH = "phoneBook.xml";
    private Map<String, List<Contact>> contacts = new TreeMap<>();

    public static class Contact implements Serializable {
        private String name, homePhone, workPhone, primaryEmail, secondaryEmail, address;
        public Contact(){}
        public Contact(String name, String homePhone, String workPhone, String primaryEmail, String secondaryEmail, String address) {
            this.name = name;
            this.homePhone = homePhone;
            this.workPhone = workPhone;
            this.primaryEmail = primaryEmail;
            this.secondaryEmail = secondaryEmail;
            this.address = address;
        }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHomePhone() { return homePhone; }
        public void setHomePhone(String homePhone) { this.homePhone = homePhone; }
        public String getWorkPhone() { return workPhone; }
        public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }
        public String getPrimaryEmail() { return primaryEmail; }
        public void setPrimaryEmail(String primaryEmail) { this.primaryEmail = primaryEmail; }
        public String getSecondaryEmail() { return secondaryEmail; }
        public void setSecondaryEmail(String secondaryEmail) { this.secondaryEmail = secondaryEmail; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if(name != null && !name.isEmpty()) sb.append("Name: ").append(name).append(" | ");
            if(homePhone != null && !homePhone.isEmpty()) sb.append("Home: ").append(homePhone).append(" | ");
            if(workPhone != null && !workPhone.isEmpty()) sb.append("Work: ").append(workPhone).append(" | ");
            if(primaryEmail != null && !primaryEmail.isEmpty()) sb.append("Primary Email: ").append(primaryEmail).append(" | ");
            if(secondaryEmail != null && !secondaryEmail.isEmpty()) sb.append("Secondary Email: ").append(secondaryEmail).append(" | ");
            if(address != null && !address.isEmpty()) sb.append("Address: ").append(address);
            return sb.toString();
        }
    }

    public PhoneBook() {
        load();
        UI();
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE_PATH)))) {
                contacts = (Map<String, List<Contact>>) decoder.readObject();
            } catch (Exception e) {
                System.out.println("No contacts found.");
            }
        }
    }

    private void UI() {
        setTitle("Phone Book");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextField search = new JTextField();
        JButton searchButton = new JButton("Search");
        search.setPreferredSize(new Dimension(200, 25));
        JTextArea area = new JTextArea();
        area.setEditable(false);

        JButton add = new JButton("Add Contact");
        JButton edit = new JButton("Edit Selected");
        JButton delete = new JButton("Delete Selected");
        JButton save = new JButton("Save");

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout());
        top.add(search);
        top.add(searchButton);

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout());
        bottom.add(add);
        bottom.add(edit);
        bottom.add(delete);
        bottom.add(save);

        JScrollPane scroll = new JScrollPane(area);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        updatePhoneBook(area);

        searchButton.addActionListener(e -> {
            String searchTerm = search.getText().toLowerCase();
            StringBuilder result = new StringBuilder();
            Collection<List<Contact>> contactLists = contacts.values();
            for (List<Contact> contactList : contactLists) {
                for (Contact contact : contactList) {
                    if (contact.getName().toLowerCase().contains(searchTerm) ||
                            contact.getHomePhone().contains(searchTerm) ||
                            contact.getWorkPhone().contains(searchTerm)) {
                            result.append("\n");
                    }
                }
            }
            area.setText(result.toString());
        });

        add.addActionListener(e -> {
            JDialog dialog = new JDialog(PhoneBook.this, "Add Contact", true);
            dialog.setLayout(new GridLayout(7, 2));
            dialog.setSize(600, 400);

            JTextField nameField = new JTextField();
            JTextField homePhoneField = new JTextField();
            JTextField workPhoneField = new JTextField();
            JTextField primaryEmailField = new JTextField();
            JTextField secondaryEmailField = new JTextField();
            JTextField addressField = new JTextField();

            dialog.add(new JLabel("Name:"));
            dialog.add(nameField);
            dialog.add(new JLabel("Home Phone:"));
            dialog.add(homePhoneField);
            dialog.add(new JLabel("Work Phone:"));
            dialog.add(workPhoneField);
            dialog.add(new JLabel("Primary Email:"));
            dialog.add(primaryEmailField);
            dialog.add(new JLabel("Secondary Email:"));
            dialog.add(secondaryEmailField);
            dialog.add(new JLabel("Address:"));
            dialog.add(addressField);

            JButton saveContact = new JButton("Save");
            saveContact.addActionListener(e2 -> {
                Contact newContact = new Contact(
                        nameField.getText(),
                        homePhoneField.getText(),
                        workPhoneField.getText(),
                        primaryEmailField.getText(),
                        secondaryEmailField.getText(),
                        addressField.getText()
                );
                String firstLetter = newContact.getName().substring(0, 1).toUpperCase();
                List<Contact> contactList = contacts.get(firstLetter);
                if (contactList == null) {
                    contactList = new ArrayList<>();
                    contacts.put(firstLetter, contactList);
                }
                contactList.add(newContact);
                updatePhoneBook(area);
                dialog.dispose();
            });

            dialog.add(saveContact);
            dialog.setVisible(true);
        });

        edit.addActionListener(e -> {
            String selected = area.getSelectedText();
            if (selected != null && !selected.trim().isEmpty()) {
                JDialog dialog = new JDialog(PhoneBook.this, "Edit Contact", true);
                dialog.setLayout(new GridLayout(7, 2));
                dialog.setSize(600, 400);

                JTextField nameField = new JTextField();
                JTextField homePhoneField = new JTextField();
                JTextField workPhoneField = new JTextField();
                JTextField primaryEmailField = new JTextField();
                JTextField secondaryEmailField = new JTextField();
                JTextField addressField = new JTextField();

                dialog.add(new JLabel("Name:"));
                dialog.add(nameField);
                dialog.add(new JLabel("Home Phone:"));
                dialog.add(homePhoneField);
                dialog.add(new JLabel("Work Phone:"));
                dialog.add(workPhoneField);
                dialog.add(new JLabel("Primary Email:"));
                dialog.add(primaryEmailField);
                dialog.add(new JLabel("Secondary Email:"));
                dialog.add(secondaryEmailField);
                dialog.add(new JLabel("Address:"));
                dialog.add(addressField);

                String[] parts = selected.trim().split(" \\| ");
                if (parts.length > 0) nameField.setText(parts[0].replace("Name: ", ""));
                if (parts.length > 1) homePhoneField.setText(parts[1].replace("Home: ", ""));
                if (parts.length > 2) workPhoneField.setText(parts[2].replace("Work: ", ""));
                if (parts.length > 3) primaryEmailField.setText(parts[3].replace("Primary Email: ", ""));
                if (parts.length > 4) secondaryEmailField.setText(parts[4].replace("Secondary Email: ", ""));
                if (parts.length > 5) addressField.setText(parts[5].replace("Address: ", ""));

                JButton saveContact = new JButton("Save");
                saveContact.addActionListener(e2 -> {
                    Collection<List<Contact>> contactLists = contacts.values();
                    for (List<Contact> contactList : contactLists) {
                        Iterator<Contact> iterator = contactList.iterator();
                        while (iterator.hasNext()) {
                            Contact contact = iterator.next();
                            if (contact.toString().equals(selected.trim())) {
                                iterator.remove();
                            }
                        }
                    }
                    Contact updatedContact = new Contact(
                            nameField.getText(),
                            homePhoneField.getText(),
                            workPhoneField.getText(),
                            primaryEmailField.getText(),
                            secondaryEmailField.getText(),
                            addressField.getText()
                    );
                    String firstLetter = updatedContact.getName().isEmpty() ? "A" :
                            updatedContact.getName().substring(0, 1).toUpperCase();
                    List<Contact> contactList = contacts.get(firstLetter);
                    if (contactList == null) {
                        contactList = new ArrayList<>();
                        contacts.put(firstLetter, contactList);
                    }
                    contactList.add(updatedContact);

                    updatePhoneBook(area);
                    dialog.dispose();
                });

                dialog.add(saveContact);
                dialog.setVisible(true);
            }
        });

        delete.addActionListener(e -> {
            String selected = area.getSelectedText();
            if (selected != null) {
                Collection<List<Contact>> contactLists = contacts.values();
                for (List<Contact> contactList : contactLists) {
                    Iterator<Contact> iterator = contactList.iterator();
                    while (iterator.hasNext()) {
                        Contact contact = iterator.next();
                        if (contact.toString().equals(selected.trim())) {
                            iterator.remove();
                        }
                    }
                }
                updatePhoneBook(area);
            }
        });

        save.addActionListener(e -> {
            try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))) {
                encoder.writeObject(contacts);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(PhoneBook.this, "Error saving contacts: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private void updatePhoneBook(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        Collection<List<Contact>> contactLists = contacts.values();
        for (List<Contact> ls : contactLists) {
            for (Contact contact : ls) {
                sb.append(contact).append("\n");
            }
        }
        area.setText(sb.toString());
    }

    public static void main(String[] args) {
        new PhoneBook();
    }
}