/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import daviscahtech_bookmanagement.Pojo.BookDetails;
import daviscahtech_bookmanagement.Pojo.HeaderFooterPageEvent;
import daviscahtech_bookmanagement.Pojo.IssuedBooks;
import daviscahtech_bookmanagement.Pojo.LostBooks;
import daviscahtech_bookmanagement.Pojo.StudentDetails;
import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class MainPageController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image studentImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/student.png").toExternalForm());
    ImageView studentImageView = new ImageView(studentImage);
    
    Image bookImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/book.png").toExternalForm());
    ImageView bookImageView = new ImageView(bookImage);
    
    Image inboxImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/inbox.png").toExternalForm());
    ImageView emailImageView = new ImageView(inboxImage);
    
    Image partnerImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/partner.png").toExternalForm());
    ImageView partnerImageView = new ImageView(partnerImage);
    
    Image pdfImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/pdf.png").toExternalForm());
    ImageView pdfImageView = new ImageView(pdfImage);
    
    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    
    //instance of FIlechooser
    private FileChooser fileChooser;
    //instance of File
    private File file;
    private Window Stage;
    
    //TExtfields
    @FXML
    private TextField studentSearchID;
    @FXML
    private TextField studentID;
    @FXML
    private TextField studentName;
    
    @FXML
    private TextField bookSearchID;
    @FXML
    private TextField bookID;
    @FXML
    private TextField bookClass;
    @FXML
    private TextField bookAuthor;
    @FXML
    private TextField BookTitle;
    @FXML
    private TextField bookPublisher;
    
    
    @FXML
    private TextField issuebook_searchBookID;
    @FXML
    private TextField issuebook_givenBookID;
    @FXML
    private TextField lostbook_searchBookID;
    @FXML
    private TextField lostbook_bookID;
    @FXML
    private TextField lostbook_bookTitle;
    @FXML
    private TextField lostbook_bookClass;
    @FXML
    private TextField lostbook_studentID;
    @FXML
    private TextField lostbook_studentName;
    
    @FXML
    private TextField issuebook_teacherIssuing;
    @FXML
    private TextField issuebook_searchStudentID;
    @FXML
    private Text issuebook_bookID;
    
    //ComboBoxes
    @FXML
    private ComboBox studentChooseClass;
    private final ObservableList classNameData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox lostbook_comboBookSubject;
    private final ObservableList subjectData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox bookSubject;
    private final ObservableList bookSubjectData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox lostBook_combonoClassList;
    private final ObservableList lostBookClassListData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox promoteSingleStudentCombobox;
    private final ObservableList singleStudentClassListData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox depromoteStudentCombobox;
    private final ObservableList depromotionClassListData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox finalStudentComboClass;
    private final ObservableList finalDeleteClassListData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox issuebook_bookStatus;
    ObservableList<String> bookstatusList = FXCollections.<String>observableArrayList("Newest","Newer","New","Old","Older","Oldest");
    
    
    // TableView
    @FXML
    private TableView<StudentDetails> student_table;
    @FXML
    private TableColumn<StudentDetails, String> column_studentID;
    @FXML
    private TableColumn<StudentDetails, String> column_studentNmae;
    @FXML
    private TableColumn<StudentDetails, String> column_studentClass;
    @FXML
    private TableColumn<StudentDetails, String> column_studentRegistrationDate;
    @FXML
    private TableColumn<StudentDetails, String> column_studentEnrolled;
    private final ObservableList<StudentDetails> studentDetailsData = FXCollections.observableArrayList();
    
    @FXML
    private TableView<BookDetails> book_table;
    @FXML
    private TableColumn<BookDetails, String> book_columnID;
    @FXML
    private TableColumn<BookDetails, String> book_columnClass;
    @FXML
    private TableColumn<BookDetails, String> book_columnSubject;
    @FXML
    private TableColumn<BookDetails, String> book_columnAuthor;
    @FXML
    private TableColumn<BookDetails, String> book_columnTitle;
    @FXML
    private TableColumn<BookDetails, String> book_columnPublisher;
    @FXML
    private TableColumn<BookDetails, String> book_columnEnteredDate;
    @FXML
    private TableColumn<BookDetails, String> book_columnAvailable;
    private final ObservableList<BookDetails> booksData = FXCollections.observableArrayList();
    
    @FXML
    private TableView<IssuedBooks> issuedBook_table;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnBookID;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnBookClass;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnBookSubject;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnBookTitle;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnStudentID;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnStudentName;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnIssuedBy;
    @FXML
    private TableColumn<IssuedBooks, String> issuedBook_columnDateIssued;
    private final ObservableList<IssuedBooks> issuedbooksData = FXCollections.observableArrayList();
    
    @FXML
    private TableView<LostBooks> table_lostBook;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnBookID;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnBookClass;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnBookTitle;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnBookSubject;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnStudentID;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnStudentName;
    @FXML
    private TableColumn<LostBooks, String> lostbook_columnDateLost;
    private final ObservableList<LostBooks> lostbooksData = FXCollections.observableArrayList();
    
    //Texts
    @FXML
    private Text book_registeredDateText;
    @FXML
    private Text book_registeredDate;
    
    @FXML
    private Text issueBook_studentID;
    @FXML
    private Text issueBook_studentName;    
    @FXML
    private Text issuebook_bookTtle;
    @FXML
    private Text issuebook_bookClass;
    @FXML
    private Text issuebook_bookSubject;    
    @FXML
    private Text lostbook_regDateText;
    @FXML
    private Text lostbook_regDate;
    @FXML
    private TextField searchAnyOnStudentTable;
    @FXML
    private TextField searchAnyOnBookTable;
    @FXML
    private TextField isssuedBook_givenStudentID;
    @FXML
    private TextField lostbook_searchStudentID;
    @FXML
    private TextField dailyFine;
    @FXML
    private TextArea replacementTerms;
    @FXML
    private TextField bookM_responsibleStudentID;
    @FXML
    private Text issueBook_studentClass;
    @FXML
    private TextArea infoTextArea;
    @FXML
    private Text promote_studentClass;    
    @FXML
    private TextField studentParentEmail;
    @FXML
    private Text parentEmailDisplay;
    @FXML
    private HBox emaillingStatus;
    @FXML
    private Text displayTermID;

    // constructor
      public MainPageController() {
      }    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
        
        //Handle comboBox details
        updateComboClass();
        updateBookSubjects();
        issuebook_bookStatus.setItems(bookstatusList);
        
        //Handle update tables
        update_studentDetailTable();
        update_bookDetailTable();
        update_issuedbookDetailTable();
        update_lostbookDetailTable();
        
        //respond to multible table search
        changeTableStudentOnSearch();
        changeTableBookOnSearch();
        changeTableIssuedBooksOnSearch();
        changeTableLostBooksOnSearch();
        
        //Create a folder in the C drive to put all printed pdf generated for student notes
               File directory = new File("C:\\Book Watch\\Lost_books_notes");
               if (!directory.exists()) {
                   if (directory.mkdirs()) {
                       System.out.println("Directory created");
                   } else {
                       System.out.println("Directory not created");
                   }
                } 
                   
        
    }
    
    //##################### SPECIAL METHODS TO HANDLE MULTIPLE TABLE SEARCHES ##
       //Method to monitor produt hange update during searh
    private void changeTableStudentOnSearch(){
        
        //wrap the observablelist in a FilteredList()
        FilteredList<StudentDetails> filteredData =  new FilteredList<>(studentDetailsData,p -> true);
        
        //set the filter predicate whenever the filter changes
        searchAnyOnStudentTable.textProperty().addListener((observable,oldValue,newValue)->{
                
            filteredData.setPredicate(StudentDetails -> {
            
                //if filter text is empty, display all product
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //compare product id and product name of every product with the filter text
                String lowerCaseFilter =newValue.toLowerCase();
               
                if (StudentDetails.getStudentName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product Id
                } else if (StudentDetails.getStudentClass().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                }
                return false; // Do not match            
            });
        
        });
        
        //wrap the filteredList in a sortedList
        SortedList<StudentDetails> sortedData = new SortedList<>(filteredData);
        
        //bind the sortedData to the Tableview
        sortedData.comparatorProperty().bind(student_table.comparatorProperty());
        
        //add sorted and filtered data to the table
        student_table.setItems(sortedData); 
    }
    
     @FXML
    private void filterStudentTableSearch(MouseEvent event) {
        
        changeTableStudentOnSearch();
    }
    
    private void changeTableBookOnSearch(){
        
        //wrap the observablelist in a FilteredList()
        FilteredList<BookDetails> filteredData =  new FilteredList<>(booksData,p -> true);
        
        //set the filter predicate whenever the filter changes
        searchAnyOnBookTable.textProperty().addListener((observable,oldValue,newValue)->{
                
            filteredData.setPredicate(BookDetails -> {
            
                //if filter text is empty, display all product
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //compare product id and product name of every product with the filter text
                String lowerCaseFilter =newValue.toLowerCase();
               
                if (BookDetails.getBookTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product Id
                } else if (BookDetails.getBookPublisher().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                } else if (BookDetails.getBookAuthor().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                } else if (BookDetails.getBookSubject().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                } else if (BookDetails.getBookID().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                }
                
                return false; // Do not match            
            });
        
        });
        
        //wrap the filteredList in a sortedList
        SortedList<BookDetails> sortedData = new SortedList<>(filteredData);
        
        //bind the sortedData to the Tableview
        sortedData.comparatorProperty().bind(book_table.comparatorProperty());
        
        //add sorted and filtered data to the table
        book_table.setItems(sortedData); 
    }
    
    @FXML
    private void filterBookTableSearch(MouseEvent event) {
        
        changeTableBookOnSearch();
    }    
    
    
    private void changeTableIssuedBooksOnSearch(){
        
        //wrap the observablelist in a FilteredList()
        FilteredList<IssuedBooks> filteredData =  new FilteredList<>(issuedbooksData,p -> true);
        
        //set the filter predicate whenever the filter changes
        isssuedBook_givenStudentID.textProperty().addListener((observable,oldValue,newValue)->{
                
            filteredData.setPredicate(IssuedBooks -> {
            
                //if filter text is empty, display all product
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //compare product id and product name of every product with the filter text
                String lowerCaseFilter =newValue.toLowerCase();
               
                if (IssuedBooks.getStudentID().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product Id
                } else if (IssuedBooks.getStudentName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                }
                return false; // Do not match            
            });
        
        });
        
        //wrap the filteredList in a sortedList
        SortedList<IssuedBooks> sortedData = new SortedList<>(filteredData);
        
        //bind the sortedData to the Tableview
        sortedData.comparatorProperty().bind(issuedBook_table.comparatorProperty());
        
        //add sorted and filtered data to the table
        issuedBook_table.setItems(sortedData); 
    }  
    
    @FXML
    private void filterIssuedBookTable(MouseEvent event) {
        changeTableIssuedBooksOnSearch();
    }
    
    private void changeTableLostBooksOnSearch(){
        
        //wrap the observablelist in a FilteredList()
        FilteredList<LostBooks> filteredData =  new FilteredList<>(lostbooksData,p -> true);
        
        //set the filter predicate whenever the filter changes
        lostbook_searchStudentID.textProperty().addListener((observable,oldValue,newValue)->{
                
            filteredData.setPredicate(LostBooks -> {
            
                //if filter text is empty, display all product
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //compare product id and product name of every product with the filter text
                String lowerCaseFilter =newValue.toLowerCase();
               
                if (LostBooks.getStudentID().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product Id
                } else if (LostBooks.getStudentName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product name
                }
                return false; // Do not match            
            });
        
        });
        
        //wrap the filteredList in a sortedList
        SortedList<LostBooks> sortedData = new SortedList<>(filteredData);
        
        //bind the sortedData to the Tableview
        sortedData.comparatorProperty().bind(table_lostBook.comparatorProperty());
        
        //add sorted and filtered data to the table
        table_lostBook.setItems(sortedData); 
    }

    @FXML
    private void filterLostBookTableOnSearch(MouseEvent event) {
        changeTableLostBooksOnSearch();
    }    
    
    
    
    //##################### END SPECIAL METHODS TO HANDLE MULTIPLE TABLE SEARCHES ##
    
    

    
    //####################### METHODS TO HANDLE STUDENT DETAILS ################
    
    //register student
    @FXML
    private void student_register(ActionEvent event) throws SQLException {
         if (studentID.getText().isEmpty() || studentName.getText().isEmpty()
                 ||studentChooseClass.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (studentID.getText().isEmpty()) {
                     msg = "Student ID is empty, you must enter it";                 
                } else if (studentName.getText().isEmpty()) {
                    msg = "Student Name is empty, you must enter it";
                }else if (studentChooseClass.getSelectionModel().isEmpty()) {
                    msg = "Student class is empty, you must choose from the drop-down menu";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
             
                String sql4 = "SELECT * FROM students WHERE student_id = ?  ";
                pstmt = con.prepareStatement(sql4);
                pstmt.setString(1, studentID.getText());
                rs =  pstmt.executeQuery(); 
                
                if (rs.next()) {
            
                    String add2 = rs.getString("student_name");
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Registration Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The student ID provided belongs to an already registered student known as:\n"
                                + add2 +"\n\nProvide a UNIQUE ID for every new student registered.");
                    errorIssue.showAndWait(); 
                    
                } else {
                    
                     //show confirm alert before registering data of issued
                    String ms = " Are you sure you want to register student = "+studentName.getText()+"of ID  = "+studentID.getText();           
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm student registration");
                    alert.setGraphic(studentImageView);
                    alert.setHeaderText(null);
                    alert.setContentText(ms);

                    Optional<ButtonType> response = alert.showAndWait();            
                    if (response.get()==ButtonType.OK) {
                       try {
                            String sql = "INSERT INTO students (student_id,student_name,student_class,parentEmail) VALUES(?,?,?,?)";
                            pstmt= con.prepareStatement(sql);

                            pstmt.setString(1, studentID.getText());
                            pstmt.setString(2, studentName.getText());
                            pstmt.setString(3, studentChooseClass.getSelectionModel().getSelectedItem().toString());
                           
                            if (studentParentEmail.getText().isEmpty()) {
                               pstmt.setString(4, "No parent's email");
                           } else {
                                if (validateEmail(studentParentEmail)) {
                                    pstmt.setString(4, studentParentEmail.getText());
                                }
                           }

                            int i = pstmt.executeUpdate(); // load data into the database             
                            if (i>0) {
                                //show alert before registering data of employee
                            String msg = " Student ID = "+studentID.getText()+", "+studentName.getText()+ " has been registered and ready to receive a book.";
                            Alert al = new Alert(Alert.AlertType.INFORMATION);
                            al.setTitle("Registration Information Dialog");
                            al.setGraphic(imageSuccess);
                            al.setHeaderText(null);
                            al.setContentText(msg);
                            al.showAndWait();

                            refresh_studentDeatailTable();
                             clearStudentField();// clear fields.
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Failed");
                                error.setGraphic(errorImage);
                                error.setContentText("OOPS! Could not register Student due  to internal error.");
                                error.showAndWait();
                            } 
                            pstmt.close();

                        } catch (Exception e) {
                            Alert at = new Alert(Alert.AlertType.ERROR);
                            at.setTitle("Error in registration of student");
                            at.setGraphic(errorImage);
                            at.setHeaderText(null);
                            at.setContentText("OOPS! Could not register Student due  to internal error. " +e);
                            at.showAndWait();                         
                        }finally {
                           try {
                                rs.close();
                                pstmt.close();
                            } catch (Exception e) {}               
                       }
                    }
                }         
        }
    }
    
    
    @FXML
    private void student_update(ActionEvent event) {
        if (studentID.getText().isEmpty() || studentName.getText().isEmpty()||studentSearchID.getText().isEmpty()
                 ||studentChooseClass.getSelectionModel().isEmpty()) { 
                 
                 String msg = null;
            
                if (studentID.getText().isEmpty()) {
                     msg = "Student ID is empty, you must enter it";                 
                } else if (studentName.getText().isEmpty()) {
                    msg = "Student Name is empty, you must enter it";
                }else if (studentChooseClass.getSelectionModel().isEmpty()) {
                    msg = "Student class is empty, you must choose from the drop-down menu";
                } else if (studentSearchID.getText().isEmpty()) {
                    msg = "Student ID is empty, you must enter the student ID to retrieve the student first for update";
                }
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student has failed update.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to Update student = "+studentName.getText()+"of ID  = "+studentID.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm student update");
            alert.setGraphic(studentImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE students SET student_id=?,student_name=?,student_class=?,parentEmail=?"
                            + "  WHERE student_id = '"+studentSearchID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, studentID.getText());
                    pstmt.setString(2, studentName.getText());
                    pstmt.setString(3, studentChooseClass.getSelectionModel().getSelectedItem().toString());
                    
                      if (studentParentEmail.getText().isEmpty()) {
                               pstmt.setString(4, "No parent's email");
                      } else {
                         if (validateEmail(studentParentEmail)) {
                               pstmt.setString(4, studentParentEmail.getText());
                         }
                      }

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Student ID = "+studentID.getText()+", "+studentName.getText()+ " has been registered and ready to receive a book.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Update Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                     clearStudentField();// clear fields.
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not update Student due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in Update of student");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update Student due  to internal error. ");
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }
                
        }
         refresh_studentDeatailTable();
    }
    
    @FXML
    private void studentSearch(ActionEvent event) {
        
        if (studentSearchID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student has failed Search operation.\n"
                        + "Ensure the student ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM students WHERE student_id = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, studentSearchID.getText());

                rs =  pstmt.executeQuery(); 
                if (rs.next()) {
                    
                    String add1 = rs.getString("student_id");
                    studentID.setText(add1); 
                    String add2 = rs.getString("student_name");
                    studentName.setText(add2);
                    String add3 = rs.getString("student_class");
                    promote_studentClass.setText(add3);
                    String add4 = rs.getString("parentEmail");
                    studentParentEmail.setText(add4);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid student");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or student does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }


    @FXML
    private void student_delete(ActionEvent event) {
        
         
        if (studentID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student has failed DELETE operation.\n"
                        + "Ensure the student ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Student");
            alert.setGraphic(studentImageView);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to delete student?");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                try {
               String query = "DELETE FROM students WHERE student_id = ? ";
               pstmt = con.prepareStatement(query);
               pstmt.setString(1, studentID.getText());
               pstmt.executeUpdate();            
               pstmt.close();            
               } catch (SQLException ex) {
                   Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
               }finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
                }
                      
           }
            
           refresh_studentDeatailTable();
           clearStudentField();

        }
    }

    @FXML
    private void student_clear(ActionEvent event) {
        
        clearStudentField();
    }
    
    @FXML
    private void studentTableMouseClick(MouseEvent event) {
        try {
                    StudentDetails std = student_table.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM students WHERE student_id = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, std.getStudentID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String add1 = rs.getString("student_id");
                        studentID.setText(add1);
                        studentSearchID.setText(add1);
                        String add2 = rs.getString("student_name");
                        studentName.setText(add2);
                        String add3 = rs.getString("student_class");
                        promote_studentClass.setText(add3);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid student");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or student does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }
    }

    @FXML
    private void studentTableKeyRealease(KeyEvent event) {
        
          student_table.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
                
                try {
                    StudentDetails std = student_table.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM students WHERE student_id = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, std.getStudentID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String add1 = rs.getString("student_id");
                        studentID.setText(add1);
                        studentSearchID.setText(add1);
                        String add2 = rs.getString("student_name");
                        studentName.setText(add2);
                        String add3 = rs.getString("student_class");
                        promote_studentClass.setText(add3);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid student");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or student does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }
            }
        });
    }

    
    private void clearStudentField(){
        studentID.clear();
        studentName.clear();
        promote_studentClass.setText(null);
        refreshComboClass();
    }
    
    public void refresh_studentDeatailTable(){
        studentDetailsData.clear();
        update_studentDetailTable();
    }
    
     // method to load Content to the vendor table
     public void update_studentDetailTable(){ 
        column_studentID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        column_studentNmae.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        column_studentClass.setCellValueFactory(new PropertyValueFactory<>("studentClass"));
        column_studentRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("studentRegDate"));
                
        //customize data rendering for this column
                    //column_studentEnrolled.setCellValueFactory(new PropertyValueFactory<>("studentEnrolled"));
        column_studentEnrolled.setCellFactory(col ->{
        
            TableCell<StudentDetails,String> cell = new TableCell<StudentDetails,String>(){
            
                @Override
                public void updateItem(String item,boolean empty){
                    super.updateItem(item, empty);
                    
                    if (!empty) {
                        if (this.getTableRow() != null) {
                            //get the cell
                            int rowIndex = this.getTableRow().getIndex();
                            StudentDetails sd = this.getTableView().getItems().get(rowIndex);
                            String isEnrolled = sd.getStudentEnrolled();                            
                            this.setText(isEnrolled);
                            
                            if (isEnrolled.equalsIgnoreCase("yes")) {
                                this.setTextFill(Paint.valueOf("green"));
                            } else {
                                this.setTextFill(Paint.valueOf("red"));
                            } 
                        }
                    }               
                }
                
            };
            return cell;
        });         
        
        
        try { 
            String sql = "SELECT student_id,student_name,student_class,student_regdate,student_isAvail FROM students";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
                String isAvail = null;
                boolean i = rs.getBoolean("student_isAvail");
                if (i) {  isAvail = "YES";} else { isAvail = "NO";}
                
                studentDetailsData.add( new StudentDetails(
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("student_class"),
                        rs.getString("student_regdate"),
                        isAvail                       
                ));         
            }
            //load items to the table
            student_table.setItems(studentDetailsData);
            student_table.setPlaceholder(new Label("No student record match the provided details", errorImage));
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }    
    
    
    //####################### METHODS TO HANDLE STUDENT CLASS DETAILS ##########
    private void updateComboClass(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                classNameData.add( rs.getString("className"));         
            }
            // populate class combo box 
            studentChooseClass.setItems(classNameData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
    // call this from anatha class
    private void refreshComboClass(){    
        classNameData.clear();
        updateComboClass();
    }
    
    @FXML
    private void refresh_classCombo(ActionEvent event) {
        refreshComboClass();
    }

    
     //####################### METHODS TO HANDLE BOOKS DETAILS ##########
 
    
    @FXML
    private void book_searchOperation(ActionEvent event) {
        
        if (bookSearchID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Book has failed Search operation.\n"
                        + "Ensure the Book ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM book WHERE book_id = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, bookSearchID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("book_id");
                    bookID.setText(add1); 
                    String add2 = rs.getString("book_class");
                    bookClass.setText(add2);
                    String add4 = rs.getString("book_author");
                    bookAuthor.setText(add4);
                    String add5 = rs.getString("book_title");
                    BookTitle.setText(add5);
                    String add6 = rs.getString("book_publisher");
                    bookPublisher.setText(add6);
                    String add7 = rs.getString("book_regdate");
                    book_registeredDateText.setVisible(true);
                    book_registeredDate.setVisible(true);
                    book_registeredDate.setText(add7);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Book");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or book does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }

    @FXML
    private void bookRegister(ActionEvent event) throws SQLException {        
        
        if (bookID.getText().isEmpty() || bookAuthor.getText().isEmpty()||bookClass.getText().isEmpty() || bookPublisher.getText().isEmpty()
                || BookTitle.getText().isEmpty()||bookSubject.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (bookID.getText().isEmpty()) {
                     msg = "Book ID is empty, you must enter it";                 
                } else if (bookAuthor.getText().isEmpty()) {
                    msg = "Book author is empty, you must enter it";
                } else if (bookClass.getText().isEmpty()) {
                    msg = "Book class/level is empty, you must enter it";
                } else if (bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (BookTitle.getText().isEmpty()) {
                    msg = "Book Title is empty, you must enter it";
                }  else if (bookSubject.getSelectionModel().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  


              String sql4 = "SELECT * FROM book WHERE book_id = ?  ";
                pstmt = con.prepareStatement(sql4);
                pstmt.setString(1, bookID.getText());
                rs =  pstmt.executeQuery(); 
                
                if (rs.next()) {
            
                    String add2 = rs.getString("book_title");
                    String add3 = rs.getString("book_publisher");
                    String add4 = rs.getString("book_class");
                    String add5 = rs.getString("book_category");
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Registration Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The book ID provided belongs to an already registered book with following details:\n"
                                +"Book title: "+ add2 +"\nBook subject: "+add5+"\nBook class: "+add4+"\nBook Publisher: "+add3+"\n\n"
                            + "Provide a unique book Id to proceed.");
                    errorIssue.showAndWait(); 
                    
                } else {
                    
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to register book ID, "+bookID.getText()+","+BookTitle.getText();           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book registration");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "INSERT INTO book (book_id,book_class,book_category,book_author,book_title,book_publisher) VALUES(?,?,?,?,?,?)";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setString(1, bookID.getText());
                                pstmt.setString(2, bookClass.getText());
                                pstmt.setString(3, bookSubject.getSelectionModel().getSelectedItem().toString());
                                pstmt.setString(4, bookAuthor.getText());
                                pstmt.setString(5, BookTitle.getText());
                                pstmt.setString(6, bookPublisher.getText());

                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " Book ID  "+bookID.getText()+", "+BookTitle.getText()+ ", has been registered and ready to be given to a student.";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Registration Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                refreash_bookTable();
                                clearBookFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not register book due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not register book due  to internal error. " +e);
                                at.showAndWait();                         
                            }finally {
                               try {
                                    rs.close();
                                    pstmt.close();
                                } catch (Exception e) {}               
                           }
                        }                      
                }    
        }       
        
    }

    @FXML
    private void bookUpdate(ActionEvent event) {
        
        if (bookID.getText().isEmpty() || bookAuthor.getText().isEmpty()||bookClass.getText().isEmpty() || bookPublisher.getText().isEmpty()
                || BookTitle.getText().isEmpty()||bookSubject.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (bookID.getText().isEmpty()) {
                     msg = "Book ID is empty, you must enter it";                 
                } else if (bookAuthor.getText().isEmpty()) {
                    msg = "Book author is empty, you must enter it";
                } else if (bookClass.getText().isEmpty()) {
                    msg = "Book class/level is empty, you must enter it";
                } else if (bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (BookTitle.getText().isEmpty()) {
                    msg = "Book Title is empty, you must enter it";
                }  else if (bookSubject.getSelectionModel().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book has failed Update.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to update book ID, "+bookID.getText()+","+BookTitle.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm book update");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE book SET book_id=?,book_class=?,book_category=?,book_author=?,book_title=?,book_publisher=? WHERE book_id = '"+bookSearchID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, bookID.getText());
                    pstmt.setString(2, bookClass.getText());
                    pstmt.setString(3, bookSubject.getSelectionModel().getSelectedItem().toString());
                    pstmt.setString(4, bookAuthor.getText());
                    pstmt.setString(5, BookTitle.getText());
                    pstmt.setString(6, bookPublisher.getText());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Book ID  "+bookID.getText()+", "+BookTitle.getText()+ ", has been update.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Update Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearBookFields();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setHeaderText(null);
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not update book due  to internal error.\n"
                                + "Ensure Book ID is filled in the search field");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in updating of book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update book due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }
                
        }
        refresh_issueBookTable();
        refreash_bookTable();
    }

    @FXML
    private void bookDelete(ActionEvent event) {
        
          
        if (bookID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book has failed DELETE operation.\n"
                        + "Ensure the Book ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Book");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to delete this book?");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                try {
               String query = "DELETE FROM book WHERE book_id = ? ";
               pstmt = con.prepareStatement(query);
               pstmt.setString(1, bookID.getText());
               pstmt.executeUpdate();            
               pstmt.close(); 
               
               clearBookFields();
               
               } catch (SQLException ex) {
                   Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
               }finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
                }
                      
           }
            
           refreash_bookTable();

        }
    }

    @FXML
    private void bookClearFields(ActionEvent event) {
        clearBookFields();
    }
    
    @FXML
    private void bookTableMouseClicked(MouseEvent event) {
        
        try {
                    BookDetails bkd = book_table.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM book WHERE book_id = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, bkd.getBookID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String add1 = rs.getString("book_id");
                        bookID.setText(add1); 
                        bookSearchID.setText(add1);
                        String add2 = rs.getString("book_class");
                        bookClass.setText(add2);
                        String add4 = rs.getString("book_author");
                        bookAuthor.setText(add4);
                        String add5 = rs.getString("book_title");
                        BookTitle.setText(add5);
                        String add6 = rs.getString("book_publisher");
                        bookPublisher.setText(add6);
                        String add7 = rs.getString("book_regdate");
                        book_registeredDateText.setVisible(true);
                        book_registeredDate.setVisible(true);
                        book_registeredDate.setText(add7);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid book");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or book does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }        
    }

    @FXML
    private void bookTableKeyReleased(KeyEvent event) {
        
         book_table.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
                
                try {
                    BookDetails bkd = book_table.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM book WHERE book_id = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, bkd.getBookID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String add1 = rs.getString("book_id");
                        bookID.setText(add1); 
                        bookSearchID.setText(add1);
                        String add2 = rs.getString("book_class");
                        bookClass.setText(add2);
                        String add4 = rs.getString("book_author");
                        bookAuthor.setText(add4);
                        String add5 = rs.getString("book_title");
                        BookTitle.setText(add5);
                        String add6 = rs.getString("book_publisher");
                        bookPublisher.setText(add6);
                        String add7 = rs.getString("book_regdate");
                        book_registeredDateText.setVisible(true);
                        book_registeredDate.setVisible(true);
                        book_registeredDate.setText(add7);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid book");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or book does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }
            }
        });
        
    }

    
    private void clearBookFields(){
        book_registeredDateText.setVisible(false);
        book_registeredDate.setVisible(false);
        bookAuthor.clear();
        bookClass.clear();
        bookID.clear();
        bookPublisher.clear();
        BookTitle.clear();
        refreshComboSubjects();
             
    
    }
    
    // method to load Content to the book table
     public void update_bookDetailTable(){ 
        book_columnID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        book_columnClass.setCellValueFactory(new PropertyValueFactory<>("bookClass"));
        book_columnSubject.setCellValueFactory(new PropertyValueFactory<>("bookSubject"));
        book_columnAuthor.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        book_columnTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        book_columnPublisher.setCellValueFactory(new PropertyValueFactory<>("bookPublisher"));
        book_columnEnteredDate.setCellValueFactory(new PropertyValueFactory<>("bookEnterDate"));
        
        // customize data rendering for this column
                    //book_columnAvailable.setCellValueFactory(new PropertyValueFactory<>("bookAvailable"));        
        book_columnAvailable.setCellFactory(col ->{
        
            TableCell<BookDetails,String> cell = new TableCell<BookDetails,String>(){
            
                @Override
                public void updateItem(String item,boolean empty){
                    super.updateItem(item, empty);
                    
                    if (!empty) {
                        if (this.getTableRow() != null) {
                            //get the cell
                            int rowIndex = this.getTableRow().getIndex();
                            BookDetails bd= this.getTableView().getItems().get(rowIndex);
                            String isAvailable = bd.getBookAvailable();                            
                            this.setText(isAvailable);
                            
                            if (isAvailable.equalsIgnoreCase("yes")) {
                                this.setTextFill(Paint.valueOf("green"));
                            } else {
                                this.setTextFill(Paint.valueOf("red"));
                            } 
                        }
                    }               
                }
                
            };
            return cell;
        });        
        
        
        try { 
            String sql = "SELECT book_id,book_class,book_category,book_author,book_title,book_publisher,book_regdate,book_isAvail FROM book";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
                String isAvail = null;
                boolean i = rs.getBoolean("book_isAvail");
                if (i) { isAvail = "YES";} else { isAvail = "NO";}
                
                booksData.add( new BookDetails(
                        rs.getString("book_id"),
                        rs.getString("book_class"),
                        rs.getString("book_category"),
                        rs.getString("book_author"),                        
                        rs.getString("book_title"),
                        rs.getString("book_publisher"),
                        rs.getString("book_regdate"),
                        isAvail                       
                ));         
            }
            //load items to the table
            book_table.setItems(booksData);
            book_table.setPlaceholder(new Label("No book record matches the details provided", errorImage));
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }  

     
     
     
    private void refreash_bookTable(){
        booksData.clear();
        update_bookDetailTable();
    
    } 
    
    private void updateBookSubjects(){  
        try { 
            String sql = "SELECT bookSubject FROM bookcategory";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                bookSubjectData.add( rs.getString("bookSubject"));
            }
            
            // populate class combo box 
            bookSubject.setItems(bookSubjectData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
       
     // call this from anatha class
    public void refreshComboSubjects(){    
        bookSubjectData.clear();
        updateBookSubjects();
    }
    
     @FXML
    private void refresh_SubjectCombo(ActionEvent event) {
        refreshComboSubjects();
    }
     
    //############################# end of methods to handle book detail #######
    
    //########################## START METHOD TO HANDLE ISSUE BOOK DETAILS #####
    
    @FXML
    private void issuebook_searchStudentOperation(ActionEvent event) {
        
        if (issuebook_searchStudentID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student has failed Search operation.\n"
                        + "Ensure the student ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM students WHERE student_id = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, issuebook_searchStudentID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("student_id");
                    issueBook_studentID.setText(add1); 
                    String add2 = rs.getString("student_name");
                    issueBook_studentName.setText(add2);
                    String add3 = rs.getString("student_class");
                    issueBook_studentClass.setText(add3);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid student");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or student does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
        
    }

    @FXML
    private void issuebook_searchBookOperation(ActionEvent event) {
        if (issuebook_searchBookID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Book has failed Search operation.\n"
                        + "Ensure the Book ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM book WHERE book_id = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, issuebook_searchBookID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("book_id");
                    issuebook_bookID.setText(add1); 
                    String add2 = rs.getString("book_class");
                    issuebook_bookClass.setText(add2);
                    String add4 = rs.getString("book_category");
                    issuebook_bookSubject.setText(add4);
                    String add5 = rs.getString("book_title");
                    issuebook_bookTtle.setText(add5);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Book");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or book does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }

    @FXML
   private void isssuebook_register(ActionEvent event) {
       
        if (issueBook_studentID.getText().isEmpty() || issueBook_studentName.getText().isEmpty()||issuebook_bookClass.getText().isEmpty() || issuebook_bookID.getText().isEmpty()
                || issuebook_bookSubject.getText().isEmpty()|| issuebook_bookTtle.getText().isEmpty()|| issuebook_teacherIssuing.getText().isEmpty()||issuebook_bookStatus.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (issueBook_studentID.getText().isEmpty() || issueBook_studentName.getText().isEmpty()) {
                     msg = "Student ID and Student name are missing, Enter student ID to search for student details.";                 
                } else if (issuebook_bookClass.getText().isEmpty() || issuebook_bookID.getText().isEmpty()
                || issuebook_bookSubject.getText().isEmpty()|| issuebook_bookTtle.getText().isEmpty()) {
                   msg = "Book ID and other book details are missing, Enter book ID to search for book details.";
                } else if (issuebook_teacherIssuing.getText().isEmpty()) {
                    msg = "Teacher issuing the book is missing, you must enter him/her";
                } else if (issuebook_bookStatus.getSelectionModel().isEmpty()) {
                    msg = "Book status or condition is empty, you must choose from the drop-down menu";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book issue has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to issue book ID, "+issuebook_bookID.getText()+","+issuebook_bookTtle.getText() +" to "+ issueBook_studentName.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm book Issue");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "INSERT INTO issuedbook (bookID,bookTitle,bookClass,bookCategory,studentID,studentName,bookStatus,teacherIssued,studentClass) VALUES(?,?,?,?,?,?,?,?,?)";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, issuebook_bookID.getText());
                    pstmt.setString(2, issuebook_bookTtle.getText());                    
                    pstmt.setString(3, issuebook_bookClass.getText());
                    pstmt.setString(4, issuebook_bookSubject.getText());
                    pstmt.setString(5, issueBook_studentID.getText());
                    pstmt.setString(6, issueBook_studentName.getText());                    
                    pstmt.setString(7, issuebook_bookStatus.getSelectionModel().getSelectedItem().toString());
                    pstmt.setString(8, issuebook_teacherIssuing.getText());
                    pstmt.setString(9, issueBook_studentClass.getText());

                    int i = pstmt.executeUpdate(); // load data into the database 
                    
                    //update the book table to make the particular book unavailable after issue
                    String sql2 = "UPDATE book SET book_isAvail=? WHERE book_id = '"+issuebook_bookID.getText()+"'";
                    pstmt= con.prepareStatement(sql2);
                    pstmt.setBoolean(1, false);            
                    int ii = pstmt.executeUpdate(); // load updated  data into the database                    
                    
                    if (i>0 && ii>0) {
                        //show alert before registering data of employee
                    String msg = " Book ID  "+issuebook_bookID.getText()+", "+issuebook_bookTtle.getText()+ ", has been issued to a student.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Book issue Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearissuedBookFields();//clear all data
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not issue book due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in issue of book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not issue book due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }
                
        }
        
        refresh_issueBookTable();
        refreash_bookTable();
        
    }

    @FXML
    private void isssuebook_clearFields(ActionEvent event) {
        clearissuedBookFields();
    }
    
    @FXML
    private void issuedbook_searchIssuedBookID(ActionEvent event) {
        
        if (issuebook_givenBookID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Book has failed Search operation.\n"
                        + "Ensure the Issued Book ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM issuedbook WHERE bookID= ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, issuebook_givenBookID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("studentID");
                    issueBook_studentID.setText(add1); 
                    String add2 = rs.getString("studentName");
                    issueBook_studentName.setText(add2);
                    String add13 = rs.getString("studentClass");
                    issueBook_studentClass.setText(add13);
                    
                    String add3 = rs.getString("bookID");
                    issuebook_bookID.setText(add3); 
                    String add4 = rs.getString("bookClass");
                    issuebook_bookClass.setText(add4);
                    String add5 = rs.getString("bookCategory");
                    issuebook_bookSubject.setText(add5);
                    String add6 = rs.getString("bookTitle");
                    issuebook_bookTtle.setText(add6);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Book");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or book was not issued.");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }
    
    @FXML
    private void issuedbook_searchIssuedStudentID(ActionEvent event) {
        
         if (isssuedBook_givenStudentID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Book has failed Search operation.\n"
                        + "Ensure the student ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM issuedbook WHERE studentID= ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, isssuedBook_givenStudentID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("studentID");
                    issueBook_studentID.setText(add1); 
                    String add2 = rs.getString("studentName");
                    issueBook_studentName.setText(add2);
                    String add13 = rs.getString("studentClass");
                    issueBook_studentClass.setText(add13);
                    
                    String add3 = rs.getString("bookID");
                    issuebook_bookID.setText(add3); 
                    String add4 = rs.getString("bookClass");
                    issuebook_bookClass.setText(add4);
                    String add5 = rs.getString("bookCategory");
                    issuebook_bookSubject.setText(add5);
                    String add6 = rs.getString("bookTitle");
                    issuebook_bookTtle.setText(add6);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Book");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or book was not issued.");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }
    
    @FXML
    private void issuebook_returnBookOperation(ActionEvent event) {
        
        if (issuebook_bookID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Book Submission Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Book has failed Submit operation.\n"
                        + "Ensure the Issued Book ID has been inserted and search"
                    + " the book to ensure that the book was given to student before submission.");
            errorIssue.showAndWait(); 
            
        }else {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Receive Book");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to receive submited book and clear the student or students who had been issued the book?\n"
                    + "Ensure the book is at or almost in the condition/status it was issued. If the book is totally destroyed by student, request a replacement");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                
                try {
                //update the book table to make the particular book available after issue
                    String sql2 = "UPDATE book SET book_isAvail=? WHERE book_id = '"+issuebook_bookID.getText()+"'";
                    pstmt= con.prepareStatement(sql2);
                    pstmt.setBoolean(1, true);            
                    int i = pstmt.executeUpdate(); // load updated  data into the database
                    
                                      
                if (i>0) {
                    
                    String query = "DELETE FROM issuedbook WHERE bookID = ? ";
                    pstmt = con.prepareStatement(query);
                    pstmt.setString(1, issuebook_bookID.getText());
                    pstmt.executeUpdate();
                    
                    Alert rt = new Alert(Alert.AlertType.INFORMATION);
                    rt.setTitle("Succesful");
                    rt.setHeaderText(null);
                    rt.setGraphic(bookImageView);
                    rt.setContentText("The book has been submitted and student(s) who were responsible for it have been cleared.");
                    rt.showAndWait();
                    
                    clearissuedBookFields();
                      
                }else {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Invalid Book");
                    alert1.setHeaderText(null);
                    alert1 .setGraphic(errorImage);
                    alert1.setContentText("Record not Found or book was not issued.");
                    alert1.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }                 
            }             
        }
        
        refreash_bookTable();
        refresh_issueBookTable();
    }
    
    
    @FXML
    private void issuedBook_tableMouseClicked(MouseEvent event) {
        
                try {
                    IssuedBooks std = issuedBook_table.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM issuedbook WHERE bookID= ?  ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, std.getBookID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                    String add1 = rs.getString("studentID");
                    issueBook_studentID.setText(add1); 
                    String add2 = rs.getString("studentName");
                    issueBook_studentName.setText(add2);
                    String add13 = rs.getString("studentClass");
                    issueBook_studentClass.setText(add13);
                    
                    String add3 = rs.getString("bookID");
                    issuebook_bookID.setText(add3); 
                    String add4 = rs.getString("bookClass");
                    issuebook_bookClass.setText(add4);
                    String add5 = rs.getString("bookCategory");
                    issuebook_bookSubject.setText(add5);
                    String add6 = rs.getString("bookTitle");
                    issuebook_bookTtle.setText(add6);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid book");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or book issued does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }        
        
    }

    @FXML
    private void issuedBook_tableKeyReleased(KeyEvent event) {
        
        
          issuedBook_table.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
                
                try {
                    IssuedBooks std = issuedBook_table.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM issuedbook WHERE bookID= ?  ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, std.getBookID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                    String add1 = rs.getString("studentID");
                    issueBook_studentID.setText(add1); 
                    String add2 = rs.getString("studentName");
                    issueBook_studentName.setText(add2);
                    String add13 = rs.getString("studentClass");
                    issueBook_studentClass.setText(add13);
                    
                    String add3 = rs.getString("bookID");
                    issuebook_bookID.setText(add3); 
                    String add4 = rs.getString("bookClass");
                    issuebook_bookClass.setText(add4);
                    String add5 = rs.getString("bookCategory");
                    issuebook_bookSubject.setText(add5);
                    String add6 = rs.getString("bookTitle");
                    issuebook_bookTtle.setText(add6);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid book");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or book issued does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                    ev.printStackTrace();
                }
            }
        });        
    }
    
    

    @FXML
    private void isssuebook_LostBookOperation(ActionEvent event) {
        
        if (issueBook_studentID.getText().isEmpty() || issueBook_studentName.getText().isEmpty()||issuebook_bookClass.getText().isEmpty() || issuebook_bookID.getText().isEmpty()
                || issuebook_bookSubject.getText().isEmpty()|| issuebook_bookTtle.getText().isEmpty()) {
             
                 String msg = null;
            
                if (issueBook_studentID.getText().isEmpty() || issueBook_studentName.getText().isEmpty()) {
                     msg = "Student ID and Student name are missing, Enter student ID to search for student details.";                 
                } else if (issuebook_bookClass.getText().isEmpty() || issuebook_bookID.getText().isEmpty()
                || issuebook_bookSubject.getText().isEmpty()|| issuebook_bookTtle.getText().isEmpty()) {
                   msg = "Book ID and other book details are missing, Enter book ID to search for lost book details.";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Lost book issue has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to register lost book ID, "+issuebook_bookID.getText()+","+issuebook_bookTtle.getText() +" by "+ issueBook_studentName.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm lost book registration");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "INSERT INTO lostbooks (lostBookID,lostBookTitle,lostBookClass,lostbookSubject,studentID,studentName,studentClass) VALUES(?,?,?,?,?,?,?)";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, issuebook_bookID.getText());
                    pstmt.setString(2, issuebook_bookTtle.getText());                    
                    pstmt.setString(3, issuebook_bookClass.getText());
                    pstmt.setString(4, issuebook_bookSubject.getText());
                    pstmt.setString(5, issueBook_studentID.getText());
                    pstmt.setString(6, issueBook_studentName.getText()); 
                    pstmt.setString(7, issueBook_studentClass.getText());

                    int i = pstmt.executeUpdate(); // load data into the database 
                    
                    if (i>0) {
                        
                    String query = "DELETE FROM book WHERE book_id = ? ";
                    pstmt = con.prepareStatement(query);
                    pstmt.setString(1, issuebook_bookID.getText());
                    pstmt.executeUpdate();
                    
                    String query2 = "DELETE FROM issuedbook WHERE bookID = ?  ";
                    pstmt = con.prepareStatement(query2);
                    pstmt.setString(1, issuebook_bookID.getText());
//                    pstmt.setString(2, issueBook_studentID.getText());
                    pstmt.executeUpdate();
                        
                        //show alert before registering data of employee
                    String msg = " Book ID  "+issuebook_bookID.getText()+", "+issuebook_bookTtle.getText()+ ", has been registered as a lost book.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Lost Book Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not register lost book due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in registration of lost book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not register lost book due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }                
        }  
        
        refresh_lostBookTable();
        refreash_bookTable();
        refresh_issueBookTable();
    }
    
    @FXML
    private void importIssuedBooksFromExcel(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Import issued books data");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Only data in Excel format and with an extension of .xlsx can be imported in the correct format.\n\n"
                    + "Are you sure you want to import issued book data from outside the system? ");
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
                
                fileChooserOpener();
                   fileChooser.setTitle("Select Student/User File Name");
                   //single File selection
                   file = fileChooser.showOpenDialog(Stage);
                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        
                        try {
                            String sql = "INSERT INTO issuedbook"
                                    + " (bookID,bookTitle,bookClass,bookCategory,studentID,"
                                    + "studentName,bookStatus,teacherIssued,studentClass)"
                                    + " VALUES(?,?,?,?,?,?,?,?,?)";
                            pstmt= con.prepareStatement(sql);
                            
                            FileInputStream fileIn = new FileInputStream(new File(filePath));
                            
                            XSSFWorkbook wb = new XSSFWorkbook(fileIn);
                            XSSFSheet sheet = wb.getSheetAt(0);
                            Row row;
                             
                            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                                
                                row = sheet.getRow(i);
                                
                                    if (isRowValueDouble(row)) {
                                        int content = (int) row.getCell(0).getNumericCellValue();
                                        pstmt.setString(1, String.valueOf(content));
                                    } else {
                                        pstmt.setString(1, row.getCell(0).getStringCellValue());
                                    }                                                                                                 
                                
                                pstmt.setString(2, row.getCell(1).getStringCellValue());
                                pstmt.setString(3, row.getCell(2).getStringCellValue());
                                pstmt.setString(4, row.getCell(3).getStringCellValue());
                                pstmt.setString(5, row.getCell(4).getStringCellValue());
                                pstmt.setString(6, row.getCell(5).getStringCellValue());
                                pstmt.setString(7, row.getCell(6).getStringCellValue());
                                pstmt.setString(8, row.getCell(7).getStringCellValue());
                                pstmt.setString(9, row.getCell(8).getStringCellValue());
                                pstmt.execute();
                                 
                                   
                            }
                            
                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Successfully");
                                    alert1.setGraphic(bookImageView);
                                    alert1.setHeaderText(null);
                                    alert1.setContentText("Successfully imported issued books data from this file.\n\n" + filePath);
                                    alert1.showAndWait();
                                    
                                    wb.close();
                                    fileIn.close();
                                    pstmt.close();
                                    
                               refresh_issueBookTable();
                            
                        } catch (SQLException | IOException e) {
                            
                            Alert at = new Alert(Alert.AlertType.ERROR);
                            at.setTitle("Error in registration of Books");
                            at.setGraphic(errorImage);
                            at.setHeaderText(null);
                            at.setContentText("Internal server Error occurred and some issued book and students' data was NOT REGISTERED. The following errors might have occured \n\n"
                                    + "1. Two or more issued books share same Book ID noted below."
                                    + " Therefore only one copy of the issued book will be registered and the other issued book copies after the duplicate will NOT be REGISTERED.\n\n"+e);
                            at.showAndWait(); 
                            
                            refresh_issueBookTable();
                            
                            e.printStackTrace();
                        }

                    }
            }  
            
    }
    
    @FXML
    private void importListsOfStudentsFromExcel(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Import students data");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Only data in Excel format and with an extension of .xlsx can be imported in the correct format.\n\n"
                    + "Are you sure you want to import students data from outside the system? ");
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
                
                fileChooserOpener();
                   fileChooser.setTitle("Select Students' data File Name");
                   //single File selection
                   file = fileChooser.showOpenDialog(Stage);
                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        
                        try {
                            String sql = "INSERT INTO students"
                                    + " (student_id,student_name,student_class,parentEmail)"
                                    + " VALUES(?,?,?,?)";
                            pstmt= con.prepareStatement(sql);
                            
                            FileInputStream fileIn = new FileInputStream(new File(filePath));
                            
                            XSSFWorkbook wb = new XSSFWorkbook(fileIn);
                            XSSFSheet sheet = wb.getSheetAt(0);
                            Row row;
                             
                            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                                
                                row = sheet.getRow(i);
                                
                                    if (isRowValueDouble(row)) {
                                        int content = (int) row.getCell(0).getNumericCellValue();
                                        pstmt.setString(1, String.valueOf(content));
                                    } else {
                                        pstmt.setString(1, row.getCell(0).getStringCellValue());
                                    }                             
                                    
                                pstmt.setString(2, row.getCell(1).getStringCellValue());
                                pstmt.setString(3, row.getCell(2).getStringCellValue());
                                pstmt.setString(4, row.getCell(3).getStringCellValue());
                                pstmt.execute();
                                 
                                   
                            }
                            
                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Successfully");
                                    alert1.setGraphic(bookImageView);
                                    alert1.setHeaderText(null);
                                    alert1.setContentText("Successfully imported students data from this file.\n\n" + filePath);
                                    alert1.showAndWait();
                                    
                                    wb.close();
                                    fileIn.close();
                                    pstmt.close();
                                    
                               refresh_studentDeatailTable();
                            
                        } catch (SQLException | IOException e) {
                            
                            Alert at = new Alert(Alert.AlertType.ERROR);
                            at.setTitle("Error in registration of Books");
                            at.setGraphic(errorImage);
                            at.setHeaderText(null);
                            at.setContentText("Internal server Error occurred and some students data was NOT REGISTERED. The following errors might have occured \n\n"
                                    + "1. Two or more students share same students ID noted below."
                                    + " Therefore only one student will be registered and the other student(s) after the duplicate will NOT be REGISTERED.\n\n"+e);
                            at.showAndWait(); 
                            
                            refresh_studentDeatailTable();
 
                           e.printStackTrace();
                        }

                    }
            }         
    }
    
    //method to validate if input is an int
    private boolean isRowValueDouble(Row row){
        try {
            double number = row.getCell(0).getNumericCellValue();
            System.out.println("Input is" + number );
            return true;
        } catch (Exception e) {
            return false;
        }
    }    
    
    

    @FXML
    private void importSchoolBooksFromExcel(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Import school books data");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Only data in Excel format and with an extension of .xlsx can be imported in the correct format.\n\n"
                    + "Are you sure you want to import school books data from outside the system? ");
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
                
                fileChooserOpener();
                   fileChooser.setTitle("Select Books File Name");
                   //single File selection
                   file = fileChooser.showOpenDialog(Stage);
                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        
                        try {
                            String sql = "INSERT INTO book"
                                    + " (book_id,book_class,book_category,book_author,book_title,"
                                    + "book_publisher)"
                                    + " VALUES(?,?,?,?,?,?)";
                            pstmt= con.prepareStatement(sql);
                            
                            FileInputStream fileIn = new FileInputStream(new File(filePath));
                            
                            XSSFWorkbook wb = new XSSFWorkbook(fileIn);
                            XSSFSheet sheet = wb.getSheetAt(0);
                            Row row;
                             
                            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                                
                                row = sheet.getRow(i);
                                
                                    if (isRowValueDouble(row)) {
                                        int content = (int) row.getCell(0).getNumericCellValue();
                                        pstmt.setString(1, String.valueOf(content));
                                    } else {
                                        pstmt.setString(1, row.getCell(0).getStringCellValue());
                                    }                             
                                
                                pstmt.setString(2, row.getCell(1).getStringCellValue());
                                pstmt.setString(3, row.getCell(2).getStringCellValue());
                                pstmt.setString(4, row.getCell(3).getStringCellValue());
                                pstmt.setString(5, row.getCell(4).getStringCellValue());
                                pstmt.setString(6, row.getCell(5).getStringCellValue());
                               
                                pstmt.execute();
                                 
                                   
                            }
                            
                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Successfully");
                                    alert1.setGraphic(bookImageView);
                                    alert1.setHeaderText(null);
                                    alert1.setContentText("Successfully imported school books data from this file.\n\n" + filePath);
                                    alert1.showAndWait();
                                    
                                    wb.close();
                                    fileIn.close();
                                    pstmt.close();
                                    
                               refreash_bookTable();                    
                            
                        } catch (SQLException | IOException e) {
                            
                            Alert at = new Alert(Alert.AlertType.ERROR);
                            at.setTitle("Error in registration of Books");
                            at.setGraphic(errorImage);
                            at.setHeaderText(null);
                            at.setContentText("Internal server Error occurred and some students data was NOT REGISTERED. The following errors might have occured \n\n"
                                    + "1. Two or more books share same Book ID noted below."
                                    + " Therefore only one copy of the book will be registered and the other book copies after the duplicate will NOT be REGISTERED.\n\n"+e);
                            at.showAndWait();   
                            
                            refreash_bookTable(); 
                        }

                    }
            }         
        
    }    

    
    @FXML
    private void issuedBook_deteteSpecificRecord(ActionEvent event) {
         if (issueBook_studentID.getText().isEmpty() || issuebook_bookID.getText().isEmpty()) {
            
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Delete Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The record has failed DELETE operation.\n"
                                + "Ensure both the student details and book details have been searched and retrieved.");
                    errorIssue.showAndWait(); 

                }else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete");
                    alert.setGraphic(studentImageView);
                    alert.setHeaderText(null);
                    alert.setContentText("Delete record of issued Book to a particular student");         
                    Optional <ButtonType> obt = alert.showAndWait();

                    if (obt.get()== ButtonType.OK) {
                        
                        try {  
                            
                            String sql = "SELECT * FROM book WHERE book_id= ?  ";
                            pstmt = con.prepareStatement(sql);
                            pstmt.setString(1, issuebook_bookID.getText());

                            rs=pstmt.executeQuery();
                            if (rs.next()) {
                                    //update the book table to make the particular book available after issue
                                 String sql2 = "UPDATE book SET book_isAvail=? WHERE book_id = '"+issuebook_bookID.getText()+"'";
                                 pstmt= con.prepareStatement(sql2);
                                 pstmt.setBoolean(1, true);            
                                 int i = pstmt.executeUpdate(); // load updated  data into the database

                                 if (i>0) {
                                        String query = "DELETE FROM issuedbook WHERE studentID = ? AND bookID = ?";
                                        pstmt = con.prepareStatement(query);
                                        pstmt.setString(1, issueBook_studentID.getText());
                                        pstmt.setString(2, issuebook_bookID.getText());
                                        pstmt.executeUpdate();            
                                        pstmt.close();

                                        refresh_issueBookTable();
                                        refreash_bookTable();
                                        clearissuedBookFields();
                                } 
                            } else {
                                
                                        String query = "DELETE FROM issuedbook WHERE studentID = ? AND bookID = ?";
                                        pstmt = con.prepareStatement(query);
                                        pstmt.setString(1, issueBook_studentID.getText());
                                        pstmt.setString(2, issuebook_bookID.getText());
                                        pstmt.executeUpdate();            
                                        pstmt.close();

                                        refresh_issueBookTable();
                                        refreash_bookTable();
                                        clearissuedBookFields();
                            } 
                            
                        } catch (Exception e) {
                        }finally {
                            try {
                                rs.close();
                                pstmt.close();
                            } catch (Exception e) {
                            }        
                        }   
                   }                   
                }
    }


    
    private void clearissuedBookFields(){
        issuebook_searchBookID.clear();
        issuebook_searchStudentID.clear();
        issuebook_teacherIssuing.clear();
        issueBook_studentID.setText(null);
        issueBook_studentName.setText(null);
        issuebook_bookID.setText(null);
        issuebook_bookClass.setText(null);
        issuebook_bookSubject.setText(null);
        issuebook_bookTtle.setText(null);
        issueBook_studentClass.setText(null);
    }
    
     // method to load Content to the book table
     public void update_issuedbookDetailTable(){ 
        issuedBook_columnBookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        issuedBook_columnBookClass.setCellValueFactory(new PropertyValueFactory<>("bookClass"));
        issuedBook_columnBookSubject.setCellValueFactory(new PropertyValueFactory<>("bookSubject"));
        issuedBook_columnBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        issuedBook_columnStudentID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        issuedBook_columnStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        issuedBook_columnIssuedBy.setCellValueFactory(new PropertyValueFactory<>("issuedBy"));
        issuedBook_columnDateIssued.setCellValueFactory(new PropertyValueFactory<>("dateIssued"));
        try { 
            String sql = "SELECT bookID,bookTitle,bookClass,bookCategory,studentID,studentName,teacherIssued,issuedDate FROM issuedbook";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
               issuedbooksData.add( new IssuedBooks(
                        rs.getString("bookID"),
                       rs.getString("bookClass"),
                        rs.getString("bookCategory"),                        
                        rs.getString("bookTitle"),                        
                        rs.getString("studentID"),
                        rs.getString("studentName"),
                        rs.getString("teacherIssued"),
                        rs.getString("issuedDate")                       
                ));         
            }
            //load items to the table
            issuedBook_table.setItems(issuedbooksData);
            issuedBook_table.setPlaceholder(new Label("No issued book record matches the details provided", errorImage));
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    } 

     
     private void refresh_issueBookTable(){
         issuedbooksData.clear();
         update_issuedbookDetailTable();
     }
    
    
    //########################## END METHOD TO HANDLE ISSUE BOOK DETAILS #####
     
    //########################## START METHODS TO HANDLE LOST BOOK DETAILS #####
     
     //METHODS TO CREATE A PDF
     
     
     private void createPDFNote(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            document.open();
            addMetaData(document);
            
            Paragraph preface = new Paragraph(infoTextArea.getText(),bfBold);
            // We add 2 empty line
            addEmptyLine(preface,1 );
            document.add(preface);
            addEmptyLine(preface,2 );
            
            
            document.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }     
     
     }
     
     
    private  void addMetaData(Document document) {
        document.addTitle( "Lost book note" );
        document.addSubject( "Order for book replacement" );
        document.addKeywords( "School, Lost, Book" );
        document.addAuthor( "Daviscah Tech Ltd" );
        document.addCreator( "Daviscah Tech Ltd" );
    } 
    
    //Method to creat an empty line in the document
     private static void addEmptyLine(Paragraph paragraph, int number) {
        for ( int i = 0 ; i < number; i++) {
              paragraph.add( new Paragraph( " " ));
        }
    }
     
     //END METHODS TO CREATE A PDF
     
     @FXML
    private void printStudentNote(ActionEvent event) { 
        
         if (infoTextArea.getText().isEmpty()) {
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Printing Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You must retrieve student who lost the book and other details for printing to take place.\n");
                    errorIssue.showAndWait();             
         } else {
                   // generate pdf
                   fileChooserOpener();
                   fileChooser.setTitle("Save lost book note for printing");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);
                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        createPDFNote(filePath);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated lost book note and it's ready for printing.\n");
                    errorIssue.showAndWait();

         }             
    }
     
    @FXML
    private void lostbook_searchOperation(ActionEvent event) {
        
         if (lostbook_searchBookID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The lost book has failed Search operation.\n"
                        + "Ensure the Book ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM lostbooks WHERE lostBookID = ? ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, lostbook_searchBookID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("lostBookID");
                    lostbook_bookID.setText(add1); 
                    String add2 = rs.getString("lostBookTitle");
                    lostbook_bookTitle.setText(add2);
                    String add4 = rs.getString("lostBookClass");
                    lostbook_bookClass.setText(add4);                    
                    String add6 = rs.getString("studentID");
                    lostbook_studentID.setText(add6);
                    String add5 = rs.getString("studentName");
                    lostbook_studentName.setText(add5);
                    String add7 = rs.getString("dateLost");
                    lostbook_regDateText.setVisible(true);
                    lostbook_regDate.setVisible(true);
                    lostbook_regDate.setText(add7);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid lost book");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or lost book does not Existing!");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }
    
     @FXML
    private void lostbook_searchStudentOperation(ActionEvent event) {
        
          if (lostbook_searchStudentID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student who lost book has failed Search operation.\n"
                        + "Ensure the Student ID has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM lostbooks WHERE studentID = ? ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, lostbook_searchStudentID.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("lostBookID");
                    lostbook_bookID.setText(add1); 
                    String add2 = rs.getString("lostBookTitle");
                    lostbook_bookTitle.setText(add2);
                    String add4 = rs.getString("lostBookClass");
                    lostbook_bookClass.setText(add4);                    
                    String add6 = rs.getString("studentID");
                    lostbook_studentID.setText(add6);
                    String add5 = rs.getString("studentName");
                    lostbook_studentName.setText(add5);
                    String add7 = rs.getString("dateLost");
                    lostbook_regDateText.setVisible(true);
                    lostbook_regDate.setVisible(true);
                    lostbook_regDate.setText(add7);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid lost book");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or Student never lost a book");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }  
        }
    }

    @FXML
    private void lostbook_Register(ActionEvent event) {
        
        if (lostbook_bookID.getText().isEmpty() || lostbook_bookClass.getText().isEmpty()||lostbook_bookTitle.getText().isEmpty() || lostbook_studentID.getText().isEmpty()
                || lostbook_studentName.getText().isEmpty()|| lostbook_comboBookSubject.getSelectionModel().isEmpty() || lostBook_combonoClassList.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (lostbook_bookID.getText().isEmpty()) {
                     msg = "Lost Book ID is missing, lost book ID can not be empty.";                 
                } else if (lostbook_bookClass.getText().isEmpty() ) {
                   msg = "Lost Book class is missing, lost book class can not be empty.";
                } else if (lostbook_bookTitle.getText().isEmpty() ) {
                   msg = "Lost Book title is missing, lost book title can not be empty.";
                } else if (lostbook_studentID.getText().isEmpty() ) {
                   msg = "Student ID is missing, lost book student ID can not be empty.";
                } else if (lostbook_studentName.getText().isEmpty() ) {
                   msg = "Student name is missing, student name can not be empty.";
                } else if (lostbook_comboBookSubject.getSelectionModel().isEmpty() ) {
                   msg = "Lost Book subject is missing, lost book subject can not be empty.";
                }  else if (lostBook_combonoClassList.getSelectionModel().isEmpty() ) {
                   msg = "Student class is missing, student class can not be empty.";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Lost book issued has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to register lost book ID, "+lostbook_bookID.getText()+","+lostbook_bookTitle.getText() +" by "+ lostbook_studentName.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm lost book registration");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "INSERT INTO lostbooks (lostBookID,lostBookTitle,lostBookClass,lostbookSubject,studentID,studentName,studentClass) VALUES(?,?,?,?,?,?,?)";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, lostbook_bookID.getText());
                    pstmt.setString(2, lostbook_bookTitle.getText());                    
                    pstmt.setString(3, lostbook_bookClass.getText());
                    pstmt.setString(4, lostbook_comboBookSubject.getSelectionModel().getSelectedItem().toString());
                    pstmt.setString(5, lostbook_studentID.getText());
                    pstmt.setString(6, lostbook_studentName.getText()); 
                    pstmt.setString(7, lostBook_combonoClassList.getSelectionModel().getSelectedItem().toString());

                    int i = pstmt.executeUpdate(); // load data into the database 
                    
                    if (i>0) {
                        
                    String query = "DELETE FROM book WHERE book_id = ? ";
                    pstmt = con.prepareStatement(query);
                    pstmt.setString(1, lostbook_bookID.getText());
                    pstmt.executeUpdate();
                    
                    String query2 = "DELETE FROM issuedbook WHERE bookID = ? ";
                    pstmt = con.prepareStatement(query2);
                    pstmt.setString(1, lostbook_bookID.getText());
                    pstmt.executeUpdate();
                        
                        //show alert before registering data of employee
                    String msg = " Book ID  "+lostbook_bookID.getText()+", "+lostbook_bookTitle.getText()+ ", has been registered as a lost book.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Lost Book Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearLostBookFields();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not register lost book due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in registration of lost book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not register lost book due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }                
        }  
        refresh_lostBookTable();
        refreash_bookTable();
        refresh_issueBookTable();
    }

    @FXML
    private void lostbook_Update(ActionEvent event) {
        
        if (lostbook_bookID.getText().isEmpty() || lostbook_searchBookID.getText().isEmpty() ||lostbook_bookClass.getText().isEmpty()||lostbook_bookTitle.getText().isEmpty() || lostbook_studentID.getText().isEmpty()
                || lostbook_studentName.getText().isEmpty()|| lostbook_comboBookSubject.getSelectionModel().isEmpty()|| lostBook_combonoClassList.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (lostbook_bookID.getText().isEmpty()) {
                     msg = "Lost Book ID is missing, lost book ID can not be empty.";                 
                } else if (lostbook_bookClass.getText().isEmpty() ) {
                   msg = "Lost Book class is missing, lost book class can not be empty.";
                } else if (lostbook_bookTitle.getText().isEmpty() ) {
                   msg = "Lost Book title is missing, lost book title can not be empty.";
                } else if (lostbook_studentID.getText().isEmpty() ) {
                   msg = "Student ID is missing, lost book student ID can not be empty.";
                } else if (lostbook_studentName.getText().isEmpty() ) {
                   msg = "Student name is missing, student name can not be empty.";
                } else if (lostbook_comboBookSubject.getSelectionModel().isEmpty() ) {
                   msg = "Lost Book subject is missing, lost book subject can not be empty.";
                } else if (lostbook_searchBookID.getText().isEmpty()) {
                    msg = "Lost book ID in search field is missing, enter it to retrieve detais os a particular lost book";
                } else if (lostBook_combonoClassList.getSelectionModel().isEmpty() ) {
                   msg = "Student class is missing, student class can not be empty.";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Updating Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Lost book issued has failed update.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to update lost book ID, "+lostbook_bookID.getText()+","+lostbook_bookTitle.getText() +" by "+ lostbook_studentName.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm lost book update");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE lostbooks SET lostBookID=?,lostBookTitle=?,lostBookClass=?,lostbookSubject=?,studentID=?,studentName=?,studentClass=? WHERE lostBookID = '"+lostbook_searchBookID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, lostbook_bookID.getText());
                    pstmt.setString(2, lostbook_bookTitle.getText());                    
                    pstmt.setString(3, lostbook_bookClass.getText());
                    pstmt.setString(4, lostbook_comboBookSubject.getSelectionModel().getSelectedItem().toString());
                    pstmt.setString(5, lostbook_studentID.getText());
                    pstmt.setString(6, lostbook_studentName.getText());
                    pstmt.setString(7, lostBook_combonoClassList.getSelectionModel().getSelectedItem().toString());

                    int i = pstmt.executeUpdate(); // load data into the database 
                    
                    if (i>0) {
                    String msg = " Book ID  "+lostbook_bookID.getText()+", "+lostbook_bookTitle.getText()+ ", has been updated.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Lost Book Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearLostBookFields();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not update lost book due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in updating of lost book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update lost book due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }                
        }  
       refresh_lostBookTable();        
    }

    @FXML
    private void lostbook_Delete(ActionEvent event) {    
        
        if (lostbook_bookID.getText().isEmpty()) {
            
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Delete Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The lost book has failed DELETE operation.\n"
                                + "Ensure the Book ID has been inserted after search the book.");
                    errorIssue.showAndWait(); 

                }else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete lost Book");
                    alert.setGraphic(bookImageView);
                    alert.setHeaderText(null);
                    alert.setContentText("CAUTION\n"
                            + "If you delete this book, all responsible students SHARING the book will also be deleted."
                            + " Ensure that that this book has been returned before deleting\n"
                            + "To delete a SPECIFIC student Sharing the book, use the button at the top right corner\n\n"
                            + "Are you sure to delete this lost book?");         
                    Optional <ButtonType> obt = alert.showAndWait();

                    if (obt.get()== ButtonType.OK) {
                        try {
                       String query = "DELETE FROM lostbooks WHERE lostBookID = ? ";
                       pstmt = con.prepareStatement(query);
                       pstmt.setString(1, lostbook_bookID.getText());
                       pstmt.executeUpdate();            
                       pstmt.close(); 

                       clearLostBookFields();

                       } catch (SQLException ex) {
                           Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
                       }finally {
                            try {
                                rs.close();
                                pstmt.close();
                            } catch (Exception e) {
                            }        
                        }
                   }
                   refresh_lostBookTable();
                }              
    }
    
    @FXML
    private void sendEmailToParentOperation(ActionEvent event) {
        
        if (parentEmailDisplay.getText().isEmpty()) {
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Messaging Error");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("Email sending experienced an error, no receipient email address.\n"
                    + "Choose a student to enable sending email to parent.");
            errorIssue.showAndWait();
        } else {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(emailImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to send email to the above address, "+parentEmailDisplay.getText()+"\n"
                    + "This may take a while, please wait....");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {

                  SendEmailService task = new SendEmailService();
                  //run the task on a background thread
                  Thread backgroundThread = new Thread(task);
                  backgroundThread.setDaemon(true);
                  backgroundThread.start();
            }
        }
    }
    
     @FXML
    private void deleteSpecificStudentWithBook(ActionEvent event) {
        
         if (lostbook_searchStudentID.getText().isEmpty() || lostbook_bookID.getText().isEmpty()) {
            
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Delete Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The lost book has failed DELETE operation.\n"
                                + "Ensure the student ID has been inserted and retrieved.");
                    errorIssue.showAndWait(); 

                }else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Student and a specific lost Book");
                    alert.setGraphic(studentImageView);
                    alert.setHeaderText(null);
                    alert.setContentText("If the book was shared among other students,"
                            + " only this student will be deleted and others will still be responsible for the lost book.\n\n"
                            + "Are you sure to remove this student from being responsible?");         
                    Optional <ButtonType> obt = alert.showAndWait();

                    if (obt.get()== ButtonType.OK) {
                        try {
                       String query = "DELETE FROM lostbooks WHERE studentID = ? AND lostBookID = ?";
                       pstmt = con.prepareStatement(query);
                       pstmt.setString(1, lostbook_searchStudentID.getText());
                       pstmt.setString(2, lostbook_bookID.getText());
                       pstmt.executeUpdate();            
                       pstmt.close(); 

                       clearLostBookFields();

                       } catch (SQLException ex) {
                           Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
                       }finally {
                            try {
                                rs.close();
                                pstmt.close();
                            } catch (Exception e) {
                            }        
                        }
                   }
                   refresh_lostBookTable();
                }
    }

    @FXML
    private void lostbook_clearLostBookFields(ActionEvent event) {
        clearLostBookFields();
    }
    
    @FXML
    private void table_lostBookMouseClicked(MouseEvent event) {
        
        try {
                    LostBooks bkd = table_lostBook.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM lostbooks WHERE lostBookID = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, bkd.getBookID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String add1 = rs.getString("lostBookID");
                        lostbook_bookID.setText(add1); 
                        lostbook_searchBookID.setText(add1); 
                        String add2 = rs.getString("lostBookTitle");
                        lostbook_bookTitle.setText(add2);
                        String add4 = rs.getString("lostBookClass");
                        lostbook_bookClass.setText(add4);                    
                        String add6 = rs.getString("studentID");
                        lostbook_studentID.setText(add6);
                        String add5 = rs.getString("studentName");
                        lostbook_studentName.setText(add5);
                        String add7 = rs.getString("dateLost");
                        lostbook_regDateText.setVisible(true);
                        lostbook_regDate.setVisible(true);
                        lostbook_regDate.setText(add7);
                        
                        pstmt.close();
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid book");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or book does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception ex) {
                    }        
                }
        
    }

    @FXML
    private void table_lostBookKeyReleased(KeyEvent event) {
        
         table_lostBook.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
                
                try {
                    LostBooks bkd = table_lostBook.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM lostbooks WHERE lostBookID = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, bkd.getBookID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String add1 = rs.getString("lostBookID");
                        lostbook_bookID.setText(add1); 
                        lostbook_searchBookID.setText(add1); 
                        String add2 = rs.getString("lostBookTitle");
                        lostbook_bookTitle.setText(add2);
                        String add4 = rs.getString("lostBookClass");
                        lostbook_bookClass.setText(add4);                    
                        String add6 = rs.getString("studentID");
                        lostbook_studentID.setText(add6);
                        String add5 = rs.getString("studentName");
                        lostbook_studentName.setText(add5);
                        String add7 = rs.getString("dateLost");
                        lostbook_regDateText.setVisible(true);
                        lostbook_regDate.setVisible(true);
                        lostbook_regDate.setText(add7);
                        
                        pstmt.close();
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid book");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or book does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                }finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception ex) {
                    }        
                }
            }
        });
        
    }

    private void clearLostBookFields(){
    
        lostbook_regDateText.setVisible(false);
        lostbook_regDate.setVisible(false);
        lostbook_bookClass.clear();
        lostbook_bookID.clear();
        lostbook_bookTitle.clear();
        lostbook_searchBookID.clear();
        lostbook_studentID.clear();
        lostbook_studentName.clear();
    }
    
     // method to load Content to the book table
     public void update_lostbookDetailTable(){ 
        lostbook_columnBookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        lostbook_columnBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        lostbook_columnBookClass.setCellValueFactory(new PropertyValueFactory<>("bookClass"));        
        lostbook_columnBookSubject.setCellValueFactory(new PropertyValueFactory<>("bookSubject"));        
        lostbook_columnStudentID.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        lostbook_columnStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        lostbook_columnDateLost.setCellValueFactory(new PropertyValueFactory<>("dateLost"));
        try { 
            String sql = "SELECT lostBookID,lostBookTitle,lostBookClass,lostbookSubject,studentID,studentName,dateLost FROM lostbooks";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
               lostbooksData.add( new LostBooks(
                        rs.getString("lostBookID"),
                       rs.getString("lostBookClass"),
                        rs.getString("lostBookTitle"),                                                
                        rs.getString("lostbookSubject"),                        
                        rs.getString("studentID"),
                        rs.getString("studentName"),
                        rs.getString("dateLost")                       
                ));         
            }
            //load items to the table
            table_lostBook.setItems(lostbooksData);
            table_lostBook.setPlaceholder(new Label("No lost book record matches the details provided", errorImage));
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    } 
     
     private void  refresh_lostBookTable(){     
         lostbooksData.clear();
         update_lostbookDetailTable();     
     }
     
     //method to handle lost book combobox
       private void updateLostBookSubjects(){  
        try { 
            String sql = "SELECT bookSubject FROM bookcategory";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
                subjectData.add( rs.getString("bookSubject")); 
            }
            
            // populate class combo box 
            lostbook_comboBookSubject.setItems(subjectData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
     
     public void refreshComboLostBook(){
         subjectData.clear();
         updateLostBookSubjects();
     
     }

       @FXML
    private void refresh_lostBookCombo(ActionEvent event) {
        refreshComboLostBook();
    }
    
         //method to handle lost book class lists combobox
    private void updateLostBookClassList(){  
         try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                lostBookClassListData.add( rs.getString("className"));         
            }
            // populate class combo box 
            lostBook_combonoClassList.setItems(lostBookClassListData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
     
     private void refreshComboLostBookClassList(){
         lostBookClassListData.clear();
         updateLostBookClassList();
     
     }
    
     @FXML
    private void refresh_lostBookClassList(ActionEvent event) {
        refreshComboLostBookClassList();
    }
    
    //########################## END METHOD TO HANDLE LOST BOOK DETAILS #####
    
    //##################### START METHODS TO HANDLE BOOK MANAGEMENT #########
    
       @FXML
    private void loadBookManagementOperation(ActionEvent event) {
        
        try {
                String sql = "SELECT * FROM bookpreference ";
                pstmt = con.prepareStatement(sql);

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    
                    String add = rs.getString("id");
                    displayTermID.setText(add);
                    String add1 = rs.getString("dailyFine");
                    dailyFine.setText(add1); 
                    String add2 = rs.getString("terms");
                    replacementTerms.setText(add2);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Data Could not be loaded due to internal error.");
                    alert.showAndWait(); 
                }
            } catch (SQLException e) {
            } finally {
                    try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {
                    }        
               }   
           
    }

    @FXML
    private void updateBookManagementDetails(ActionEvent event) {
 
      if (dailyFine.getText().isEmpty() || replacementTerms.getText().isEmpty()) {
             
                 String msg = null;
            
                if (dailyFine.getText().isEmpty()) {
                     msg = "Daily fine for lost books is empty, you must fill it if necessary.";                 
                } else if (replacementTerms.getText().isEmpty()) {
                    msg = "Replacement term is empty, you must enter it";
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("Replacement condition update failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to update book replacement condition? ";           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm book update");
            alert.setGraphic(partnerImageView);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE bookpreference SET dailyFine=?,terms=? WHERE id = '"+displayTermID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, dailyFine.getText());
                    pstmt.setString(2, replacementTerms.getText());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Replacement terms have been update.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Confirmation");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearBookMngFields();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setHeaderText(null);
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not update book replacement terms due  to internal error.\n");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in updating of book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update book replacement terms due  to internal error. " +e);
                    at.showAndWait();                         
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
            }
                
        }  
        
    }
    
    private void clearBookMngFields(){
        
        dailyFine.clear();
        replacementTerms.clear();
        displayTermID.setText(null);
    }

    @FXML
    private void clearBookManagementFields(ActionEvent event) {
        clearBookMngFields();
    }
    
     @FXML
    private void bookM_loadStudentAndBookInfo(ActionEvent event) {
        
        //clear textarea first
        infoTextArea.clear();
        
        try {                        
            //loading data to listview
            String sqlIssuedBook = "SELECT * FROM lostbooks WHERE studentID = ?  ";
            pstmt = con.prepareStatement(sqlIssuedBook);
            pstmt.setString(1, bookM_responsibleStudentID.getText());
            
            rs=pstmt.executeQuery();
            if (rs.next()) {
                //variables to hold data
                float fineImposed = 0;
                String terms = null;
                String schoolName = null;
                String schoolContact = null;
                String schoolAddress = null;
                String schoolRegion = null;
                String schoolEmail = null;
                String schoolWebsite = null;
                    
                    // Get fine ammount to be incurred by retrieving data from the database.                               
                String sql3 = "SELECT * FROM schoolInfo ";
                pstmt = con.prepareStatement(sql3);

                ResultSet rs3=pstmt.executeQuery();
                if (rs3.next()) {
                     schoolName = rs3.getString("name");
                     schoolContact = rs3.getString("contact");
                     schoolAddress = rs3.getString("address");
                     schoolRegion = rs3.getString("region");
                     schoolEmail = rs3.getString("email");
                     schoolWebsite = rs3.getString("website");
                }  
                ////////////////////////////////////
                    
                //calculate the days the book has been kept by student and the fine also
                    //first, get the date from the database and onvert it to localdate
                Date retrievedDate = rs.getDate("dateLost");
                Instant instant = Instant.ofEpochMilli(retrievedDate.getTime());
                LocalDate startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
                                
                    // second, find the difference days between current and lost date
                LocalDate currentDate = LocalDate.now();    
                long p2 = ChronoUnit.DAYS.between(startDate, currentDate);
                    // Get fine ammount to be incurred by retrieving data from the database.                               
                String sql = "SELECT * FROM bookpreference ";
                pstmt = con.prepareStatement(sql);

                ResultSet rs2=pstmt.executeQuery();
                if (rs2.next()) {
                     fineImposed = (float) rs2.getDouble("dailyFine");
                     terms = rs2.getString("terms");
                }  
                ////////////////////////////////////
                              
                // determine if the student has stayed longer with the book and apply a fine
                String fineStatement = null;
                    if (fineImposed>0) {
                        //calculate the fine
                        float totalFine = fineImposed * p2;
                        fineStatement = "Amount of fine imposed : " + totalFine + " KSH";

                    } else {
                        fineStatement = "Amount of fine imposed : " + 0.00 + " KSH" ;
                    }
                    
                // append details to the textarea
                infoTextArea.appendText(
                "\t\t"+schoolName +":\n" 
                + "\t\t"+ schoolAddress +" "+ schoolRegion+ "\n"
                + "\t\tContact: "+ schoolContact+ "\n"
                + "\t\tEmail: "+ schoolEmail+ "\n"                
                + "\t\tWebsite: "+ schoolWebsite+"\n\n"
                + "LOST BOOK INFORMATION.\n"
                + "Book ID : "+ rs.getString("lostBookID") + "\n"
                + "Book Class/Level : "+ rs.getString("lostBookClass") + "\n"
                + "Book Subject : "+ rs.getString("lostBookSubject")+ "\n"
                + "Book title : "+ rs.getString("lostBookTitle")+ "\n\n"
                + "STUDENT RESPONSIBLE INFORMATION. \n"
                + "Student ID : "+ rs.getString("studentID")+ "\n"
                + "Student name : "+ rs.getString("studentName") + "\n"
                + "Recorded lost date : "+ rs.getDate("dateLost") + "\n"
                + "Number of days since recorded lost book : " + p2 + " days.\n"
                + ""+ fineStatement + "\n\n"
                + "BOOK REPLACEMENT CONDITION:\n"        
                + ""+terms+"\n"
                + "Note issued to student on: " + currentDate+ "\n\n"
                + "Teacher sign & school stamp: ______________________________________"
                           
            );
                pstmt.close();                
                
            }else {
                Alert issuedbk = new Alert(Alert.AlertType.ERROR);
                issuedbk.setTitle("Failed");
                issuedbk.setGraphic(errorImage);
                issuedbk.setHeaderText(null);
                issuedbk.setContentText("Record of the student not found.\n"
                        + "The student has never been issued a book,\n"
                        + "or the student submitted  the book.");
                issuedbk.showAndWait(); 
            }
            
            //retrieve student's parent's email
            String pEmail = "SELECT * FROM students WHERE student_id = ?  ";
            pstmt = con.prepareStatement(pEmail);
            pstmt.setString(1, bookM_responsibleStudentID.getText());
            
            rs=pstmt.executeQuery(); 
            if (rs.next()) {
            
                String add12 = rs.getString("parentEmail");
                parentEmailDisplay.setText(add12);
                
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
                
    }

    
    //##################### END METHODS TO HANDLE BOOK MANAGEMENT ###########
    
     //####################### METHODS TO HANDLE single STUDENT promotion DETAILS ##########
    private void updateSingleStudentCombo(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                singleStudentClassListData.add( rs.getString("className"));         
            }
            // populate class combo box 
            promoteSingleStudentCombobox.setItems(singleStudentClassListData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
    // call this from anatha class
    public void refreshSingleStudentComboClass(){    
        singleStudentClassListData.clear();
        updateSingleStudentCombo();
    }
    
    @FXML
    private void refresh_promoteSingleStudentCombobox(ActionEvent event) {
        refreshSingleStudentComboClass();
    }

    @FXML
    private void promoteSingleStudentOperation(ActionEvent event) {
        
        if (promoteSingleStudentCombobox.getSelectionModel().isEmpty() || studentID.getText().isEmpty() || promote_studentClass.getText().isEmpty()) { 
                 
                 String msg = null;
            
                if (promoteSingleStudentCombobox.getSelectionModel().isEmpty()) {
                     msg = "Target class is not choosen, you must choose it";                 
                } else if(studentID.getText().isEmpty()) {
                   msg = "Student ID not found, retrieve the student first.";
                } else if(promote_studentClass.getText().isEmpty()) {
                   msg = "Student class not found, retrieve the student first.";
                }
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Promotion Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student could not be promoted.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else { 
            
            String destClass =  promoteSingleStudentCombobox.getSelectionModel().getSelectedItem().toString();
            
            try {
                    String sql = "UPDATE students SET student_class = ? WHERE"
                            + " student_class = '"+promote_studentClass.getText()+"' AND "
                            + "student_id = '"+studentID.getText()+"'";
                    
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, destClass);

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before promoting
                    String msg = ""+studentName.getText()+""
                            + " has successfully been promoted to "+promoteSingleStudentCombobox.getSelectionModel().getSelectedItem().toString()+ ".\n\n";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Promotion status");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not promote Student due  to internal error.");
                        error.showAndWait();
                    } 
                    
                    pstmt.close();
                    refresh_studentDeatailTable();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in promotion");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not promote Student due  to internal error. ");
                    at.showAndWait();  
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
        }
    }
    
    //####################### end methods to promote students #################
    
     //####################### METHODS TO HANDLE single STUDENT de-promotion DETAILS ##########
    private void updateSingleStudentDepromote(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                depromotionClassListData.add( rs.getString("className"));         
            }
            // populate class combo box 
            depromoteStudentCombobox.setItems(depromotionClassListData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
    // call this from anatha class
    public void refreshSingleStudentdepromoteClass(){    
        depromotionClassListData.clear();
        updateSingleStudentDepromote();
    }
    
    @FXML
    private void refresh_depromoteStudentCombobox(ActionEvent event) {
        refreshSingleStudentdepromoteClass();
    }

    @FXML
    private void depromoteSingleStudentOperation(ActionEvent event) {
        
         if (depromoteStudentCombobox.getSelectionModel().isEmpty() || studentID.getText().isEmpty() || promote_studentClass.getText().isEmpty()) { 
                 
                 String msg = null;
            
                if (depromoteStudentCombobox.getSelectionModel().isEmpty()) {
                     msg = "Target class is not choosen, you must choose it";                 
                } else if(studentID.getText().isEmpty()) {
                   msg = "Student ID not found, retrieve the student first.";
                } else if(promote_studentClass.getText().isEmpty()) {
                   msg = "Student class not found, retrieve the student first.";
                }
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Promotion Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The student could not be de-promoted.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else { 
            
            String destClass =  depromoteStudentCombobox.getSelectionModel().getSelectedItem().toString();
            
            try {
                    String sql = "UPDATE students SET student_class = ? WHERE"
                            + " student_class = '"+promote_studentClass.getText()+"' AND "
                            + "student_id = '"+studentID.getText()+"'";
                    
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, destClass);

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before promoting
                    String msg = ""+studentName.getText()+""
                            + " has successfully been de-promoted to "+depromoteStudentCombobox.getSelectionModel().getSelectedItem().toString()+ ".\n\n";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Promotion status");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not de-promote Student due  to internal error.");
                        error.showAndWait();
                    } 
                    
                    pstmt.close();
                    refresh_studentDeatailTable();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in promotion");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not de-promote Student due  to internal error. ");
                    at.showAndWait();  
                }finally {
                   try {
                        rs.close();
                        pstmt.close();
                    } catch (Exception e) {}               
               }
        }
    }
    
    //####################### METHODS TO HANDLE single STUDENT de-promotion DETAILS ##########
    
    //####################### methods that handle deleting final students##########
    private void deleteClassStudents(){  
        try { 
            String sql = "SELECT className FROM class WHERE isFinal = 1";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                finalDeleteClassListData.add( rs.getString("className"));         
            }
            // populate class combo box 
            finalStudentComboClass.setItems(finalDeleteClassListData);
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (Exception e) {
            }        
        }
    }
    
    // call this from anatha class
    public void refreshFinalDeleteClass(){    
        finalDeleteClassListData.clear();
        deleteClassStudents();
    }
    
     @FXML
    private void refresh_finalStudentComboClass(ActionEvent event) {
        refreshFinalDeleteClass();
    }

    @FXML
    private void deleteSpecificStudentInClass(ActionEvent event) {
        
        if (finalStudentComboClass.getSelectionModel().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The students has failed DELETE operation.\n"
                        + "Ensure the class to be removed students is selected.");
            errorIssue.showAndWait(); 
            
        }else {                        
            String thisClass = finalStudentComboClass.getSelectionModel().getSelectedItem().toString();
            
            try {
                String sql = "SELECT * FROM students WHERE student_class = ?";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, thisClass);
                
                rs = pstmt.executeQuery();                
                if (rs.next()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete Students");
                        alert.setGraphic(studentImageView);
                        alert.setHeaderText(null);
                        alert.setContentText("Are you sure to delete student form "+thisClass+" ?");         
                        Optional <ButtonType> obt = alert.showAndWait();

                        if (obt.get()== ButtonType.OK) {
                            try {
                           String query = "DELETE FROM students WHERE student_class = ? ";
                           pstmt = con.prepareStatement(query);
                           pstmt.setString(1, thisClass);
                           pstmt.executeUpdate();            
                           pstmt.close();            
                           } catch (SQLException ex) {
                               Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
                           }finally {
                                try {
                                    rs.close();
                                    pstmt.close();
                                } catch (Exception e) {
                                }        
                            }   
                       }
                        refresh_studentDeatailTable();
                        clearStudentField();
                } else {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("OOPS!");
                        alert.setGraphic(studentImageView);
                        alert.setHeaderText(null);
                        alert.setContentText("There are no students in "+thisClass+"\n"
                                + "The class is empty. You can promote students into this class."); 
                        alert.showAndWait();
                }

            } catch (Exception e) {
            }
        }
    }

    //############# end methods that handle deleting final students #######
    
    //############################# Other methods ##############################
    
    //method to write any content to a particular file
    private void saveFile(String content, File file){        
        try {
            FileWriter fileWriter;            
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();            
        } catch (IOException e) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE,null,e);
        }  
    }
    
        //Method to open a fileChooser dialog box
    public void fileChooserOpener(){
                fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files","*.*"),
                new FileChooser.ExtensionFilter("PDF Files","*.pdf"),
                new FileChooser.ExtensionFilter("Excel Files","*.xslx")             

                
//                new FileChooser.ExtensionFilter("Text Files","*.txt"),
//                new FileChooser.ExtensionFilter("Word Files","*.docx"),
//                new FileChooser.ExtensionFilter("Image Files","*.png","*.jpg","*.gif"),
//                new FileChooser.ExtensionFilter("Audio Files","*.wav","*.mp3","*.mp4","*.acc") 
                
        );   
    }
    
    
    //method to validate if input is an int
    private boolean isInt(TextField input){
        input.setStyle("-fx-border-color: green; -fx-border-radius: 15;-fx-background-radius: 20;");
        try {
            int number = Integer.parseInt(input.getText());
            System.out.println("Input is" + number );
            return true;
        } catch (Exception e) {
            input.setStyle("-fx-border-color: red;");
            input.setText(null);
            input.setPromptText("Only digits/numbers must be filled");
            return false;
        }
    }
    
     //method to validate if input is an EMPTY for blue fields
    private boolean isBlueFieldsEmpty(TextField input){
        input.setStyle("-fx-border-color: blue; -fx-border-radius: 15;-fx-background-radius: 20;");
        
        if (input.getText().isEmpty()) {
            input.setStyle("-fx-border-color: red;");
            input.setText(null);
            input.setPromptText("Field is Empty, must be filled");
            return true;
        } else {            
            return false;
        }
        
    }
    
    // method to validate email addresss
    private boolean validateEmail(TextField input){
    
        Pattern p = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z])+");
        Matcher m = p.matcher(input.getText());
        if (m.find() && m.group().equals(input.getText())) {
            return  true;
        } else {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Email Validation Error");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The format of parent's email is incorrect. Enter the correct email format address\n");
            errorIssue.showAndWait(); 
            
            return  false;
        }
    }
    
     //Method to take and open any window
     private void loadWindow(String loc, String title) throws IOException{
        //cretate stage with specified owner and modality                
        Parent root = FXMLLoader.load(getClass().getResource(loc));
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.getIcons().add(new Image(MainPageController.class.getResourceAsStream("/daviscahtech_bookmanagement/Resources/daviscahtechLogo.png")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();   
    }
     
     /////////////// end other methods ######################################

     @FXML
    private void closeProgrammeOperation(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void openClassSettingOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/schoolClass.fxml", "School classes/Level setting");
    }

    @FXML
    private void openSubjectSettingOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/schoolSubject.fxml", "School subject setting");
    }

    @FXML
    private void openSchoolInfoOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/schoolInfo.fxml", "School Information setting");
    }

    @FXML
    private void openPromoteStudentOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/promoteStudents.fxml", "Student promotion setting");
    }

    @FXML
    private void openLogginSettingOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/loggingMasterWindow.fxml", "Master Adminstrator");
    }

    @FXML
    private void openReportPrntingOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/reportPrinting.fxml", "Report printing");
    }

    @FXML
    private void adoutUsOperation(ActionEvent event) throws IOException {
         loadWindow("/daviscahtech_bookmanagement/Fxmls/daviscahtech.fxml", "Book Watch Developers");
    }

    @FXML
    private void openLocalServerSettingOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/forgottenPassWord.fxml", "Change Server Authenthication");
    }

    @FXML
    private void openTeachingStaffWindow(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/teachers.fxml", "Manage Book issue to teachers");
    }

    @FXML
    private void openBackUpWindow(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/backup.fxml", "Back up data");
    }

    @FXML
    private void openPersonalLoginDetailOperation(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/personalAccount.fxml", "Change personal logging details");
    }

    @FXML
    private void openQuickReportWindow(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/quickReport.fxml", "Quick summary report");
    }

    @FXML
    private void rebootStudentTable(ActionEvent event) {
        refresh_studentDeatailTable();
    }

    @FXML
    private void rebootBookTable(ActionEvent event) {
        refreash_bookTable();
    }


   
    
    // #########  Subclass to send email to parents##################
    private class SendEmailService extends Task<Void> {

         @Override
        protected Void call() throws Exception {
            sendEmail();
            return null;
        }
       
        
        @Override
        protected void succeeded(){
            super.succeeded();
            
            Alert bookError = new Alert(Alert.AlertType.ERROR);  
            bookError.setTitle("Success");
            bookError.setHeaderText(null);
            bookError.setGraphic(emailImageView);
            bookError.setContentText("Successfuly sent email to the parent.\n");
            bookError.showAndWait();
            
            emaillingStatus.setVisible(false); 
        }
        
        
        @Override
        protected void failed(){
            super.failed(); 
            Alert bookError = new Alert(Alert.AlertType.ERROR);  
            bookError.setTitle("Error occured");
            bookError.setHeaderText(null);
            bookError.setGraphic(errorImage);
            bookError.setContentText("An error occurred sending email.\n");
            bookError.showAndWait();
            
        }        
        
         @Override
        protected void running(){
            super.running();
            emaillingStatus.setVisible(true); 
        }
        
        // Method to send all email for back up
        private void sendEmail(){
        
            final String username = "dnyandiri@gmail.com";
            final String password = "daviscahtech2018";
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            
            Session session = Session.getInstance(props, new javax.mail.Authenticator(){
                @Override
                protected  PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(username, password);
                }
            } );
            
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("dnyandiri@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(parentEmailDisplay.getText()));
                message.setSubject("Notification of Lost Book");
                message.setSentDate(new java.util.Date());
                message.setText(infoTextArea.getText());
                                
                //send full message
                System.out.println("Your email is being sent...");
                Transport.send(message);
                System.out.println("Your email was successful sent");
                
                                
            } catch (Exception e) {
            }
        }

       

        
    }// end of emailling class


    
    // #########  Subclass for all issued books  to run on background ##################

    

    

 
    
   
}// End of class 
