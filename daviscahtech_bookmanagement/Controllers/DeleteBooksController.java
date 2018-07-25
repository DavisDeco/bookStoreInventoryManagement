/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

/**
 * FXML Controller class
 *
 * @author davis
 */
public class DeleteBooksController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);  
    
    Image bookImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/book.png").toExternalForm());
    ImageView bookImageView = new ImageView(bookImage);
        
    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;   

    @FXML
    private ComboBox bookClassesComboBox;
    private final ObservableList bookClassData = FXCollections.observableArrayList();
    @FXML
    private ComboBox bookSubjectComboBox;
    private final ObservableList bookSubjectData = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
    }    

    @FXML
    private void refreshBookClassesOperation(ActionEvent event) {
        refreshComboClass();
    }

    @FXML
    private void deleteBooksOperation(ActionEvent event) {
        
        if (bookClassesComboBox.getSelectionModel().isEmpty() || bookSubjectComboBox.getSelectionModel().isEmpty()) {
            String msg = null;
            
            if (bookClassesComboBox.getSelectionModel().isEmpty() ) {
                msg = "Ensure the books' class/level to be removed is selected.";
            } else if (bookSubjectComboBox.getSelectionModel().isEmpty() ) {
                msg = "Ensure the books' subject to be removed is selected.";
            }
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The books have failed DELETE operation.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {                        
            String thisClass = bookClassesComboBox.getSelectionModel().getSelectedItem().toString();
            String bookSubject = bookSubjectComboBox.getSelectionModel().getSelectedItem().toString();
            try {
                String sql = "SELECT * FROM book WHERE book_class = ? AND book_category = ?";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, thisClass);
                pstmt.setString(2, bookSubject);
                
                rs = pstmt.executeQuery();                
                if (rs.next()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Delete books");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText("PLEASE TAKE NOTE! \n"
                                + "Are you sure of deleting all books in "+thisClass+" of the subject "+  bookSubject +"?\n\n"
                                + "REFRESH BOOK TABLE OR RE-START book watch application for data to be refreshed after deleting.");         
                        Optional <ButtonType> obt = alert.showAndWait();

                        if (obt.get()== ButtonType.OK) {
                            try {
                           String query = "DELETE FROM book WHERE book_class = ? AND book_category = ?";
                           pstmt = con.prepareStatement(query);
                           pstmt.setString(1, thisClass);
                           pstmt.setString(2, bookSubject);
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
                        
                } else {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("OOPS!");
                        alert.setGraphic(bookImageView);
                        alert.setHeaderText(null);
                        alert.setContentText("There are no books registered in "+thisClass+" of subject "+ bookSubject+"\n"); 
                        alert.showAndWait();
                }

            } catch (Exception e) {
            }
        }          
        
    }
    
        @FXML
    private void refreshBookSubjectOperation(ActionEvent event) {
        refreshComboSubjects();
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
            bookSubjectComboBox.setItems(bookSubjectData);
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
    

    // call this from anatha class
    public void refreshComboClass(){    
        bookClassData.clear();
        updateComboClass();
    }

    private void updateComboClass(){  
        try { 
            String sql = "SELECT book_class FROM book";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){                
                bookClassData.add( rs.getString("book_class"));         
            }
            // populate class combo box 
            bookClassesComboBox.setItems(bookClassData);
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



    
    
} // end of class
