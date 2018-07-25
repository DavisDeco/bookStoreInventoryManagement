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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class SchoolInfoController implements Initializable {    
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);
    
    Image schoolImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/School.png").toExternalForm());
    ImageView schoolSuccess = new ImageView(schoolImage);
    
    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    @FXML
    private TextField schoolName;
    @FXML
    private TextField schoolAddress;
    @FXML
    private TextField schoolRegion;
    @FXML
    private TextField schoolContact;
    @FXML
    private TextField schoolEmail;
    @FXML
    private TextField schoolWebsite;
    @FXML
    private Text displaySchoolID;

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
    private void loadInitialValueOperation(ActionEvent event) {
       
        try {
                String sql = "SELECT * FROM schoolInfo ";
                pstmt = con.prepareStatement(sql);

                rs=pstmt.executeQuery();
                if (rs.next()) {
                    
                    String add = rs.getString("id");
                    displaySchoolID.setText(add);
                    String add1 = rs.getString("name");
                    schoolName.setText(add1); 
                    String add2 = rs.getString("address");
                    schoolAddress.setText(add2);
                    String add3 = rs.getString("region");
                    schoolRegion.setText(add3);
                    String add4 = rs.getString("contact");
                    schoolContact.setText(add4);
                    String add5 = rs.getString("email");
                    schoolEmail.setText(add5);
                    String add6 = rs.getString("website");
                    schoolWebsite.setText(add6);
                      
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
    private void updateSchoolChanges(ActionEvent event) {
 
      if (schoolAddress.getText().isEmpty() || schoolContact.getText().isEmpty()|| schoolEmail.getText().isEmpty()
              || schoolName.getText().isEmpty()|| schoolRegion.getText().isEmpty()|| schoolWebsite.getText().isEmpty()) {
             
                 String msg = null;
            
                if (schoolAddress.getText().isEmpty()) {
                     msg = "School address is empty.";                 
                } else if (schoolContact.getText().isEmpty()) {
                    msg = "School contact is empty";
                } else if (schoolEmail.getText().isEmpty()) {
                    msg = "School email is empty";
                } else if (schoolName.getText().isEmpty()) {
                    msg = "School name is empty";
                } else if (schoolRegion.getText().isEmpty()) {
                    msg = "School region is empty";
                } else if (schoolWebsite.getText().isEmpty()) {
                    msg = "School website is empty";
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("School Information update failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to update school information? ";           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm update");
            alert.setGraphic(schoolSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "UPDATE schoolInfo SET name=?,address=?,region=?,contact=?,"
                            + "email=?,website=? WHERE id = '"+displaySchoolID.getText()+"'";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, schoolName.getText());
                    pstmt.setString(2, schoolAddress.getText());
                    pstmt.setString(3, schoolRegion.getText());
                    pstmt.setString(4, schoolContact.getText());
                    pstmt.setString(5, schoolEmail.getText());
                    pstmt.setString(6, schoolWebsite.getText());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " School information have been update.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Confirmation");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                     clearfields();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setHeaderText(null);
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not school information due  to internal error.\n");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in updating of book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not update school information due  to internal error. " +e);
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
    
    private void clearfields(){
    
        schoolAddress.clear();
        schoolContact.clear();
        schoolEmail.clear();
        schoolName.clear();
        schoolRegion.clear();
        schoolWebsite.clear();
        displaySchoolID.setText(null);
    }

    @FXML
    private void clearSchoolFields(ActionEvent event) {
        
        clearfields();
    }

    @FXML
    private void registerSchoolInfoOperation(ActionEvent event) {
        
      if (schoolAddress.getText().isEmpty() || schoolContact.getText().isEmpty()|| schoolEmail.getText().isEmpty()
              || schoolName.getText().isEmpty()|| schoolRegion.getText().isEmpty()|| schoolWebsite.getText().isEmpty()) {
             
                 String msg = null;
            
                if (schoolAddress.getText().isEmpty()) {
                     msg = "School address is empty.";                 
                } else if (schoolContact.getText().isEmpty()) {
                    msg = "School contact is empty";
                } else if (schoolEmail.getText().isEmpty()) {
                    msg = "School email is empty";
                } else if (schoolName.getText().isEmpty()) {
                    msg = "School name is empty";
                } else if (schoolRegion.getText().isEmpty()) {
                    msg = "School region is empty";
                } else if (schoolWebsite.getText().isEmpty()) {
                    msg = "School website is empty";
                } 
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("School Information registration failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
            
        }else {              
             //show confirm alert before registering data of issued
            String ms = " Are you sure you want to register school information? ";           
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm update");
            alert.setGraphic(schoolSuccess);
            alert.setHeaderText(null);
            alert.setContentText(ms);
           
            Optional<ButtonType> response = alert.showAndWait();            
            if (response.get()==ButtonType.OK) {
               try {
                    String sql = "INSERT INTO schoolInfo (name,address,region,contact,"
                            + "email,website) values (?,?,?,?,?,?)";
                    pstmt= con.prepareStatement(sql);

                    pstmt.setString(1, schoolName.getText());
                    pstmt.setString(2, schoolAddress.getText());
                    pstmt.setString(3, schoolRegion.getText());
                    pstmt.setString(4, schoolContact.getText());
                    pstmt.setString(5, schoolEmail.getText());
                    pstmt.setString(6, schoolWebsite.getText());

                    int i = pstmt.executeUpdate(); // load data into the database             
                    if (i>0) {
                        //show alert before registering data of employee
                    String msg = " School information have been registered.";
                    Alert al = new Alert(Alert.AlertType.INFORMATION);
                    al.setTitle("Confirmation");
                    al.setGraphic(imageSuccess);
                    al.setHeaderText(null);
                    al.setContentText(msg);
                    al.showAndWait();
                    
                     clearfields();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Failed");
                        error.setHeaderText(null);
                        error.setGraphic(errorImage);
                        error.setContentText("OOPS! Could not register school information due  to internal error.\n");
                        error.showAndWait();
                    } 
                    pstmt.close();
                    
                } catch (Exception e) {
                    Alert at = new Alert(Alert.AlertType.ERROR);
                    at.setTitle("Error in updating of book");
                    at.setGraphic(errorImage);
                    at.setHeaderText(null);
                    at.setContentText("OOPS! Could not register school information due  to internal error. " +e);
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
    
    
    
    
    
    
    
}// end of class
