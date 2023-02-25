import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Insur extends JFrame{
    Calendar cld = Calendar.getInstance();
    JDateChooser dcCommence = new JDateChooser(cld.getTime());
    JDateChooser dcExpiry = new JDateChooser();
    JDateChooser dcDOB = new JDateChooser();
    private JPanel panel1;
    private JTextField textName;
    private JComboBox cbGender;
    private JTextField textPostcode;
    private JTextField textIC;
    private JTextField textTown;
    private JButton saveButton;
    private JLabel labelCommence;
    private JLabel labelInsured;
    private JLabel labelGender;
    private JLabel labelPostcode;
    private JLabel labelExpiry;
    private JLabel labelIC;
    private JLabel labelDOB;
    private JLabel labelTown;
    private JPanel jpCal0;
    private JPanel jpCal1;
    private JPanel jpCal2;

    public Insur(String title){
        super(title);

        // Set maximum length of textName to 30
        textName.setDocument(new JTextFieldLimit(30));

        // Set maximum length of textIC to 12
        textIC.setDocument(new JTextFieldLimit(12));

        // add input verifier to textCommence
        dcCommence.setInputVerifier(new DateVerifier());
        // add input verifier to textExpiry
        dcExpiry.setInputVerifier(new DateVerifier());
        // add input verifier to textDOB
        dcDOB.setInputVerifier(new DateVerifier());

        cbGender.setSelectedIndex(0);
        cbGender.setEnabled(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);

        //calendar
        dcCommence.setDateFormatString("dd/MM/yyyy");
        dcExpiry.setDateFormatString("dd/MM/yyyy");
        dcDOB.setDateFormatString("dd/MM/yyyy");
        jpCal0.add(dcCommence);
        jpCal1.add(dcExpiry);

        textIC.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setDOBFromIC();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setDOBFromIC();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setDOBFromIC();
            }
        });

        jpCal2.add(dcDOB);

        // Add save button ActionListener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        this.pack();
    }


    public static void main(String[] args) {
        JFrame frame = new Insur("Insurance");
        frame.setVisible(true);
    }


    private void setDOBFromIC() {
        String ic = textIC.getText().trim();

        if (ic.length() == 12 && ic.matches("\\d+")) {
            int year = Integer.parseInt(ic.substring(0, 2));
            int month = Integer.parseInt(ic.substring(2, 4));
            int day = Integer.parseInt(ic.substring(4, 6));

            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                Calendar cal = Calendar.getInstance();
                int currYear = cal.get(Calendar.YEAR) % 100;

                if (year > currYear) {
                    year += 1900;
                } else {
                    year += 2000;
                }

                cal.set(year, month - 1, day);
                Date dob = cal.getTime();
                dcDOB.setDate(dob);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid date of birth");
            }
        } else {
            dcDOB.setDate(null);
        }
    }

    private void saveData() {
        // Get the values entered by the user
        String name = textName.getText();
        String icNumber = textIC.getText();
        String gender = (String)cbGender.getSelectedItem();
        String postcode = textPostcode.getText();
        String town = textTown.getText();
        Date commenceDate = dcCommence.getDate();
        Date expiryDate = dcExpiry.getDate();
        Date dob = dcDOB.getDate();

        // Check if any required fields are blank
        if (name.isBlank() || icNumber.isBlank() || gender.isBlank() || postcode.isBlank() || town.isBlank() || commenceDate == null || expiryDate == null || dob == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save the data to a file or database, etc.
        // ...

        // Show confirmation message
        JOptionPane.showMessageDialog(this, "Your info has been saved successfully.");
    }

    public class JTextFieldLimit extends PlainDocument {
        private int limit;

        public JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) {
                return;
            }

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    // custom input verifier for date fields
    private class DateVerifier extends InputVerifier {
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String text = textField.getText().trim();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            try {
                Date date = dateFormat.parse(text);
                return true;
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid date format (dd/MM/yyyy)");
                return false;
            }
        }
    }
}
