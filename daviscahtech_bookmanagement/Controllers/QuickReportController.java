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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class QuickReportController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);    
    
    Image bookImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/documents.png").toExternalForm());
    ImageView bookImageView = new ImageView(bookImage);    
    
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
    
    String bookIssuedTable = null;
    String lostBookTable = null;

    @FXML
    private ComboBox tableCategoryComboBox;
    ObservableList<String> tableType = FXCollections.<String>observableArrayList("Books issued","Books lost");
    
    @FXML
    private ComboBox reportClassComboBox;
    private final ObservableList classNameData = FXCollections.observableArrayList();
    
    @FXML
    private ComboBox reportSubjectComboBox;
    private final ObservableList bookSubjectData = FXCollections.observableArrayList();
    
    
    @FXML
    private Text answer1A;
    @FXML
    private Text answer2B;
    @FXML
    private Text answer3C;
    @FXML
    private Text answer4C;
    @FXML
    private Text answer5D;
    @FXML
    private Text answer6E;
    @FXML
    private Text detail7F;
    @FXML
    private Text answer7F;
    @FXML
    private Text detail8G;
    @FXML
    private Text answer8G;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
        
        loadSchoolInfo();
        
        //Handle comboBox details
        tableCategoryComboBox.setItems(tableType);
        updateComboClass();
        updateBookSubjects();
        
        totalSchoolBooks();
        totalIssuedBooks();
        totalUnissuedBooks();
        totalLostBooks();
        totalTeachersBooks();
        totalBooksHeldByTeachers();
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
    private void refreshClassComboBox(ActionEvent event) {
        refreshComboClass();
    }

    @FXML
    private void refreshSubjectComboBox(ActionEvent event) {
        refreshComboSubjects();
    }

    @FXML
    private void showSearchedResultOperation(ActionEvent event) {
        showFilteredResultsFromCombo();
    }

    
    private void updateComboClass(){  
        try { 
            String sql = "SELECT className FROM class";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                classNameData.add( rs.getString("className"));         
            }
            // populate class combo box 
            reportClassComboBox.setItems(classNameData);
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
    
    
    private void updateBookSubjects(){  
        try { 
            String sql = "SELECT bookSubject FROM bookcategory";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                bookSubjectData.add( rs.getString("bookSubject"));
            }
            
            // populate class combo box 
            reportSubjectComboBox.setItems(bookSubjectData);
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
    
    // get total registered school books
    private void totalSchoolBooks(){
    
        try {
             String sql4 = "SELECT COUNT(*) FROM book ";
                pstmt = con.prepareStatement(sql4);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                    answer1A.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer1A.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // get total issued school books
    private void totalIssuedBooks(){
    
        try {
             String sql4 = "SELECT COUNT(*) FROM book WHERE book_isAvail = ?";
                pstmt = con.prepareStatement(sql4);
                pstmt.setBoolean(1, false);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                    answer2B.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer2B.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
    
    
    // get total unissued school books
    private void totalUnissuedBooks(){
    
        try {
             String sql4 = "SELECT COUNT(*) FROM book WHERE book_isAvail = ?";
                pstmt = con.prepareStatement(sql4);
                pstmt.setBoolean(1, true);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                    answer3C.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer3C.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    // get total lost school books
    private void totalLostBooks(){
    
        try {
             String sql4 = "SELECT COUNT(*) FROM lostbooks";
                pstmt = con.prepareStatement(sql4);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                    answer4C.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer4C.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // get total teachers' books
    private void totalTeachersBooks(){
    
        try {
             String sql4 = "SELECT COUNT(*) FROM teachercopy";
                pstmt = con.prepareStatement(sql4);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                    answer5D.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer5D.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    // get total teachers' books
    private void totalBooksHeldByTeachers(){
    
        try {
             String sql4 = "SELECT SUM(quantity_issued) FROM teacherissued";
                pstmt = con.prepareStatement(sql4);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                    answer6E.setText(rs.getInt("SUM(quantity_issued)") + "");
               } else {
                   answer6E.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
    private void showFilteredResultsFromCombo(){
        
        clearTexts();
        String selected = null;
        
        //populate the above field with the exact db table
        if (tableCategoryComboBox.getSelectionModel().isEmpty()) {
            
        } else {            
                selected = tableCategoryComboBox.getSelectionModel().getSelectedItem().toString();            
            if (selected.equals("Books issued")) {
                bookIssuedTable = "issuedbook";
            } else if (selected.equals("Books lost")) {
                lostBookTable = "lostbooks";
            }
        }
        
        //run method for filter query
         if (tableCategoryComboBox.getSelectionModel().isEmpty() || reportClassComboBox.getSelectionModel().isEmpty() ||
                 reportSubjectComboBox.getSelectionModel().isEmpty()) {
            String msg = null;
            
             if (tableCategoryComboBox.getSelectionModel().isEmpty()) {
                 msg = "Select the category of data type to be serached.";
             } else if (reportClassComboBox.getSelectionModel().isEmpty()) {
                 msg = "Select the class/level you wish to get data from.";
             } else if (reportSubjectComboBox.getSelectionModel().isEmpty()) {
                 msg = "Select the subject you wish to get information.";
             }
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Data analysis Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The data analysis and query has failed operation.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        } else {
             
            if (selected.equals("Books issued")) {
                booksIssuedToSpecificClass();
            } else if (selected.equals("Books lost")) {
                booksLostInSpecificClass();
            } 
            
         }
    }
    
    private void booksIssuedToSpecificClass(){
        String classname = reportClassComboBox.getSelectionModel().getSelectedItem().toString();
        String subjectName = reportSubjectComboBox.getSelectionModel().getSelectedItem().toString();
        
        try {
             String sql4 = "SELECT COUNT(*) FROM "+ bookIssuedTable +" WHERE bookCategory = ? AND studentClass = ?";
                pstmt = con.prepareStatement(sql4);
                pstmt.setString(1, subjectName);
                pstmt.setString(2, classname);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                   detail7F.setText("Books issued in " + classname );
                   answer7F.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer7F.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    
    private void booksLostInSpecificClass(){
        String classname = reportClassComboBox.getSelectionModel().getSelectedItem().toString();
        String subjectName = reportSubjectComboBox.getSelectionModel().getSelectedItem().toString();
        try {
             String sql4 = "SELECT COUNT(*) FROM "+ lostBookTable +" WHERE lostbookSubject = ? AND studentClass = ?";
                pstmt = con.prepareStatement(sql4);
                pstmt.setString(1, subjectName);
                pstmt.setString(2, classname);
                rs = pstmt.executeQuery(); 
                              
               if (rs.next()) {
                   detail8G.setText("Books lost in " + classname );
                   answer8G.setText(rs.getInt("COUNT(*)") + "");
               } else {
                   answer8G.setText(0 + "");
               }
               
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    private void clearTexts(){
    
        detail7F.setText(null);
        detail8G.setText(null);
        answer7F.setText(null);
        answer8G.setText(null);
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
      

    @FXML
    private void printQuickReportOperation(ActionEvent event) {
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRMATION");
            alert.setGraphic(bookImageView);
            alert.setHeaderText(null);
            alert.setContentText("Do you want to generate summary report\n");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                // generate pdf
                 fileChooserOpener();
                   fileChooser.setTitle("Save summary report");
                   //single File selection
                   file = fileChooser.showSaveDialog(Stage);                   
                    if (file != null) {                      
                        String path = file.getAbsolutePath();
                        createSummaryReport(path);
                    }
                    Alert errorIssue = new Alert(Alert.AlertType.ERROR);
                    errorIssue.setTitle("Success");
                    errorIssue.setGraphic(pdfImageView);
                    errorIssue.setHeaderText(null);
                    errorIssue.setContentText("You have successfully generated summary report.\n");
                    errorIssue.showAndWait();
            
            }
        
    }
    
    
    
         // method to generate lost books per class////////////////////////////////////////////
   public void createSummaryReport(String filename) {
        
       // create a pdf
       Document document = new Document(PageSize.A4,5,5,20,40);
       
        try {
            
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            
            document.open();
            addMetaData(document);
            
            //school info
            Paragraph intro = new Paragraph(""+schoolName+"\n"
                    + ""+schoolAddress+" "+ schoolRegion+"\n"
                    + "Website: "+ schoolWebsite+"\n"
                    + "Quantitative summary of school books as at "+ LocalDate.now() +"\n\r",bfBold12);
            intro.setAlignment(Element.ALIGN_CENTER);            
            document.add(intro);
            
            // Add a table
            float[] columnWidths = {5f,1.1f};
            PdfPTable issuedBookTable = new PdfPTable(columnWidths);
            issuedBookTable.setWidthPercentage(90f);            
                      
            insertCell(issuedBookTable, "Detail", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(issuedBookTable, "Quantity", Element.ALIGN_LEFT, 1, bfBold12);
            
            insertCell(issuedBookTable, "Total Registered school books", Element.ALIGN_LEFT, 1, normalFont);
            insertCell(issuedBookTable, answer1A.getText(), Element.ALIGN_LEFT, 1, normalFont);
            
            insertCell(issuedBookTable, "Total issued books to students", Element.ALIGN_LEFT, 1, normalFont);
            insertCell(issuedBookTable, answer2B.getText(), Element.ALIGN_LEFT, 1, normalFont);
            
            insertCell(issuedBookTable, "Total books not issued to students", Element.ALIGN_LEFT, 1, normalFont);
            insertCell(issuedBookTable, answer3C.getText(), Element.ALIGN_LEFT, 1, normalFont);
            
            insertCell(issuedBookTable, "Total lost books ( unreturned )", Element.ALIGN_LEFT, 1, normalFont);
            insertCell(issuedBookTable, answer4C.getText(), Element.ALIGN_LEFT, 1, normalFont);
            
            insertCell(issuedBookTable, "Total registered teachers' copies", Element.ALIGN_LEFT, 1, normalFont);
            insertCell(issuedBookTable, answer5D.getText(), Element.ALIGN_LEFT, 1, normalFont);
            
            insertCell(issuedBookTable, "Total books held by teachers", Element.ALIGN_LEFT, 1, normalFont);
            insertCell(issuedBookTable, answer6E.getText(), Element.ALIGN_LEFT, 1, normalFont);
           
            
            document.add(issuedBookTable);
            
            document.close();
            pstmt.close();
            
            
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (SQLException ex) {     
                Logger.getLogger(ReportPrintingController.class.getName()).log(Level.SEVERE, null, ex);
            }     
     
     }  
   
    private  void addMetaData(Document document) {
        document.addTitle( "Book watch" );
        document.addSubject( "Books management software" );
        document.addKeywords( "School, software, Books, Students, Teachers" );
        document.addAuthor( "Daviscah Tech Ltd" );
        document.addCreator( "Daviscah Tech Ltd" );
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
    
    
}// end of class
