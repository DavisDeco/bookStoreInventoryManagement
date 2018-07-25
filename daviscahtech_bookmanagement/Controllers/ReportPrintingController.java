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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
public class ReportPrintingController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
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
    @FXML
    private ComboBox classListCombobox;
    private final ObservableList classListData = FXCollections.observableArrayList();
    @FXML
    private ComboBox issuedBooksCombobox;
    private final ObservableList issuedBooksData = FXCollections.observableArrayList();
    @FXML
    private ComboBox lostBookCombobox;
    private final ObservableList lostBooksData = FXCollections.observableArrayList();
    @FXML
    private HBox classlistPleaseWait;
    @FXML
    private HBox issuebooPleaseWait;
    @FXML
    private HBox lostbooksPleaseWait;
    @FXML
    private HBox generalBookPleaseWait;
    
    //variables to hold school info
    String schoolName = null;
    String schoolContact = null;
    String schoolAddress = null;
    String schoolRegion = null;
    String schoolEmail = null;
    String schoolWebsite = null;  
    
    // varaiables to hold selected strings from combo box
    String selectedClassForClassList = null;
    String selectedClassForIssuedBooks = null;
    String selectedClassForLostBooks = null;
                
     //instance of FIlechooser
    private FileChooser fileChooser;
    //instance of File
    private File file;
    private Window Stage;
    
    
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
       
        // call method to load variables with school info
        loadSchoolInfo();
        
        //Create a folder in the C drive to put all classlist generated
               File directory = new File("C:/Book Watch/Class_lists");
               if (!directory.exists()) {
                   if (directory.mkdirs()) {
                       System.out.println("Directory created");
                   } else {
                       System.out.println("Directory not created");
                   }
                } 
               
        //Create a folder in the C drive to put all ISSUED BOOKS generated
               File directory2 = new File("C:/Book Watch/issued_books");
               if (!directory2.exists()) {
                   if (directory2.mkdirs()) {
                       System.out.println("Directory2 created");
                   } else {
                       System.out.println("Directory2 not created");
                   }
                }  
               
        //Create a folder in the C drive to put all LOST BOOKS generated
               File directory3 = new File("C:/Book Watch/lost_books");
               if (!directory3.exists()) {
                   if (directory3.mkdirs()) {
                       System.out.println("Directory3 created");
                   } else {
                       System.out.println("Directory3 not created");
                   }
                }
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
    
    //####################### METHODS TO HANDLE STUDENT CLASS LIST DETAILS ##########
    private void updateComboClassList(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                classListData.add( rs.getString("className"));         
            }
            // populate class combo box 
            classListCombobox.setItems(classListData);
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
    public void refreshComboClassList(){    
        classListData.clear();
        updateComboClassList();
    }
    
     @FXML
    private void refresh_classListCombobox(ActionEvent event) {
        refreshComboClassList();
    }

    @FXML
    private void generateClassListOperation(ActionEvent event) throws SQLException {
        
        if (classListCombobox.getSelectionModel().isEmpty()) {             
                 String msg = null;
          if (classListCombobox.getSelectionModel().isEmpty()) {
                    msg = "Class is empty, you must choose from the drop-down menu";
            }
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("FAILED");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("Report generation for printing failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {
            selectedClassForClassList = classListCombobox.getSelectionModel().getSelectedItem().toString();
            
            // generate pdf
            fileChooserOpener();
                   fileChooser.setTitle("Save Class list");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        classlistPleaseWait.setVisible(true);                        
                        String path = file.getAbsolutePath();
                        createClassListPDF(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated class list of "+selectedClassForClassList+" and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           classlistPleaseWait.setVisible(false);
            
        } 
    }
    
    //####################### METHODS TO HANDLE STUDENT CLASS LIST DETAILS ##########

    //####################### METHODS TO HANDLE ISSUED BOOK DETAILS ##########
    private void updateComboIssuedBooks(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                issuedBooksData.add( rs.getString("className"));         
            }
            // populate class combo box 
            issuedBooksCombobox.setItems(issuedBooksData);
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
    public void refreshComboIssuedBooks(){    
        issuedBooksData.clear();
        updateComboIssuedBooks();
    }
    
    
    
    @FXML
    private void refresh_issuedBooksCombobox(ActionEvent event) {
        refreshComboIssuedBooks();
    }

    @FXML
    private void generateIssuedBooksPerClass(ActionEvent event) throws SQLException {  
               
            if (issuedBooksCombobox.getSelectionModel().isEmpty()) {             
                 String msg = null;
            if (issuedBooksCombobox.getSelectionModel().isEmpty()) {
                    msg = "Class is empty, you must choose from the drop-down menu";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("FAILED");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("Report generation for printing failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
            
            selectedClassForIssuedBooks = issuedBooksCombobox.getSelectionModel().getSelectedItem().toString();
            
            // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save issued books");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        issuebooPleaseWait.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createIssuedBooks(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated issued books in "+selectedClassForIssuedBooks+" and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           issuebooPleaseWait.setVisible(false); 
                
        } 
    }
    
    //####################### END METHODS TO HANDLE ISSUED BOOK LIST DETAILS ########## 
    
    //####################### METHODS TO HANDLE ISSUED BOOK DETAILS ##########
    private void updateComboLostBooks(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                lostBooksData.add( rs.getString("className"));         
            }
            // populate class combo box 
            lostBookCombobox.setItems(lostBooksData);
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
    public void refreshComboLostBooks(){    
        lostBooksData.clear();
        updateComboLostBooks();
    }
    
    
    
   @FXML
    private void refresh_lostBookCombobox(ActionEvent event) {
        refreshComboLostBooks();
    }

    @FXML
    private void generateLostBooksPerClass(ActionEvent event) throws SQLException { 
               
          if (lostBookCombobox.getSelectionModel().isEmpty()) {             
                 String msg = null;
            if (lostBookCombobox.getSelectionModel().isEmpty()) {
                    msg = "Class is empty, you must choose from the drop-down menu";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("FAILED");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("Report generation for printing failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {  
            selectedClassForLostBooks = lostBookCombobox.getSelectionModel().getSelectedItem().toString();
            
            // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save Lost books");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        lostbooksPleaseWait.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createLostBooks(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated Lost books in "+selectedClassForLostBooks+" and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           lostbooksPleaseWait.setVisible(false); 
              
        } 
    }
    
    //####################### END METHODS TO HANDLE ISSUED BOOK LIST DETAILS ##########    

    @FXML
    private void generateAllIssuedbooks(ActionEvent event) throws SQLException {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setGraphic(bookImageView);
        alert.setHeaderText(null);
        alert.setContentText("Do you want to generate all books issued to student?\n"
                + "The process will take a few minutes to generate records.");         
        Optional <ButtonType> obt = alert.showAndWait();

        if (obt.get()== ButtonType.OK) {
            
                // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save all issued school books");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        generalBookPleaseWait.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createAllIssuedBooks(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated All issued books and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           generalBookPleaseWait.setVisible(false); 
        }
    }

    @FXML
    private void generateAllLostBooks(ActionEvent event) throws SQLException {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setGraphic(bookImageView);
        alert.setHeaderText(null);
        alert.setContentText("Do you want to generate all books lost by student?\n"
                + "The process will take a few minutes to generate records.");         
        Optional <ButtonType> obt = alert.showAndWait();

        if (obt.get()== ButtonType.OK) {
                // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save all lost school books");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        generalBookPleaseWait.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createAllLostBooks(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated All lost books and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           generalBookPleaseWait.setVisible(false); 
            
        }
        
        
    }

    @FXML
    private void generateAllSchooltBooks(ActionEvent event) throws SQLException {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to generate all school books\n");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save all school books");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {
                        generalBookPleaseWait.setVisible(true);                       
                        String path = file.getAbsolutePath();
                        createAllSchoolBooks(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated All school books and it's ready for printing.\n");
                    errorIssue.showAndWait();
            
           generalBookPleaseWait.setVisible(false); 
            }
    }

// ################################ Other Methods #################################
    
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
    
     // method to generate class list////////////////////////////////////////////
   public void createClassListPDF(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
             String sq = "SELECT * FROM students WHERE student_class = ? ";
             pstmt = con.prepareStatement(sq);
             pstmt.setString(1, selectedClassForClassList );
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
                    + "Class Lits of: "+ selectedClassForClassList +" as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            
            
            // Add a table
            float[] columnWidths = {1.5f,4f,4f,2f,2f};
            PdfPTable classListTable = new PdfPTable(columnWidths);
            classListTable.setWidthPercentage(90f);
            
            insertCell(classListTable, "ID NO.", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(classListTable, "Sudent Name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(classListTable, "Remarks (book ID/status)", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(classListTable, "Std. sign", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(classListTable, "Tch. sign", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String studentID = rs.getString("student_id");
                insertCell(classListTable, studentID, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentName = rs.getString("student_name");
                insertCell(classListTable, studentName, Element.ALIGN_LEFT, 1, bfBold);                
                insertCell(classListTable, "", Element.ALIGN_LEFT, 1, bfBold);
                insertCell(classListTable, "", Element.ALIGN_LEFT, 1, bfBold);
                insertCell(classListTable, "", Element.ALIGN_LEFT, 1, bfBold);
                         
            }
            
            document.add(classListTable);
            
            document.close();
            pstmt.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (SQLException ex) {     
                Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
            }     
     
     }
   
        // method to generate isssued books per class////////////////////////////////////////////
   public void createIssuedBooks(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
             String sq = "SELECT * FROM issuedbook WHERE studentClass = ? ";
             pstmt = con.prepareStatement(sq);
             pstmt.setString(1, selectedClassForIssuedBooks );
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
                    + "Issued books in: "+ selectedClassForIssuedBooks +" as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            
            
            // Add a table
            float[] columnWidths = {1f,2.5f,1.5f,2.8f,2.3f,0.8f,1.2f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "ID NO.", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Sudent Name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Status", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Issued By", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String studentID = rs.getString("studentID");
                insertCell(issuedBookTable, studentID, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentName = rs.getString("studentName");
                insertCell(issuedBookTable, studentName, Element.ALIGN_LEFT, 1, bfBold);                 
                
                String bookSubject = rs.getString("bookCategory");
                insertCell(issuedBookTable, bookSubject, Element.ALIGN_LEFT, 1, bfBold);
                
                String bookID = rs.getString("bookID");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);
                
                String title = rs.getString("bookTitle");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, bfBold);
                
                String bookStatus = rs.getString("bookStatus");
                insertCell(issuedBookTable, bookStatus, Element.ALIGN_LEFT, 1, bfBold);
                
                String issuedBy = rs.getString("teacherIssued");
                insertCell(issuedBookTable, issuedBy, Element.ALIGN_LEFT, 1, bfBold);
                         
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
   
         // method to generate lost books per class////////////////////////////////////////////
   public void createLostBooks(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
             String sq = "SELECT * FROM lostbooks WHERE studentClass = ? ";
             pstmt = con.prepareStatement(sq);
             pstmt.setString(1, selectedClassForLostBooks );
             rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 10);
            Font dateFont = new Font(Font.FontFamily.TIMES_ROMAN, 8);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "Lost books in: "+ selectedClassForLostBooks +" as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            
            
            // Add a table
            float[] columnWidths = {1f,2.3f,1.5f,2.8f,2f,1.1f,2.2f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "ID NO.", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Sudent Name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Date Lost", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String studentID = rs.getString("studentID");
                insertCell(issuedBookTable, studentID, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentName = rs.getString("studentName");
                insertCell(issuedBookTable, studentName, Element.ALIGN_LEFT, 1, bfBold);                 
                
                String bookSubject = rs.getString("lostbookSubject");
                insertCell(issuedBookTable, bookSubject, Element.ALIGN_LEFT, 1, bfBold);
                
                String bookID = rs.getString("lostBookID");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);
                
                String title = rs.getString("lostBookTitle");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, bfBold);
                
                String bookClass = rs.getString("lostBookClass");
                insertCell(issuedBookTable, bookClass, Element.ALIGN_LEFT, 1, bfBold);
                
                String lostDate = rs.getString("dateLost");
                insertCell(issuedBookTable, lostDate, Element.ALIGN_LEFT, 1, dateFont);
                         
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
      
         // method to generate lost books per class////////////////////////////////////////////
   public void createAllIssuedBooks(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
             String sq = "SELECT * FROM issuedbook ";
             pstmt = con.prepareStatement(sq);
             rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            Font idFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "All issued school books as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            // Add a table
            float[] columnWidths = {2.8f,2f,1.5f,2.5f,0.8f,1.7f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Student ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Student name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Issued by", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("bookID");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, idFont);                               
                
                String bookSubject = rs.getString("bookCategory");
                insertCell(issuedBookTable, bookSubject, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentID = rs.getString("studentID");
                insertCell(issuedBookTable, studentID, Element.ALIGN_LEFT, 1, bfBold);
                
                String name = rs.getString("studentName");
                insertCell(issuedBookTable, name, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentClass = rs.getString("studentClass");
                insertCell(issuedBookTable, studentClass, Element.ALIGN_LEFT, 1, bfBold);
                
                String issuedBy = rs.getString("teacherIssued");
                insertCell(issuedBookTable, issuedBy, Element.ALIGN_LEFT, 1, bfBold);
                         
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
    
   
         // method to generate lost books per class////////////////////////////////////////////
   public void createAllLostBooks(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
             String sq = "SELECT * FROM lostbooks ";
             pstmt = con.prepareStatement(sq);
             rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            Font dateFont = new Font(Font.FontFamily.TIMES_ROMAN, 8);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "All lost school books as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            // Add a table
            float[] columnWidths = {3f,2f,1.2f,2.2f,0.7f,1.8f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);
            
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Student ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Student name", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Date lost", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("lostBookID");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);                               
                
                String bookSubject = rs.getString("lostbookSubject");
                insertCell(issuedBookTable, bookSubject, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentID = rs.getString("studentID");
                insertCell(issuedBookTable, studentID, Element.ALIGN_LEFT, 1, bfBold);
                
                String name = rs.getString("studentName");
                insertCell(issuedBookTable, name, Element.ALIGN_LEFT, 1, bfBold);
                
                String studentClass = rs.getString("studentClass");
                insertCell(issuedBookTable, studentClass, Element.ALIGN_LEFT, 1, bfBold);
                
                String datelost = rs.getString("dateLost");
                insertCell(issuedBookTable, datelost, Element.ALIGN_LEFT, 1, dateFont);
                         
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
   
   
         // method to generate lost books per class////////////////////////////////////////////
   public void createAllSchoolBooks(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
             String sq = "SELECT * FROM book ";
             pstmt = con.prepareStatement(sq);
             rs=pstmt.executeQuery();
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK);
            Font bfBold = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "All school books as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            // Add a table
            float[] columnWidths = {3.3f,1.1f,2.1f,2.8f,1.3f,1f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);            
                      
            insertCell(issuedBookTable, "Book ID", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Class", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Subject", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Title", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Publisher", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Issued?", Element.ALIGN_LEFT, 1, bfBold12);
            
            while (rs.next()) {
                String bookID = rs.getString("book_id");
                insertCell(issuedBookTable, bookID, Element.ALIGN_LEFT, 1, bfBold);                               
                
                String bookClass = rs.getString("book_class");
                insertCell(issuedBookTable, bookClass, Element.ALIGN_LEFT, 1, bfBold);
                
                String subject = rs.getString("book_category");
                insertCell(issuedBookTable, subject, Element.ALIGN_LEFT, 1, bfBold);
                
                String title = rs.getString("book_title");
                insertCell(issuedBookTable, title, Element.ALIGN_LEFT, 1, titleFont);
                
                String publisher = rs.getString("book_publisher");
                insertCell(issuedBookTable, publisher, Element.ALIGN_LEFT, 1, bfBold);
                
                boolean isAvail = rs.getBoolean("book_isAvail");
                String available = null;
                if (isAvail) {
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
    
    
    
    
    
    
}// END OF CLASS 