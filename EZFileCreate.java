//Chris Varghese
//This program creates a file for the user, using what they input as their desired file name and file extension. If an inputted file already exists, the user is prompted if they want to rename the new desired file. If the user cancels, they are given the option to delete the existing file.
//01/15/24
//01/18/24

//Imports classes and libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class EZFileCreate extends JFrame { //extends JFrame to avoid having to repeat code and creates a foundation for the rest of the GUI parts.

    JTextField fileNameTextField;//TextField for user input.
    JComboBox<String> extensionComboBox; //I used arrayList so that the user can add their desired file extensions.
    
    final String[] DEFAULT_EXTENSIONS = {".java", ".py", ".css", ".txt", ".doc", ".html"}; //I turned the file extensions into a constant for simplicity as it would take up unecessary lines of code if I used the 'add' calls instead.
    ArrayList<String> extensionsList = new ArrayList<>(Arrays.asList(DEFAULT_EXTENSIONS));//I used this to convert the extensions into a list and initialize them in one line for conciseness.

    public EZFileCreate() { 
        //I asked ChatGPT to help me with the FlowLayout GUI. I tried using GridLayout and BoxLayout like we learned in class but I could not seem to make it look visually pleasing. I then asked ChatGPT to convert my previously created GridLayout into a FlowLayout, and I really liked how it looked.
        //I created all the JLabels, JButtons, and JTextField, and ChatGPT helped me rearrange the GUI components for the FlowLayout.
        setTitle("EZFileCreate - Chris Varghese");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
       
        //Creating GUI components. 
        fileNameTextField = new JTextField();
        fileNameTextField.setPreferredSize(new Dimension(200, 25));
        extensionComboBox = new JComboBox<>(extensionsList.toArray(new String[0]));
        JButton createButton = new JButton("Create File");
        JButton addExtensionButton = new JButton("Add Extension");
        

        //Setting the layout.
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        //Adding GUI components to the frame.
        add(new JLabel("Enter File Name: "));
        add(fileNameTextField);
        add(new JLabel("Select File Extension: "));
        add(extensionComboBox);
        add(addExtensionButton);
        add(createButton);

        //Adding the action listeners for the JButtons.
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createFile();
            }
        });
        addExtensionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addExtension();
            }
        });
    }
    /*
     * Creates a new file using the provided file name and selected file extension. Then writes a short statement into the newly created file.
     * pre: The file name must not be empty or consist only of whitespace. fileName != null
     * post: If the file is successfully created, a short statement is written into the newly created file. Also displays success or error messages through JOptionPane dialogs.
     */
    public void createFile() {
        String fileName = fileNameTextField.getText();
        String selectedExtension = (String) extensionComboBox.getSelectedItem();

        if (fileName.trim().isEmpty()) { //Checks if the user input nothing or whitespace. 
            JOptionPane.showMessageDialog(this, "Please enter a file name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = renameFile(fileName, selectedExtension); //calls renameFile method.

        if (file == null) {
            return; //User canceled or an error occurred.
        }

        try { //Using try-catch block to handle potential exceptions that may occur.
            if (file.createNewFile()) {
                // File created successfully, then writes a short statement into the newly created file.
                FileWriter writer = new FileWriter(file);
                writer.write("File content for " + fileName + selectedExtension);
                writer.close();

                JOptionPane.showMessageDialog(this, "File created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     * Renames the file that the user wants to create to avoid naming conflicts and provides options for user interaction.
     * pre: The file name must not be empty or consist only of whitespace. fileName != null
     * post: If the file is successfully renamed, the new File object is returned.
     * If the user chooses not to rename and opts to delete the existing file, the existing file is deleted, and null is returned.
     * If the user cancels the operation or enters an empty new file name, then null is returned. This also displays relevant messages through the JOptionPane.
     */
    public File renameFile(String fileName, String selectedExtension) {
        File file = new File(fileName + selectedExtension);

        while (file.exists()) { //checks if the file exists in the Java folder in which this program is located.
            int option = JOptionPane.showConfirmDialog(this, //JOptionPane which asks user if they want to rename the file that they want to create.
                    "File already exists. Do you want to choose a new name for your file? " + fileName + selectedExtension, "File Exists", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) { // If user selects Yes and wants to rename the file.
                String newFileName = JOptionPane.showInputDialog(this, "Enter new file name for new file:");

                if (newFileName == null || newFileName.trim().isEmpty()) { // User canceled or entered an empty name.
                    return null;
                }

                file = new File(newFileName + selectedExtension);
            } else {
                // User clicked No and chose not to rename, end file creation.
                int option2 = JOptionPane.showConfirmDialog(this, //JOptionPane for asking the user if they want to delete existing file.
                    "Would you like to delete the already existing file instead? " + fileName + selectedExtension, "File Deletion",JOptionPane.YES_NO_OPTION);
                    
                    if (option2 == JOptionPane.YES_OPTION){ //User chose to delete existing file.
                        file.delete();
                        JOptionPane.showMessageDialog(this, file + " deleted successfully.");
                    }
                    int option3 = JOptionPane.showConfirmDialog(this, //JOptionPane for asking the user if they want to create the new file.
                    "Would you like to create the new file? " + fileName + selectedExtension, "File Creation",JOptionPane.YES_NO_OPTION);
                    if (option3 == JOptionPane.YES_OPTION){ //User chose to create the new file.
                        createFile();
                    }
                    if (option3 == JOptionPane.NO_OPTION){ //User chose not to create the new file. No file was created and returns to the beginning.
                        return null;
                    }
                return null;
            }
        }
        return file;
    }
    /*
     * Prompts the user to input a new file extension and adds it to the list of supported extensions.
     * pre: none, if the user does not input anything, then nothing will be created or added to the extensionsList JComboBox.
     * post: The user is prompted for a file extension and the new extension is then added to the JComboBox for user selection.
     */
    public void addExtension() {
        String newExtension = JOptionPane.showInputDialog(this,
                "Enter new file extension (e.g., .xyz):",
                ".");  // Makes it so that the input text box will begin with a dot for easier use.
    
        if (newExtension != null && !newExtension.trim().isEmpty()) { //checks if input is empty or not.
            extensionsList.add(newExtension);
            extensionComboBox.addItem(newExtension); //adds the user-inputted file extension to the JComboBox.
        }
    }
    
    //Code directly from the template provided in class.
    public static void runGUI() {
        JFrame.setDefaultLookAndFeelDecorated(false);
        EZFileCreate fileCreator = new EZFileCreate();
        fileCreator.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runGUI();
            }
        });
    }
}