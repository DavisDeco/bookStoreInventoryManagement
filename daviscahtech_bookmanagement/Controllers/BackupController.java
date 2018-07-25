/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class BackupController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image bookImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/book.png").toExternalForm());
    ImageView bookImageView = new ImageView(bookImage);
    
    Image excelImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/excel_1.png").toExternalForm());
    ImageView excelImageView = new ImageView(excelImage);
    
    Image browerImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/browser.png").toExternalForm());
    ImageView browerImageView = new ImageView(browerImage);
    
    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    
                    //variables to hold school info
                String schoolName = null;
                String schoolContact = null;
                String schoolAddress = null;
                String schoolRegion = null;
                String schoolEmail = null;
                String schoolWebsite = null; 
    
    
    
    @FXML
    private HBox generateBackUpStatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //connect to the database
        con = DatabaseConnection.connectDb();        
        
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
    private void backUpAllSystemData(ActionEvent event) {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setGraphic(bookImageView);
        alert.setHeaderText(null);
        alert.setContentText("Do you want to Back up all system data for security reasons?\n"
                + "This may take several minutes depending on data volume, please wait....");         
        Optional <ButtonType> obt = alert.showAndWait();

        if (obt.get()== ButtonType.OK) {
             
              BackUpService task = new BackUpService();
              //run the task on a background thread
              Thread backgroundThread = new Thread(task);
              backgroundThread.setDaemon(true);
              backgroundThread.start();
        }
    }
    
    
    // #########  Subclass for all school books  to run on background ##################
    
    public class BackUpService extends Task<Void>{
        
        //major back up files in excel format
        String books_backup = "books_backup.xlsx";
        String issuedBooks_backup = "issuedBooks_backup.xlsx";
        String lostBooks_backup = "lostBooks_backup.xlsx";
        String teacherCopies_backup = "teacherCopies_backup.xlsx";
        String teacherIssuedBooks_backup = "teacherIssuedBooks_backup.xlsx";
        String teacherReturnedBooks_backup = "teacherReturnedBooks_backup.xlsx";
        String students_backup = "students_backup.xlsx";
        
            
        @Override
        protected Void call() throws Exception {
            
               //call method to back up school books
               backUpAllSchoolBooks();
               //call method to back up all school issued books
               backUpAll_IssuedBooks();
               //call method to back up all school lost books
               backUpAll_lostBooks();
               // call method to back up all school teachers copies
               backUpAll_teacherCopies();
               // call method to back up all book quantities issued to teachers
               backUpAll_teacherIssuedBooks();
               // call method to back up all book quantities returned by teachers
               backUpAll_teacherReturnedBooks();
               // call method to back up school students
               backUpAll_students();
               
               
               // call method to send email and attachment to daviscah tech company
                sendEmail();
                
                
            return null;
        }
        
        @Override
        protected void succeeded(){
            super.succeeded();
            
            Alert bookError = new Alert(Alert.AlertType.ERROR);  
            bookError.setTitle("Success");
            bookError.setHeaderText(null);
            bookError.setGraphic(browerImageView);
            bookError.setContentText("Successfuly Backed up all system data.\n"
                    + "To retrieve this Back up data, Contact our company and we shall send you the back-up data.");
            bookError.showAndWait();
            
            generateBackUpStatus.setVisible(false); 
        }
        
        
        @Override
        protected void failed(){
            super.failed(); 
            Alert bookError = new Alert(Alert.AlertType.ERROR);  
            bookError.setTitle("Error occured");
            bookError.setHeaderText(null);
            bookError.setGraphic(errorImage);
            bookError.setContentText("An error occurred while generating books report.\n");
            bookError.showAndWait();
            
        }        
        
         @Override
        protected void running(){
            super.running();
            generateBackUpStatus.setVisible(true); 
            System.out.println("Background data back up running");
        }
        
        
        //Method to back up all school books
        private void backUpAllSchoolBooks() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM book ";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for books");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for books as at "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Book ID");
                     titles.createCell(1).setCellValue("Boook class");
                     titles.createCell(2).setCellValue("Book category");
                     titles.createCell(3).setCellValue("Book Author");
                     titles.createCell(4).setCellValue("Book Title");
                     titles.createCell(5).setCellValue("Book Publisher");
                     titles.createCell(6).setCellValue("Registration Date");
                     titles.createCell(7).setCellValue("IsAvailable");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.setColumnWidth(0, 256*13);
                     sheet.setColumnWidth(1, 256*25);
                     sheet.setColumnWidth(2, 256*17);
                     sheet.setColumnWidth(3,256*13);
                     sheet.setColumnWidth(4, 256*25);
                     sheet.autoSizeColumn(5);
                     sheet.setColumnWidth(6, 256*23);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("book_id"));
                         row.createCell(1).setCellValue(rs.getString("book_class"));
                         row.createCell(2).setCellValue(rs.getString("book_category"));
                         row.createCell(3).setCellValue(rs.getString("book_author"));
                         row.createCell(4).setCellValue(rs.getString("book_title"));
                         row.createCell(5).setCellValue(rs.getString("book_publisher"));
                         row.createCell(6).setCellValue(rs.getString("book_regdate"));
                         row.createCell(7).setCellValue(rs.getString("book_isAvail"));

                         index++;                        
                     }
                  try ( FileOutputStream fileout = new FileOutputStream(books_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }
        
        
        //Method to back up all issued school books
        private void backUpAll_IssuedBooks() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM issuedbook ";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for issued books");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for issued books as at "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Book ID");
                     titles.createCell(1).setCellValue("Boook Title");
                     titles.createCell(2).setCellValue("Book class");
                     titles.createCell(3).setCellValue("Book Category");
                     titles.createCell(4).setCellValue("Stident ID");
                     titles.createCell(5).setCellValue("Student Name");
                     titles.createCell(6).setCellValue("Book Status");
                     titles.createCell(7).setCellValue("Teacher Issued");
                     titles.createCell(8).setCellValue("Student Class");
                     titles.createCell(9).setCellValue("Date Issued");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.setColumnWidth(0, 256*13);
                     sheet.setColumnWidth(1, 256*25);
                     sheet.setColumnWidth(2, 256*13);
                     sheet.setColumnWidth(3,256*17);
                     sheet.setColumnWidth(4, 256*13);
                     sheet.setColumnWidth(5,256*23);
                     sheet.autoSizeColumn(6);
                     sheet.setColumnWidth(7, 256*20);
                     sheet.autoSizeColumn(8);
                     sheet.setColumnWidth(9, 256*23);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("bookID"));
                         row.createCell(1).setCellValue(rs.getString("bookTitle"));
                         row.createCell(2).setCellValue(rs.getString("bookClass"));
                         row.createCell(3).setCellValue(rs.getString("bookCategory"));
                         row.createCell(4).setCellValue(rs.getString("studentID"));
                         row.createCell(5).setCellValue(rs.getString("studentName"));
                         row.createCell(6).setCellValue(rs.getString("bookStatus"));
                         row.createCell(7).setCellValue(rs.getString("teacherIssued"));
                         row.createCell(8).setCellValue(rs.getString("studentClass"));
                         row.createCell(9).setCellValue(rs.getString("issuedDate"));

                         index++;                        
                     }
                  try ( FileOutputStream fileout = new FileOutputStream(issuedBooks_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }  
        
        
        //Method to back up all lost school books
        private void backUpAll_lostBooks() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM lostbooks ";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for lost books");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for lost books as at "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Book ID");
                     titles.createCell(1).setCellValue("Boook Title");
                     titles.createCell(2).setCellValue("Book class");
                     titles.createCell(3).setCellValue("Book Subject");
                     titles.createCell(4).setCellValue("Stident ID");
                     titles.createCell(5).setCellValue("Student Name");
                     titles.createCell(6).setCellValue("Student Class");
                     titles.createCell(7).setCellValue("Date lost");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.setColumnWidth(0, 256*13);
                     sheet.setColumnWidth(1, 256*25);
                     sheet.autoSizeColumn(2);
                     sheet.setColumnWidth(3,256*17);
                     sheet.setColumnWidth(4, 256*13);
                     sheet.setColumnWidth(5,256*23);
                     sheet.autoSizeColumn(6);
                     sheet.setColumnWidth(7, 256*20);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("lostBookID"));
                         row.createCell(1).setCellValue(rs.getString("lostBookTitle"));
                         row.createCell(2).setCellValue(rs.getString("lostBookClass"));
                         row.createCell(3).setCellValue(rs.getString("lostbookSubject"));
                         row.createCell(4).setCellValue(rs.getString("studentID"));
                         row.createCell(5).setCellValue(rs.getString("studentName"));
                         row.createCell(6).setCellValue(rs.getString("studentClass"));
                         row.createCell(7).setCellValue(rs.getString("dateLost"));

                         index++;                        
                     }
                  try ( FileOutputStream fileout = new FileOutputStream(lostBooks_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }  
        
        
        //Method to back up all lost school books
        private void backUpAll_teacherCopies() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM teachercopy ";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for teacher copies");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for teacher copies as at "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Book ID");
                     titles.createCell(1).setCellValue("Boook subject");
                     titles.createCell(2).setCellValue("Book title");
                     titles.createCell(3).setCellValue("Book class");
                     titles.createCell(4).setCellValue("Book publisher");
                     titles.createCell(5).setCellValue("Book Available");
                     titles.createCell(6).setCellValue("Teacher Issued");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.setColumnWidth(0, 256*13);
                     sheet.setColumnWidth(1, 256*17);
                     sheet.setColumnWidth(2, 256*25);
                     sheet.autoSizeColumn(3);
                     sheet.setColumnWidth(4, 256*17);
                     sheet.autoSizeColumn(5);
                     sheet.setColumnWidth(6, 256*23);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("book_id"));
                         row.createCell(1).setCellValue(rs.getString("book_subject"));
                         row.createCell(2).setCellValue(rs.getString("book_title"));
                         row.createCell(3).setCellValue(rs.getString("book_class"));
                         row.createCell(4).setCellValue(rs.getString("book_publisher"));
                         row.createCell(5).setCellValue(rs.getString("book_isAvail"));
                         row.createCell(6).setCellValue(rs.getString("teacher_issued"));

                         index++;                        
                     }
                  try ( FileOutputStream fileout = new FileOutputStream(teacherCopies_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }          
        
       
        //Method to back up all lost school books
        private void backUpAll_teacherIssuedBooks() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM teacherissued ";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for book quantity issued to teachers");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for books quantity issued to teachers "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Teacher ID");
                     titles.createCell(1).setCellValue("Teacher Name");
                     titles.createCell(2).setCellValue("Department");
                     titles.createCell(3).setCellValue("Book Subject");
                     titles.createCell(4).setCellValue("Book Title");
                     titles.createCell(5).setCellValue("Book Class");
                     titles.createCell(6).setCellValue("Book publisher");
                     titles.createCell(7).setCellValue("Quantity issued");
                     titles.createCell(8).setCellValue("Date issued");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.autoSizeColumn(0);
                     sheet.setColumnWidth(1, 256*17);
                     sheet.setColumnWidth(2, 256*17);
                     sheet.autoSizeColumn(3);
                     sheet.setColumnWidth(4, 256*17);
                     sheet.autoSizeColumn(5);
                     sheet.setColumnWidth(6, 256*17);
                     sheet.autoSizeColumn(7);
                     sheet.setColumnWidth(8, 256*17);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("teacher_ID"));
                         row.createCell(1).setCellValue(rs.getString("teacher_name"));
                         row.createCell(2).setCellValue(rs.getString("teacher_depart"));
                         row.createCell(3).setCellValue(rs.getString("book_subject"));
                         row.createCell(4).setCellValue(rs.getString("book_title"));
                         row.createCell(5).setCellValue(rs.getString("book_class"));
                         row.createCell(6).setCellValue(rs.getString("book_publisher"));
                         row.createCell(6).setCellValue(rs.getString("quantity_issued"));
                         row.createCell(6).setCellValue(rs.getString("date_issued"));

                         index++;                        
                     }
                  try ( FileOutputStream fileout = new FileOutputStream(teacherIssuedBooks_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }   
        
        
        
        //Method to back up all lost school books
        private void backUpAll_teacherReturnedBooks() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM teacherreturns";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for book quantity returned by teachers");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for books quantity returned by teachers "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Teacher ID");
                     titles.createCell(1).setCellValue("Teacher Name");
                     titles.createCell(2).setCellValue("Department");
                     titles.createCell(3).setCellValue("Book Subject");
                     titles.createCell(4).setCellValue("Book Title");
                     titles.createCell(5).setCellValue("Book Class");
                     titles.createCell(6).setCellValue("Book publisher");
                     titles.createCell(7).setCellValue("Quantity returned");
                     titles.createCell(8).setCellValue("Date returneed");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.autoSizeColumn(0);
                     sheet.setColumnWidth(1, 256*17);
                     sheet.setColumnWidth(2, 256*17);
                     sheet.autoSizeColumn(3);
                     sheet.setColumnWidth(4, 256*17);
                     sheet.autoSizeColumn(5);
                     sheet.setColumnWidth(6, 256*17);
                     sheet.autoSizeColumn(7);
                     sheet.setColumnWidth(8, 256*17);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("teacher_ID"));
                         row.createCell(1).setCellValue(rs.getString("teacher_name"));
                         row.createCell(2).setCellValue(rs.getString("teacher_depart"));
                         row.createCell(3).setCellValue(rs.getString("book_subject"));
                         row.createCell(4).setCellValue(rs.getString("book_title"));
                         row.createCell(5).setCellValue(rs.getString("book_class"));
                         row.createCell(6).setCellValue(rs.getString("book_publisher"));
                         row.createCell(6).setCellValue(rs.getString("quantity_returned"));
                         row.createCell(6).setCellValue(rs.getString("date_returned"));

                         index++;                        
                     }
                   
                  try ( FileOutputStream fileout = new FileOutputStream(teacherReturnedBooks_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }    
        
        
        //Method to back up all lost school books
        private void backUpAll_students() throws SQLException{
        
               try { 
                     String sql = "SELECT * FROM students";
                     pstmt = con.prepareStatement(sql);
                     rs=pstmt.executeQuery();

                     //create excel document
                     XSSFWorkbook wb = new XSSFWorkbook();
                     XSSFSheet sheet = wb.createSheet("Back up for students");

                     XSSFRow header = sheet.createRow(0); 
                     header.createCell(0).setCellValue(schoolName);
                     
                     XSSFRow header1 = sheet.createRow(1);
                     header1.createCell(0).setCellValue(schoolAddress+" "+ schoolRegion);
                     
                     XSSFRow header2 = sheet.createRow(2);
                     header2.createCell(0).setCellValue("Website: "+ schoolWebsite);
                     
                     XSSFRow header3 = sheet.createRow(3);
                     header3.createCell(0).setCellValue("Back up for students "+ LocalDate.now());

                     XSSFRow titles = sheet.createRow(4);
                     titles.createCell(0).setCellValue("Student ID");
                     titles.createCell(1).setCellValue("Student Name");
                     titles.createCell(2).setCellValue("Student Class");
                     titles.createCell(3).setCellValue("Parent Email");
                     titles.createCell(4).setCellValue("Registered");
                     titles.createCell(5).setCellValue("Enrolled");

                     sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
                     sheet.autoSizeColumn(0);
                     sheet.setColumnWidth(1, 256*25);
                     sheet.autoSizeColumn(2);
                     sheet.setColumnWidth(3, 256*25);
                     sheet.autoSizeColumn(4);
                     sheet.autoSizeColumn(5);

                     sheet.setZoom(100);

                     int index = 5;            

                     while(rs.next()){                

                         XSSFRow row = sheet.createRow(index);
                         row.createCell(0).setCellValue(rs.getString("student_id"));
                         row.createCell(1).setCellValue(rs.getString("student_name"));
                         row.createCell(2).setCellValue(rs.getString("student_class"));
                         row.createCell(3).setCellValue(rs.getString("parentEmail"));
                         row.createCell(4).setCellValue(rs.getString("student_regdate"));
                         row.createCell(5).setCellValue(rs.getString("student_isAvail"));

                         index++;                        
                     }
                   
                  try ( FileOutputStream fileout = new FileOutputStream(students_backup)) {
                      wb.write(fileout);
                  }
                     pstmt.close();
                         
                 } catch (SQLException | IOException ex) {                        
                 } finally {   
                     pstmt.close();          
                 }
            
        }         
        
        
        // Method to send all email for back up
        private void sendEmail(){
        
            final String username = "dnyandiri@gmail.com";
            final String password = "liver2pool";
            
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
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dnyandiri@gmail.com"));
                message.setSubject("Book Watch Back up data");
                message.setSentDate(new java.util.Date());
                message.setText("This is back up data for book watch software sent from a client as at "+ LocalDate.now()
                        +" Reply to this client email: "+ schoolEmail);
                
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                Multipart multipart = new MimeMultipart();
                
                //attachment 1
                String filename = "Books_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(books_backup);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 1
                
                //attachment 2
                String filename2 = "issuedBooks_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source2 = new FileDataSource(issuedBooks_backup);
                messageBodyPart.setDataHandler(new DataHandler(source2));
                messageBodyPart.setFileName(filename2);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 2
                
                
                //attachment 3
                String filename3 = "lostBooks_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source3 = new FileDataSource(lostBooks_backup);
                messageBodyPart.setDataHandler(new DataHandler(source3));
                messageBodyPart.setFileName(filename3);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 3   
                
                //attachment 4
                String filename4 = "teacherCopies_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source4 = new FileDataSource(teacherCopies_backup);
                messageBodyPart.setDataHandler(new DataHandler(source4));
                messageBodyPart.setFileName(filename4);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 4  
                
                //attachment 5
                String filename5 = "teacherIssuedBooks_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source5 = new FileDataSource(teacherIssuedBooks_backup);
                messageBodyPart.setDataHandler(new DataHandler(source5));
                messageBodyPart.setFileName(filename5);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 5        
                
                
                //attachment 6
                String filename6 = "teacherReturnedBooks_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source6 = new FileDataSource(teacherReturnedBooks_backup);
                messageBodyPart.setDataHandler(new DataHandler(source6));
                messageBodyPart.setFileName(filename6);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 6  
                
                //attachment 7
                String filename7 = "students_"+schoolName;
                messageBodyPart = new MimeBodyPart();
                DataSource source7 = new FileDataSource(students_backup);
                messageBodyPart.setDataHandler(new DataHandler(source7));
                messageBodyPart.setFileName(filename7);
                multipart.addBodyPart(messageBodyPart);
                // end attachement 7
                
                
                //populate message with attachments                
                message.setContent(multipart);
                
                //send full message
                System.out.println("Your email is being sent...");
                Transport.send(message);
                System.out.println("Your email was successful sent");
                
                                
            } catch (Exception e) {
            }
        }
        
    } // end of class
    
    // #########  Subclass for all school books  to run on background ##################
    
    
    
    
} // End of Class
