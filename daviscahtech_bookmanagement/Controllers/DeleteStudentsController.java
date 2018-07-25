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
public class DeleteStudentsController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);   
    
    Image studentImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/student.png").toExternalForm());
    ImageView studentImageView = new ImageView(studentImage);
    
    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;    

    @FXML
    private ComboBox allClassesComboBox;
    private final ObservableList classNameData = FXCollections.observableArrayList();

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
    private void refreshAllClassesOperation(ActionEvent event) {
        refreshComboClass();
    }

    @FXML
    private void deleteStudentsOperation(ActionEvent event) {        
        
        if (allClassesComboBox.getSelectionModel().isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The students has failed DELETE operation.\n"
                        + "Ensure the class to be removed students is selected.");
            errorIssue.showAndWait(); 
            
        }else {                        
            String thisClass = allClassesComboBox.getSelectionModel().getSelectedItem().toString();
            
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
                        alert.setContentText("PLEASE TAKE NOTE!"
                                + "Are you sure of deleting students from "+thisClass+"?\n\n"
                                + "REFRESH STUDENT TABLE OR RE-START book watch application for data to be refreshed after deleting.");         
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
    
    // call this from anatha class
    private void refreshComboClass(){    
        classNameData.clear();
        updateComboClass();
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
            allClassesComboBox.setItems(classNameData);
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


    
}// end of class
