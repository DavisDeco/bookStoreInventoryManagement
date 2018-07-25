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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class PersonalAccountController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage); 
    
    Image userImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/user.png").toExternalForm());
    ImageView userImageView = new ImageView(userImage);     
    

    //For DB link
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    // links to FXML
    
    //variable to hold DBuserID
    String userDBID = null;
    
    
    @FXML
    private TextField currentUsername;
    @FXML
    private PasswordField currentPassword;
    @FXML
    private HBox loginCurrentStatus;
    @FXML
    private VBox newDetailsPanel;
    @FXML
    private TextField newUsername;
    @FXML
    private PasswordField newPassword;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //ensure connection to the database is set
        con = DatabaseConnection.connectDb();
    }    

    @FXML
    private void loginCurrentDetailUserOperation(ActionEvent event) throws SQLException {
        
        String pWord = DigestUtils.sha1Hex(currentPassword.getText().trim());
        
        //Retrieve master details from DB where user level is equal to 1       
        String sql = "SELECT * FROM userinfo WHERE userName=? AND userPassword= ? AND userLevel= ?";
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, currentUsername.getText());
        pstmt.setString(2, pWord);
        pstmt.setBoolean(3, true);

        rs=pstmt.executeQuery(); 
        if (rs.next()) {
            
            userDBID = rs.getString("id");
            
            newDetailsPanel.setVisible(true);
            loginCurrentStatus.setVisible(false);
            
        } else {
            loginCurrentStatus.setVisible(true);
            newDetailsPanel.setVisible(false);
        }
        
        pstmt.close();
        
    }

    @FXML
    private void saveNewUserDetailsOperation(ActionEvent event) {
        
        if (newUsername.getText().isEmpty() || newPassword.getText().isEmpty() || userDBID.isEmpty()) {
             
                 String msg = null;
            
                if (newUsername.getText().isEmpty()) {
                     msg = "New Username is empty, you must enter it";                 
                } else if (newPassword.getText().isEmpty()) {
                    msg = " New Password is empty, you must enter it";
                } else if (userDBID.isEmpty()) {
                    msg = "YOU MUST enter your current login details and continue to update/Change their details";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Update Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("Update of a system user has failed.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {                      
                     //show confirm alert before registering data of issued
                    String ms = " Are you sure you want to update = "+currentUsername.getText()+"'s details as system user?";           
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Update");
                    alert.setGraphic(userImageView);
                    alert.setHeaderText(null);
                    alert.setContentText(ms);

                    Optional<ButtonType> response = alert.showAndWait();            
                    if (response.get()==ButtonType.OK) {
                       try {
                            String sql = "UPDATE userinfo SET userName=?,userPassword=? WHERE id = '"+userDBID+"'";
                            pstmt= con.prepareStatement(sql);

                            pstmt.setString(1, newUsername.getText());
                            pstmt.setString(2, DigestUtils.sha1Hex(newPassword.getText()));
                            
           
                            int i = pstmt.executeUpdate(); // load data into the database             
                            if (i>0) {
                                //show alert before registering data of employee
                            String msg = " "+newUsername.getText()+ " has been updated as master Admin.";
                            Alert al = new Alert(Alert.AlertType.INFORMATION);
                            al.setTitle("Update Information Dialog");
                            al.setGraphic(imageSuccess);
                            al.setHeaderText(null);
                            al.setContentText(msg);
                            al.showAndWait();
                            
                             clearUserField();// clear fields.
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Failed");
                                error.setGraphic(errorImage);
                                error.setContentText("OOPS! Could not Update user due  to internal error.");
                                error.showAndWait();
                            } 
                            pstmt.close();

                        } catch (Exception e) {
                            Alert at = new Alert(Alert.AlertType.ERROR);
                            at.setTitle("Error in updating of student");
                            at.setGraphic(errorImage);
                            at.setHeaderText(null);
                            at.setContentText("OOPS! Could not Update Master Admin due  to internal error. " +e);
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

    private void clearUserField() {
        currentPassword.clear();
        currentUsername.clear();
        newPassword.clear();
        newUsername.clear();
    }
    
}// end of class
