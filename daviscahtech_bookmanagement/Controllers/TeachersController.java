/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import daviscahtech_bookmanagement.Pojo.HeaderFooterPageEvent;
import daviscahtech_bookmanagement.Pojo.TeacherBookIssue;
import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class TeachersController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image studentImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/student.png").toExternalForm());
    ImageView studentImageView = new ImageView(studentImage);
    
    Image bookImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/book.png").toExternalForm());
    ImageView bookImageView = new ImageView(bookImage);
    
    Image excelImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/excel_1.png").toExternalForm());
    ImageView excelImageView = new ImageView(excelImage);
    
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
    
    //variables to hold school info
    String schoolName = null;
    String schoolContact = null;
    String schoolAddress = null;
    String schoolRegion = null;
    String schoolEmail = null;
    String schoolWebsite = null;     
    
    //variable to hold the unique ID FROM TABLE OF BOOKS ISSUED TO TEACHERS
    private int uniqueTeacherID;

    @FXML
    private TextField teacher_ID;
    @FXML
    private TextField teacher_name;
    @FXML
    private Text teacher_displayDepartment;
    @FXML
    private ComboBox teacher_departmentCombo;
     ObservableList<String> departmentList = FXCollections.<String>observableArrayList(
             "Science department","Languages department","Humanities department","Technical department",
             "Mathematics department", "Games department", "IT department", "Health & Nutrition", "Co-Curriculum" );
    @FXML
    private Text teacher_displayBookSubject;
    @FXML
    private ComboBox teacher_bookSubjectCombo;
    private final ObservableList bookSubjectData = FXCollections.observableArrayList();
    @FXML
    private TextField teacher_bookTitle;
    @FXML
    private TextField teacher_bookClass;
    @FXML
    private TextField teacher_bookPublisher;
    @FXML
    private TextField teacher_booksIssued;
    @FXML
    private Text teacherInfo_ID;
    @FXML
    private Text teacherInfo_name;
    @FXML
    private Text teacherInfo_department;
    @FXML
    private Text teacherInfo_bookSubject;
    @FXML
    private Text teacherInfo_bookTitle;
    @FXML
    private Text teacherInfo_bookClass;
    @FXML
    private Text teacherInfo_bookPublisher;
    @FXML
    private TextField teacher_booksReturned;
    @FXML
    private TextField teacher_searchTeacherID;
    @FXML
    private TableView<TeacherBookIssue> teachserIssuedTable;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnID;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnName;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnDepart;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnQuantity;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnBookTitle;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnBookClass;
    @FXML
    private TableColumn<TeacherBookIssue, String> teacher_columnDateIssued;
    private final ObservableList<TeacherBookIssue> teacherBooksData = FXCollections.observableArrayList();
    @FXML
    private Text teacherInfo_booksIssued;
    @FXML
    private TextField teacherCopy_ID;
    @FXML
    private ComboBox teacherCopy_bookSubjectCombo;
    private final ObservableList teacherCopySubjectData = FXCollections.observableArrayList();
    @FXML
    private TextField teacherCopy_title;
    @FXML
    private TextField teacherCopy_bookClass;
    @FXML
    private TextField teacherCopy_bookPublisher;
    @FXML
    private TextField teacherCopy_searchID;
    @FXML
    private TextField teacherCopyInfo_searchID;
    @FXML
    private Text teacherCopy_displayBookSubject;
    @FXML
    private Text teacherCopyInfo_ID;
    @FXML
    private Text teacherCopyInfo_bookSubject;
    @FXML
    private Text teacherCopyInfo_bookTitle;
    @FXML
    private Text teacherCopyInfo_bookClass;
    @FXML
    private Text teacherCopyInfo_bookPublisher;
    @FXML
    private Text teacherCopyInfo_bookStatus;
    @FXML
    private Text teacherCopyInfo_teacherIssued;
    @FXML
    private TextField teacherCopyInfo_issuedTeacherName;
    @FXML
    private ListView<String> teacherCopiesLIstView;
    ObservableList<String> teacherCopyData = FXCollections.observableArrayList();
    @FXML
    private HBox teacherCopiesPrintintingStatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
       
        //populate combobox
        updateBookSubjects();
        teacher_departmentCombo.setItems(departmentList);
        
        //populate table
        update_teacherIssuedDetailTable();
        updateTeacherCopySubject();
        
        //populate listview
        refreshTeacherCopies();
        
        //get school info
        loadSchoolInfo();
    } 
    
        //method to automatically set school information from the database
    private void loadSchoolInfo() {
    
        try {
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
                
                pstmt.close();
            
        } catch (SQLException e) {
        }
               
                //////////////////////////////////// 
    }

    
    @FXML
    private void teacher_RegisterIssuedBooksOperation(ActionEvent event) {
        
        if (teacher_ID.getText().isEmpty() || teacher_name.getText().isEmpty()||teacher_bookClass.getText().isEmpty() || teacher_bookPublisher.getText().isEmpty()
                || teacher_bookTitle.getText().isEmpty()|| teacher_booksIssued.getText().isEmpty()||teacher_departmentCombo.getSelectionModel().isEmpty()||teacher_bookSubjectCombo.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (teacher_ID.getText().isEmpty()) {
                     msg = "Teacher ID is empty, you must enter it";                 
                } else if (teacher_name.getText().isEmpty()) {
                    msg = "Teacher name is empty, you must enter it";
                } else if (teacher_bookClass.getText().isEmpty()) {
                    msg = "Book class/level is empty, you must enter it";
                } else if (teacher_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (teacher_bookTitle.getText().isEmpty()) {
                    msg = "Book Title is empty, you must enter it";
                }  else if (teacher_bookSubjectCombo.getSelectionModel().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                } else if (teacher_departmentCombo.getSelectionModel().isEmpty()) {
                    msg = "Teacher department is empty, you must choose from the drop-down menu";
                } else if (teacher_booksIssued.getText().isEmpty()) {
                    msg = "Number of books issued to the teacher is empty, you must enter it";
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The issue operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
            
            if (isInt(teacher_booksIssued)) {
                
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to register and issue books to, "+teacher_name.getText()+" ("+teacher_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book issue");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "INSERT INTO teacherissued (teacher_ID,teacher_name,teacher_depart,book_subject,book_title,book_class,book_publisher,"
                                        + "quantity_issued) VALUES(?,?,?,?,?,?,?,?)";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setString(1, teacher_ID.getText());
                                pstmt.setString(2, teacher_name.getText());
                                pstmt.setString(3, teacher_departmentCombo.getSelectionModel().getSelectedItem().toString());
                                pstmt.setString(4, teacher_bookSubjectCombo.getSelectionModel().getSelectedItem().toString());
                                pstmt.setString(5, teacher_bookTitle.getText());
                                pstmt.setString(6, teacher_bookClass.getText());
                                pstmt.setString(7, teacher_bookPublisher.getText());
                                pstmt.setString(8, teacher_booksIssued.getText());

                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " Details of book have been successfuly saved under "+teacher_name.getText()+ ", to be given to a students.";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Registration Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                refreshTeacherBookIssueTable();
                                clearTeacherFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not register book issue due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not register book isssue due  to internal error. " +e);
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
    private void teacher_updateOperation(ActionEvent event) {

        if (teacher_ID.getText().isEmpty() || teacher_name.getText().isEmpty()||teacher_bookClass.getText().isEmpty() || teacher_bookPublisher.getText().isEmpty()
                || teacher_bookTitle.getText().isEmpty()|| teacher_booksIssued.getText().isEmpty()|| teacher_displayBookSubject.getText().isEmpty() || teacher_displayDepartment.getText().isEmpty()  ) {
             
                 String msg = null;
            
                if (teacher_ID.getText().isEmpty()) {
                     msg = "Teacher ID is empty, you must enter it";                 
                } else if (teacher_name.getText().isEmpty()) {
                    msg = "Teacher name is empty, you must enter it";
                } else if (teacher_bookClass.getText().isEmpty()) {
                    msg = "Book class/level is empty, you must enter it";
                } else if (teacher_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (teacher_bookTitle.getText().isEmpty()) {
                    msg = "Book Title is empty, you must enter it";
                }  else if (teacher_displayBookSubject.getText().isEmpty()) {
                    msg = "Book Subject is empty, you must retrieve the record first";
                } else if (teacher_displayDepartment.getText().isEmpty()) {
                    msg = "Teacher department is empty, you must retrieve the record first";
                } else if (teacher_booksIssued.getText().isEmpty()) {
                    msg = "Number of books issued to the teacher is empty, you must enter it";
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The update operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
            
            if (isInt(teacher_booksIssued)) {
                
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to update issued books to, "+teacher_name.getText()+" ("+teacher_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book issue");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "UPDATE teacherissued SET teacher_ID=?,teacher_name=?,teacher_depart=?,book_subject=?,book_title=?,book_class=?,book_publisher=?,"
                                        + "quantity_issued=? WHERE id = '"+uniqueTeacherID+"' ";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setString(1, teacher_ID.getText());
                                pstmt.setString(2, teacher_name.getText());
                                pstmt.setString(3, teacher_displayDepartment.getText());
                                pstmt.setString(4, teacher_displayBookSubject.getText());
                                pstmt.setString(5, teacher_bookTitle.getText());
                                pstmt.setString(6, teacher_bookClass.getText());
                                pstmt.setString(7, teacher_bookPublisher.getText());
                                pstmt.setString(8, teacher_booksIssued.getText());

                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " Details of book have been successfuly updated under "+teacher_name.getText()+ ", to be given to a students.";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Update Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                refreshTeacherBookIssueTable();
                                clearTeacherFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not update book issue due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not update book isssue due  to internal error. " +e);
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
    private void teacher_searchTeacherIDOperation(MouseEvent event) {
        changeTableBookOnSearch();
    }   

    @FXML
    private void teacher_clearFieldsOperation(ActionEvent event) {
        clearTeacherFields();
    }
    
    private void clearTeacherFields(){
        teacher_ID.clear();
        teacher_name.clear();
        teacher_bookClass.clear();
        teacher_bookPublisher.clear();
        teacher_bookTitle.clear();
        teacher_bookTitle.clear();
        teacher_booksIssued.clear();
        teacher_displayBookSubject.setText(null);
        teacher_displayDepartment.setText(null);
        
        teacherInfo_ID.setText(null);
        teacherInfo_bookClass.setText(null);
        teacherInfo_bookPublisher.setText(null);
        teacherInfo_bookSubject.setText(null);
        teacherInfo_bookTitle.setText(null);
        teacherInfo_booksIssued.setText(null);
        teacherInfo_department.setText(null);
        teacherInfo_name.setText(null);
        teacher_booksReturned.clear();
    }

    @FXML
    private void teacher_returnBooksOperation(ActionEvent event) {
        
        if (teacherInfo_ID.getText().isEmpty() || teacherInfo_name.getText().isEmpty()||teacherInfo_department.getText().isEmpty() || teacherInfo_bookClass.getText().isEmpty()
                || teacherInfo_bookPublisher.getText().isEmpty() || teacherInfo_bookTitle.getText().isEmpty()||teacherInfo_bookSubject.getText().isEmpty()) {
             
                 String msg = null;
            
                if (teacherInfo_ID.getText().isEmpty()) {
                     msg = "Teacher ID is empty, you must search for record  first";                 
                } else if (teacherInfo_name.getText().isEmpty()) {
                    msg = "Teacher name is empty, you must search for record  first";                    
                } else if (teacherInfo_bookClass.getText().isEmpty()) {
                    msg = "Book class/level is empty, you must search for record  first";
                } else if (teacherInfo_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must search for record  first";
                } else if (teacherInfo_bookTitle.getText().isEmpty()) {
                    msg = "Book Title is empty, you must search for record  first";
                }  else if (teacherInfo_bookSubject.getText().isEmpty()) {
                    msg = "Book Subject is empty, you must search for the record first";                    
                } else if (teacherInfo_department.getText().isEmpty()) {
                    msg = "Teacher department is empty, you must search for the record first";
                } else if (teacher_booksIssued.getText().isEmpty()) {
                    msg = "Number of books returned by this teacher is empty, you must enter it";
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The bOOKS return operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
            
            if (isInt(teacher_booksReturned)) {
                
                //CALSCULATE THE NUMBER OF BOOKS REMAINING AND RETURNED
                int numberOfBooksReceived = Integer.parseInt(teacher_booksReturned.getText());
                int numberOfBooksIssued = Integer.parseInt(teacherInfo_booksIssued.getText());
                int remainingBooks = numberOfBooksIssued-numberOfBooksReceived;
                
                
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to register and return books by, "+teacherInfo_name.getText()+" ("+teacherInfo_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book return");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "INSERT INTO teacherreturns (teacher_ID,teacher_name,teacher_depart,book_subject,book_title,book_class,book_publisher,"
                                        + "quantity_returned) VALUES(?,?,?,?,?,?,?,?)";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setString(1, teacherInfo_ID.getText());
                                pstmt.setString(2, teacherInfo_name.getText());
                                pstmt.setString(3, teacherInfo_department.getText());
                                pstmt.setString(4, teacherInfo_bookSubject.getText());
                                pstmt.setString(5, teacherInfo_bookTitle.getText());
                                pstmt.setString(6, teacherInfo_bookClass.getText());
                                pstmt.setString(7, teacherInfo_bookPublisher.getText());
                                pstmt.setString(8, teacher_booksReturned.getText());

                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    
                                    String sql2 = "UPDATE teacherissued SET quantity_issued=? WHERE id = '"+uniqueTeacherID+"' ";
                                    pstmt= con.prepareStatement(sql2);
                                    pstmt.setInt(1, remainingBooks);
                                    int ii = pstmt.executeUpdate(); // load data into the database  
                                    if (ii>0) {
                                        System.out.println("teacherissued Table has been updated");
                                    }
                                    
                                    if (remainingBooks == 0) {                                        
                                        String sql3 = "DELETE FROM teacherissued WHERE id = ? ";
                                        pstmt = con.prepareStatement(sql3);
                                        pstmt.setInt(1, uniqueTeacherID);
                                        int iii=pstmt.executeUpdate();
                                        if (iii>0) {
                                            System.out.println("teacherissued Table record has been deleted");
                                        }                                        
                                    }
                                    
                                        //show alert before registering data of employee
                                String msg = " Details of book returned have been successfuly saved under "+teacherInfo_name.getText();
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Registration Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                refreshTeacherBookIssueTable();
                                clearTeacherFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not register book issue due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not register book isssue due  to internal error. " +e);
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
    
    private void updateBookSubjects(){  
        try { 
            String sql = "SELECT bookSubject FROM bookcategory";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                bookSubjectData.add( rs.getString("bookSubject"));
            }
            
            // populate class combo box 
            teacher_bookSubjectCombo.setItems(bookSubjectData);
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
    private void teacher_refreshBookSubjectCombo(ActionEvent event) {
         refreshComboSubjects();
    }
    
    @FXML
    private void teacherBookIssueMouseClicked(MouseEvent event) {
        
                try {
                    TeacherBookIssue bkd = teachserIssuedTable.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM teacherissued WHERE teacher_ID = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, bkd.getTeacherID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        
                        int add = rs.getInt("id");
                        uniqueTeacherID = add;
                        System.out.println("Unique teacher ID is "+ uniqueTeacherID);
                        
                        String add1 = rs.getString("teacher_ID");
                        teacher_ID.setText(add1); 
                        teacherInfo_ID.setText(add1);
                        String add2 = rs.getString("teacher_name");
                        teacher_name.setText(add2);
                        teacherInfo_name.setText(add2);
                        String add4 = rs.getString("teacher_depart");
                        teacher_displayDepartment.setText(add4);
                        teacherInfo_department.setText(add4);
                        String add5 = rs.getString("book_subject");
                        teacher_displayBookSubject.setText(add5);
                         teacherInfo_bookSubject.setText(add5);
                        String add6 = rs.getString("book_title");
                        teacher_bookTitle.setText(add6);
                        teacherInfo_bookTitle.setText(add6);
                        String add7 = rs.getString("book_class");
                        teacher_bookClass.setText(add7);
                        teacherInfo_bookClass.setText(add7);
                        String add8 = rs.getString("book_publisher");
                        teacher_bookPublisher.setText(add8);
                        teacherInfo_bookPublisher.setText(add8);
                        String add9 = rs.getString("quantity_issued");
                        teacher_booksIssued.setText(add9);
                        teacherInfo_booksIssued.setText(add9);
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
    private void teacherBookIssueKeyRelease(KeyEvent event) {
        
         teachserIssuedTable.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
                
                try {
                    TeacherBookIssue bkd = teachserIssuedTable.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM teacherissued WHERE teacher_ID = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, bkd.getTeacherID());
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        
                        int add = rs.getInt("id");
                        uniqueTeacherID = add;
                        System.out.println("Unique teacher ID is "+ uniqueTeacherID);
                        
                        String add1 = rs.getString("teacher_ID");
                        teacher_ID.setText(add1); 
                        teacherInfo_ID.setText(add1);
                        String add2 = rs.getString("teacher_name");
                        teacher_name.setText(add2);
                        teacherInfo_name.setText(add2);
                        String add4 = rs.getString("teacher_depart");
                        teacher_displayDepartment.setText(add4);
                        teacherInfo_department.setText(add4);
                        String add5 = rs.getString("book_subject");
                        teacher_displayBookSubject.setText(add5);
                         teacherInfo_bookSubject.setText(add5);
                        String add6 = rs.getString("book_title");
                        teacher_bookTitle.setText(add6);
                        teacherInfo_bookTitle.setText(add6);
                        String add7 = rs.getString("book_class");
                        teacher_bookClass.setText(add7);
                        teacherInfo_bookClass.setText(add7);
                        String add8 = rs.getString("book_publisher");
                        teacher_bookPublisher.setText(add8);
                        teacherInfo_bookPublisher.setText(add8);
                        String add9 = rs.getString("quantity_issued");
                        teacher_booksIssued.setText(add9);
                        teacherInfo_booksIssued.setText(add9);
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

    
   private void changeTableBookOnSearch(){
        
        //wrap the observablelist in a FilteredList()
        FilteredList<TeacherBookIssue> filteredData =  new FilteredList<>(teacherBooksData,p -> true);
        
        //set the filter predicate whenever the filter changes
        teacher_searchTeacherID.textProperty().addListener((observable,oldValue,newValue)->{
                
            filteredData.setPredicate(TeacherBookIssue -> {
            
                //if filter text is empty, display all product
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                //compare product id and product name of every product with the filter text
                String lowerCaseFilter =newValue.toLowerCase();
               
                if (TeacherBookIssue.getTeacherID().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches product Id
                } 
                
                return false; // Do not match            
            });
        
        });
        
        //wrap the filteredList in a sortedList
        SortedList<TeacherBookIssue> sortedData = new SortedList<>(filteredData);
        
        //bind the sortedData to the Tableview
        sortedData.comparatorProperty().bind(teachserIssuedTable.comparatorProperty());
        
        //add sorted and filtered data to the table
        teachserIssuedTable.setItems(sortedData); 
    }
   
   // method to load Content to the books issued to teachers table
     public void update_teacherIssuedDetailTable(){ 
        teacher_columnID.setCellValueFactory(new PropertyValueFactory<>("teacherID"));
        teacher_columnName.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        teacher_columnDepart.setCellValueFactory(new PropertyValueFactory<>("teacherDepart"));
        teacher_columnQuantity.setCellValueFactory(new PropertyValueFactory<>("bookQuantity"));
        teacher_columnBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        teacher_columnBookClass.setCellValueFactory(new PropertyValueFactory<>("bookClass"));
        teacher_columnDateIssued.setCellValueFactory(new PropertyValueFactory<>("dateIssued"));
        try { 
            String sql = "SELECT * FROM teacherissued";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
                teacherBooksData.add( new TeacherBookIssue(
                        rs.getString("teacher_ID"),
                        rs.getString("teacher_name"),
                        rs.getString("teacher_depart"),
                        rs.getString("quantity_issued"),                        
                        rs.getString("book_title"),
                        rs.getString("book_class"),
                        rs.getString("date_issued") 
                ));         
            }
            //load items to the table
            teachserIssuedTable.setItems(teacherBooksData);
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
     
     private void refreshTeacherBookIssueTable(){
         teacherBooksData.clear();
         update_teacherIssuedDetailTable();
     }
     
    @FXML
    private void teacherCopy_bookSubjectRefresh(ActionEvent event) {
        refreshTeacherCopySubject();
        
    }
    private void refreshTeacherCopySubject(){
        teacherCopySubjectData.clear();
        updateTeacherCopySubject();
    }
    
    private void updateTeacherCopySubject(){
        
        try { 
            String sql = "SELECT bookSubject FROM bookcategory";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                teacherCopySubjectData.add( rs.getString("bookSubject"));
            }
            
            // populate class combo box 
            teacherCopy_bookSubjectCombo.setItems(teacherCopySubjectData);
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
    

    @FXML
    private void teacherCopy_registerOperation(ActionEvent event) {

        if (teacherCopy_ID.getText().isEmpty() || teacherCopy_bookClass.getText().isEmpty()
                ||teacherCopy_bookPublisher.getText().isEmpty() || teacherCopy_title.getText().isEmpty()
                ||teacherCopy_bookSubjectCombo.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (teacherCopy_ID.getText().isEmpty()) {
                     msg = "Teacher's book ID is empty, you must enter it";                 
                } else if (teacherCopy_bookClass.getText().isEmpty()) {
                    msg = "Book class is empty, you must enter it";
                } else if (teacherCopy_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (teacherCopy_title.getText().isEmpty()) {
                    msg = "Book title is empty, you must enter it";
                }  else if (teacherCopy_bookSubjectCombo.getSelectionModel().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                }             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book registeration operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
                            
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to register this teacher's copy , "+teacherCopy_title.getText()+" ("+teacherCopy_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book registration");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "INSERT INTO teachercopy (book_id,book_subject,book_title,book_class,book_publisher) VALUES(?,?,?,?,?)";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setString(1, teacherCopy_ID.getText());
                                pstmt.setString(2, teacherCopy_bookSubjectCombo.getSelectionModel().getSelectedItem().toString());
                                pstmt.setString(3, teacherCopy_title.getText());
                                pstmt.setString(4, teacherCopy_bookClass.getText());
                                pstmt.setString(5, teacherCopy_bookPublisher.getText());
                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " Details of book have been successfuly saved with ID "+teacherCopy_ID.getText()+ ", to be given to a teacher.";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Registration Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                refreshTeacherCopiesData();
                                clearTeacherCopyFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not register book issue due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not register book isssue due  to internal error. " +e);
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
    
    private void clearTeacherCopyFields(){
    
        teacherCopy_ID.clear();
        teacherCopy_bookClass.clear();
        teacherCopy_bookPublisher.clear();
        teacherCopy_title.clear();
        refreshTeacherCopySubject();
        teacherCopy_displayBookSubject.setText(null);
    }

    @FXML
    private void teacherCopy_updateOpereaion(ActionEvent event) {
        

        if (teacherCopy_ID.getText().isEmpty() || teacherCopy_bookClass.getText().isEmpty()
                ||teacherCopy_bookPublisher.getText().isEmpty() || teacherCopy_title.getText().isEmpty()
                ||teacherCopy_bookSubjectCombo.getSelectionModel().isEmpty()) {
             
                 String msg = null;
            
                if (teacherCopy_ID.getText().isEmpty()) {
                     msg = "Teacher's book ID is empty, you must enter it";                 
                } else if (teacherCopy_bookClass.getText().isEmpty()) {
                    msg = "Book class is empty, you must enter it";
                } else if (teacherCopy_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (teacherCopy_title.getText().isEmpty()) {
                    msg = "Book title is empty, you must enter it";
                }  else if (teacherCopy_bookSubjectCombo.getSelectionModel().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                }             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book update operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
                            
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to update this teacher's copy , "+teacherCopy_title.getText()+" ("+teacherCopy_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book update");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "UPDATE teachercopy SET book_id=?,book_subject=?,book_title=?,book_class=?,book_publisher=? WHERE book_id = '"+teacherCopy_ID.getText()+" ' ";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setString(1, teacherCopy_ID.getText());
                                pstmt.setString(2, teacherCopy_bookSubjectCombo.getSelectionModel().getSelectedItem().toString());
                                pstmt.setString(3, teacherCopy_title.getText());
                                pstmt.setString(4, teacherCopy_bookClass.getText());
                                pstmt.setString(5, teacherCopy_bookPublisher.getText());
                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " Details of book have been successfuly updated with ID "+teacherCopy_ID.getText()+ ", to be given to a teacher.";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Update Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                refreshTeacherCopiesData();
                                clearTeacherCopyFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not update book issue due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not update book isssue due  to internal error. " +e);
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
    
    @FXML
    private void teacherCopy_searchIDOperation(ActionEvent event) {
        
                if (teacherCopy_searchID.getText().isEmpty()) {

                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Search Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The Book has failed Search operation.\n"
                                + "Ensure the Book ID has been inserted.");
                    errorIssue.showAndWait(); 

                }else {
                    try {
                        String sql = "SELECT * FROM teachercopy WHERE book_id = ?  ";
                        pstmt = con.prepareStatement(sql);
                        pstmt.setString(1, teacherCopy_searchID.getText());

                        rs=pstmt.executeQuery();
                        if (rs.next()) {
                            String add1 = rs.getString("book_id");
                            teacherCopy_ID.setText(add1); 
                            String add2 = rs.getString("book_subject");
                            teacherCopy_displayBookSubject.setText(add2);
                            String add4 = rs.getString("book_title");
                            teacherCopy_title.setText(add4);
                            String add5 = rs.getString("book_class");
                            teacherCopy_bookClass.setText(add5);
                            String add6 = rs.getString("book_publisher");
                            teacherCopy_bookPublisher.setText(add6);
                            

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
    private void teacherCopyInfo_searchIDOperation(ActionEvent event) {
        
                if (teacherCopyInfo_searchID.getText().isEmpty()) {

                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Search Failed");
                    errorIssue.setGraphic(errorImage);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("The Book has failed Search operation.\n"
                                + "Ensure the Book ID has been inserted.");
                    errorIssue.showAndWait(); 

                }else {
                    try {
                        String sql = "SELECT * FROM teachercopy WHERE book_id = ?  ";
                        pstmt = con.prepareStatement(sql);
                        pstmt.setString(1, teacherCopyInfo_searchID.getText());

                        rs=pstmt.executeQuery();
                        if (rs.next()) {
                            String add1 = rs.getString("book_id");
                            teacherCopyInfo_ID.setText(add1); 
                            String add2 = rs.getString("book_subject");
                           teacherCopyInfo_bookSubject.setText(add2);
                            String add4 = rs.getString("book_title");
                            teacherCopyInfo_bookTitle.setText(add4);
                            String add5 = rs.getString("book_class");
                            teacherCopyInfo_bookClass.setText(add5);
                            String add6 = rs.getString("book_publisher");
                            teacherCopyInfo_bookPublisher.setText(add6);
                            boolean add7 =  rs.getBoolean("book_isAvail");
                                    if (add7) {
                                        teacherCopyInfo_bookStatus.setText("Book is available.");
                                        teacherCopyInfo_teacherIssued.setText("No teacher issued");                                
                                    } else {
                                        teacherCopyInfo_bookStatus.setText("Book not available");
                                        String add8 = rs.getString("teacher_issued");
                                        teacherCopyInfo_teacherIssued.setText("Book is issued to " + add8);
                                    }

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
    private void teacherCopy_deleteOperation(ActionEvent event) {
        
        if (teacherCopy_ID.getText().isEmpty()) {
            
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
               String query = "DELETE FROM teachercopy WHERE book_id = ? ";
               pstmt = con.prepareStatement(query);
               pstmt.setString(1, teacherCopy_ID.getText());
               pstmt.executeUpdate();            
               pstmt.close(); 
               
               refreshTeacherCopiesData();
               clearTeacherCopyFields();
               
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
        }
    }

    @FXML
    private void teacherCopy_clearFieldsOperation(ActionEvent event) {
        clearTeacherCopyFields();
    }
    
    
    @FXML
    private void issueBookToTeacherOperation(ActionEvent event) {
        
        if (teacherCopyInfo_ID.getText().isEmpty() || teacherCopyInfo_bookClass.getText().isEmpty()
                ||teacherCopyInfo_bookPublisher.getText().isEmpty() || teacherCopyInfo_bookTitle.getText().isEmpty()
                ||teacherCopyInfo_bookSubject.getText().isEmpty()) {
             
                 String msg = null;
            
                if (teacherCopyInfo_ID.getText().isEmpty()) {
                     msg = "Teacher's book ID is empty, you must enter it";                 
                } else if (teacherCopyInfo_bookClass.getText().isEmpty()) {
                    msg = "Book class is empty, you must enter it";
                } else if (teacherCopyInfo_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (teacherCopyInfo_bookTitle.getText().isEmpty()) {
                    msg = "Book title is empty, you must enter it";
                }  else if (teacherCopyInfo_bookSubject.getText().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                }             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book issue operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
                            
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to issue this teacher's copy , "+teacherCopyInfo_bookTitle.getText()+" ("+teacherCopyInfo_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book issue");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "UPDATE teachercopy SET book_isAvail=?,teacher_issued=? WHERE book_id = '"+teacherCopyInfo_ID.getText()+" ' ";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setBoolean(1, false);
                                pstmt.setString(2, teacherCopyInfo_issuedTeacherName.getText());
                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " The copy has been issued to "+teacherCopyInfo_issuedTeacherName.getText()+ ".";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Issue Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                clearTeacherCopyInfoFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not update book issue due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not update book isssue due  to internal error. " +e);
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

    @FXML
    private void receiveReturnedBook(ActionEvent event) {
        
        if (teacherCopyInfo_ID.getText().isEmpty() || teacherCopyInfo_bookClass.getText().isEmpty()
                ||teacherCopyInfo_bookPublisher.getText().isEmpty() || teacherCopyInfo_bookTitle.getText().isEmpty()
                ||teacherCopyInfo_bookSubject.getText().isEmpty()) {
             
                 String msg = null;
            
                if (teacherCopyInfo_ID.getText().isEmpty()) {
                     msg = "Teacher's book ID is empty, you must enter it";                 
                } else if (teacherCopyInfo_bookClass.getText().isEmpty()) {
                    msg = "Book class is empty, you must enter it";
                } else if (teacherCopyInfo_bookPublisher.getText().isEmpty()) {
                    msg = "Book publisher is empty, you must enter it";
                } else if (teacherCopyInfo_bookTitle.getText().isEmpty()) {
                    msg = "Book title is empty, you must enter it";
                }  else if (teacherCopyInfo_bookSubject.getText().isEmpty()) {
                    msg = "Book Subject is empty, you must choose from the drop-down menu";
                }             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The book return operation has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
                            
                      //show confirm alert before registering data of issued
                        String ms = " Are you sure you want to receive this returned teacher's copy , "+teacherCopyInfo_bookTitle.getText()+" ("+teacherCopyInfo_ID.getText()+")";           
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirm book issue");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText(ms);

                        Optional<ButtonType> response = alert.showAndWait();            
                        if (response.get()==ButtonType.OK) {
                           try {
                                String sql = "UPDATE teachercopy SET book_isAvail=?,teacher_issued=? WHERE book_id = '"+teacherCopyInfo_ID.getText()+" ' ";
                                pstmt= con.prepareStatement(sql);

                                pstmt.setBoolean(1, true);
                                pstmt.setString(2, "None");
                                int i = pstmt.executeUpdate(); // load data into the database             
                                if (i>0) {
                                    //show alert before registering data of employee
                                String msg = " The copy has been received and returned.";
                                Alert al = new Alert(Alert.AlertType.INFORMATION);
                                al.setTitle("Return Information Dialog");
                                al.setGraphic(imageSuccess);
                                al.setHeaderText(null);
                                al.setContentText(msg);
                                al.showAndWait();
                                
                                clearTeacherCopyInfoFields();
                                } else {
                                    Alert error = new Alert(Alert.AlertType.ERROR);
                                    error.setTitle("Failed");
                                    error.setGraphic(errorImage);
                                    error.setContentText("OOPS! Could not Return the  book due  to internal error.");
                                    error.showAndWait();
                                } 
                                pstmt.close();

                            } catch (Exception e) {
                                Alert at = new Alert(Alert.AlertType.ERROR);
                                at.setTitle("Error in registration of book");
                                at.setGraphic(errorImage);
                                at.setHeaderText(null);
                                at.setContentText("OOPS! Could not return book due  to internal error. " +e);
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
    
   
    private void clearTeacherCopyInfoFields(){
        teacherCopyInfo_ID.setText(null);
        teacherCopyInfo_bookClass.setText(null);
        teacherCopyInfo_bookPublisher.setText(null);
        teacherCopyInfo_bookStatus.setText(null);
        teacherCopyInfo_bookSubject.setText(null);
        teacherCopyInfo_bookTitle.setText(null);
        teacherCopyInfo_issuedTeacherName.clear();
        teacherCopyInfo_teacherIssued.setText(null);
        teacherCopyInfo_searchID.clear();
        
    }
    
    private void refreshTeacherCopiesData(){        
        teacherCopyData.clear();
        refreshTeacherCopies();         
    }

    private void refreshTeacherCopies() {
        try {
            String sql = "SELECT * FROM teachercopy ";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                      teacherCopyData.add("("+ rs.getString("book_id")+") "+rs.getString("book_title")+" - "+rs.getString("book_subject")+" - "+ rs.getString("book_class"));            
               }
                                
                pstmt.close(); 
        } catch (SQLException ex) {
            Logger.getLogger(SchoolClassController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // show this details on the listview
        teacherCopiesLIstView.getItems().setAll(teacherCopyData);
    }
    
    @FXML
    private void clearAllTeacherCopyInfoFields(ActionEvent event) {
        clearTeacherCopyInfoFields();
    }
    
    @FXML
    private void printIssuedBooksToTeachers(ActionEvent event) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to generate all books issued to Teachers?\n");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                 
                  // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save Issued books to teachers");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        teacherCopiesPrintintingStatus.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createIssuedBooksToTeachers(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated issued books to teachers report and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           teacherCopiesPrintintingStatus.setVisible(false); 
           
            }
    }
    
    @FXML
    private void printReturnedBooksByTeachersReport(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to generate all books returned by Teachers?\n");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                
                                // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save returned teacher copies");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        teacherCopiesPrintintingStatus.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createReturnedBooksByTeachers(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated returned books report and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           teacherCopiesPrintintingStatus.setVisible(false); 
       
            }
    }

    @FXML
    private void printAllTeacherCopies(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to generate all teachers' copies?\n");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                
                // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save all teachers copies");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        teacherCopiesPrintintingStatus.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createAllTeachersCopies(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated all teachers' copies and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           teacherCopiesPrintintingStatus.setVisible(false); 
            }
    }

    @FXML
    private void printAllCopiesIssuedToTeachers(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to generate all issued teachers' copies?\n");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                 
               // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save issued books to teachers");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        teacherCopiesPrintintingStatus.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createTeachersCopiesIssued(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated All issued books to teachers and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           teacherCopiesPrintintingStatus.setVisible(false); 
            }
        
    }
    
            // method to generate isssued teachers' copies////////////////////////////////////////////
   public void createTeachersCopiesIssued(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
            String sql = "SELECT * FROM teachercopy WHERE book_isAvail = false ";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "Issued teacher copies as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            
            
            // Add a table
            float[] columnWidths = {2.3f,2f,2.5f,1f,2f,2f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);            
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Publisher", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Issued To", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("book_id");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);
                
                String subject = rs.getString("book_subject");
                insertCell(issuedBookTable, subject, Element.ALIGN_LEFT, 1, bfBold);                 
                
                String title = rs.getString("book_title");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, bfBold);
                
                String classbook = rs.getString("book_class");
                insertCell(issuedBookTable, classbook, Element.ALIGN_LEFT, 1, bfBold);
                
                String publisher = rs.getString("book_publisher");
                insertCell(issuedBookTable, publisher, Element.ALIGN_LEFT, 1, bfBold);
                
                String issuedTo = rs.getString("teacher_issued");
                insertCell(issuedBookTable, issuedTo, Element.ALIGN_LEFT, 1, bfBold);
                
                                        
            }
            
            document.add(issuedBookTable);
            
            document.close();
            pstmt.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (SQLException ex) {     
                Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
            }     
     
     }
    
    
   
         // method to generate isssued teachers' copies////////////////////////////////////////////
   public void createAllTeachersCopies(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
            String sql = "SELECT * FROM teachercopy";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "All teacher copies as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            
            
            // Add a table
            float[] columnWidths = {2.3f,2f,2.5f,1f,2f,1f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);            
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Publisher", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Issued?", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("book_id");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);
                
                String subject = rs.getString("book_subject");
                insertCell(issuedBookTable, subject, Element.ALIGN_LEFT, 1, bfBold);                 
                
                String title = rs.getString("book_title");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, bfBold);
                
                String classbook = rs.getString("book_class");
                insertCell(issuedBookTable, classbook, Element.ALIGN_LEFT, 1, bfBold);
                
                String publisher = rs.getString("book_publisher");
                insertCell(issuedBookTable, publisher, Element.ALIGN_LEFT, 1, bfBold);
                
                boolean iaAvail = rs.getBoolean("book_isAvail");
                String available = null;
                if (iaAvail) {
                    available = "No";
                } else {
                    available = "Yes";
                }
                insertCell(issuedBookTable, available, Element.ALIGN_LEFT, 1, bfBold);
                
                                        
            }
            
            document.add(issuedBookTable);
            
            document.close();
            pstmt.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (SQLException ex) {     
                Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
            }     
     
     }
   
   
     // method to generate isssued teachers' copies////////////////////////////////////////////
   public void createReturnedBooksByTeachers(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
            String sql = "SELECT * FROM teacherreturns";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "Returned books by teachers ( Full report) as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            // Add a table
            float[] columnWidths = {1f,2f,1.5f,2f,1f,1.8f,2f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "Tch. ID", Element.ALIGN_LEFT, 1, bfBold12);            
            insertCell(issuedBookTable, "Tch. Name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Book title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Returned Qnty", Element.ALIGN_MIDDLE, 1, bfBold12);
            insertCell(issuedBookTable, "Date Returned", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("teacher_ID");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);
                
                String subject = rs.getString("teacher_name");
                insertCell(issuedBookTable, subject, Element.ALIGN_LEFT, 1, bfBold);                 
                
                String title = rs.getString("book_subject");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, bfBold);
                
                String classbook = rs.getString("book_title");
                insertCell(issuedBookTable, classbook, Element.ALIGN_LEFT, 1, bfBold);
                
                String publisher = rs.getString("book_class");
                insertCell(issuedBookTable, publisher, Element.ALIGN_LEFT, 1, bfBold);
                
                int qnty = rs.getInt("quantity_returned");               
                insertCell(issuedBookTable, String.valueOf(qnty), Element.ALIGN_LEFT, 1, bfBold);
                
                String date = rs.getString("date_returned");
                insertCell(issuedBookTable, date, Element.ALIGN_LEFT, 1, bfBold);
                
                                        
            }
            
            document.add(issuedBookTable);
            
            document.close();
            pstmt.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (SQLException ex) {     
                Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
            }     
     
     }
         
     // method to generate isssued teachers' copies////////////////////////////////////////////
   public void createIssuedBooksToTeachers(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
            String sql = "SELECT * FROM teacherissued";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "Issued books to teachers as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            // Add a table
            float[] columnWidths = {1f,2f,1.5f,2f,1f,1.8f,2f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "Tch. ID", Element.ALIGN_LEFT, 1, bfBold12);            
            insertCell(issuedBookTable, "Tch. Name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Book title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Issued Qnty", Element.ALIGN_MIDDLE, 1, bfBold12);
            insertCell(issuedBookTable, "Date Issued", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("teacher_ID");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);
                
                String subject = rs.getString("teacher_name");
                insertCell(issuedBookTable, subject, Element.ALIGN_LEFT, 1, bfBold);                 
                
                String title = rs.getString("book_subject");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, bfBold);
                
                String classbook = rs.getString("book_title");
                insertCell(issuedBookTable, classbook, Element.ALIGN_LEFT, 1, bfBold);
                
                String publisher = rs.getString("book_class");
                insertCell(issuedBookTable, publisher, Element.ALIGN_LEFT, 1, bfBold);
                
                int qnty = rs.getInt("quantity_issued");               
                insertCell(issuedBookTable, String.valueOf(qnty), Element.ALIGN_LEFT, 1, bfBold);
                
                String date = rs.getString("date_issued");
                insertCell(issuedBookTable, date, Element.ALIGN_LEFT, 1, bfBold);
                
                                        
            }
            
            document.add(issuedBookTable);
            
            document.close();
            pstmt.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (SQLException ex) {     
                Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
            }     
     
     }       
    

    //############################## other methods ###########################
    
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
    
            //Method to open a fileChooser dialog box
        public void fileChooserOpener(){
                fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(                
                new FileChooser.ExtensionFilter("PDF Files","*.pdf")
//               new FileChooser.ExtensionFilter("All Files","*.*")        
//               new FileChooser.ExtensionFilter("Excel Files","*.xslx") 
//                new FileChooser.ExtensionFilter("Text Files","*.txt"),
//                new FileChooser.ExtensionFilter("Word Files","*.docx"),
//                new FileChooser.ExtensionFilter("Image Files","*.png","*.jpg","*.gif"),
//                new FileChooser.ExtensionFilter("Audio Files","*.wav","*.mp3","*.mp4","*.acc") 
                
        );   
    } 
        
    private  void addMetaData(Document document) {
        document.addTitle( "Book watch" );
        document.addSubject( "Books management software" );
        document.addKeywords( "School, software, Books, Students, Teachers" );
        document.addAuthor( "Daviscah Tech Ltd" );
        document.addCreator( "Daviscah Tech Ltd" );
    } 
    
    //Method to creat an empty line in the document
     private static void addEmptyLine(Paragraph paragraph, int number) {
        for ( int i = 0 ; i < number; i++) {
              paragraph.add( new Paragraph( " " ));
        }
    }
     
    // method to format table cell with data
     private void insertCell(PdfPTable table,String text,int align,int colspan,Font font){
     
         PdfPCell cell = new PdfPCell(new Phrase(text.trim(),font));
         cell.setHorizontalAlignment(align);
         cell.setColspan(colspan);
         if (text.trim().equalsIgnoreCase("")) {
             cell.setMinimumHeight(10f);
         }
         table.addCell(cell);
     }        

  
    
    


    
    
}// end of classs
