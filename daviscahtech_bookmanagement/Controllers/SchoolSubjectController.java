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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class SchoolSubjectController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image subjectImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/q.png").toExternalForm());
    ImageView subjectSuccess = new ImageView(subjectImage);

    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;

    @FXML
    private TextField searchSubjectName;
    @FXML
    private Text searchSubjectID;
    @FXML
    private TextField subjectName;
    
    @FXML
    private ListView<String> subjectNameListView;
    ObservableList<String> subjectNameData = FXCollections.observableArrayList();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();
        
        refreshSubjectName();
    }   
    
    //method to clear fields after processes
    private void clearFields(){
        searchSubjectID.setText(null);
        searchSubjectName.clear();
        subjectName.clear();
    }

    @FXML
    private void subject_register(ActionEvent event) {
        
         if (subjectName.getText().isEmpty() ) {             
                 String msg = null; 
                 
                if (subjectName.getText().isEmpty()) {
                     msg = "Subject name is empty, you must enter it";                 
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The Subject has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to register class = "+subjectName.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm class registration");
            alert.setGraphic(subjectSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "INSERT INTO bookcategory (bookSubject) VALUES(?)";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, subjectName.getText());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Subject Name = "+subjectName.getText()+ " has been registered and ready to be allocated to a book.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Registration Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearFields();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not subject class due  to internal error.");
                        error.showAndWait();
                    } 
                    
                    refreshSubjectName();
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in registration of subject");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not register subject due  to internal error. " +e);
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
    private void subject_update(ActionEvent event) {
        
        if (subjectName.getText().isEmpty() || searchSubjectID.getText().isEmpty() ) {             
                 String msg = null; 
                 
                if (subjectName.getText().isEmpty()) {
                     msg = "Subject name is empty, you must enter it";                 
                } else if (searchSubjectID.getText().isEmpty()) {
                    msg = "Enter the subject name to retrieve it first in order to update the subject";
             }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The subject has failed update.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to update subject = "+subjectName.getText();           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm class update");
            alert.setGraphic(subjectSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE bookcategory SET bookSubject=? WHERE id = '"+searchSubjectID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, searchSubjectName.getText());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " Subject Name = "+subjectName.getText()+ " has been updated and ready to be allocated to a book.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Update Information Dialog");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                    clearFields();
                    refreshSubjectName();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not update subject due  to internal error.");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in Update of class");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update subject due  to internal error. " +e);
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
    private void subject_delete(ActionEvent event) {
        
        if (searchSubjectID.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The subject has failed DELETE operation.\n"
                        + "Ensure the subject name has been inserted and retrieve subject information before deleting.");
            errorIssue.showAndWait(); 
            
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete class");
            alert.setGraphic(subjectSuccess);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to delete this subject?");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                try {
               String query = "DELETE FROM bookcategory WHERE id = ? ";
               pstmt = con.prepareStatement(query);
               pstmt.setString(1, searchSubjectID.getText());
               pstmt.executeUpdate();            
               pstmt.close(); 
               
               clearFields();
               refreshSubjectName();
               
               } catch (SQLException ex) {
                   Logger.getLogger(SchoolSubjectController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void searchSubjectNameOperation(ActionEvent event) {
        
         if (searchSubjectName.getText().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Search Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The subject has failed Search operation.\n"
                        + "Ensure the subject name has been inserted.");
            errorIssue.showAndWait(); 
            
        }else {
            try {
                String sql = "SELECT * FROM bookcategory WHERE bookSubject = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, searchSubjectName.getText());

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("id");
                    searchSubjectID.setText(add1); 
                    String add2 = rs.getString("bookSubject");
                    subjectName.setText(add2);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid subject");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or subject does not Existing!");
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
    
    private void refreshSubjectName(){        
        subjectNameData.clear();
        refreshList();         
    }

    private void refreshList() {
        try {
            String sql = "SELECT * FROM bookcategory ";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                      subjectNameData.add(rs.getString("bookSubject"));            
               }
                                
                pstmt.close(); 
        } catch (SQLException ex) {
            Logger.getLogger(SchoolClassController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // show this details on the listview
        subjectNameListView.getItems().setAll(subjectNameData);
    }

    @FXML
    private void subjectNameListViewMouseClicked(MouseEvent event) {
        
            try {
                String name = subjectNameListView.getSelectionModel().getSelectedItem();
                String sql = "SELECT * FROM bookcategory WHERE bookSubject = ?  ";
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, name);

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    String add1 = rs.getString("id");
                    searchSubjectID.setText(add1); 
                    String add2 = rs.getString("bookSubject");
                    subjectName.setText(add2);
                      
                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid subject");
                    alert.setHeaderText(null);
                    alert .setGraphic(errorImage);
                    alert.setContentText("Record not Found or subject does not Existing!");
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
    
}// end of class
