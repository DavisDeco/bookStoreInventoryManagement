/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class LoginSettingController implements Initializable {
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    Image thumbsImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/thumbs.png").toExternalForm());
    ImageView imageSuccess = new ImageView(thumbsImage);  
    
    Image userImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/user.png").toExternalForm());
    ImageView userImageView = new ImageView(userImage); 
    
        //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    
    // Variable to hold user ID from the database
    String userDBID = null;

    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private CheckBox isMasterAdmin;
    @FXML
    private ListView<String> userListView;
    ObservableList<String> userListData = FXCollections.observableArrayList();
    @FXML
    private Button onlyAdminChangesButton;

    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();    
        
        // populate listview
        refreshUserList();
        
        
    }    

    @FXML
    private void registerUserOperation(ActionEvent event) throws SQLException {
        
        
         if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()) {
             
                 String msg = null;
            
                if (loginUsername.getText().isEmpty()) {
                     msg = "Username is empty, you must enter it";                 
                } else if (loginPassword.getText().isEmpty()) {
                    msg = "Password is empty, you must enter it";
                }
             
             
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Registration Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The system user has failed registration.\n"
                        + msg);
            errorIssue.showAndWait(); 
            
        }else {                      
                     //show confirm alert before registering data of issued
                    String ms = " Are you sure you want to register = "+loginUsername.getText()+" as system user ";           
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm system user registration");
                    alert.setGraphic(userImageView);
                    alert.setHeaderText(null);
                    alert.setContentText(ms);

                    Optional<ButtonType> response = alert.showAndWait();            
                    if (response.get()==ButtonType.OK) {
                       try {
                            String sql = "INSERT INTO userinfo (userName,userPassword,userLevel) VALUES(?,?,?)";
                            pstmt= con.prepareStatement(sql);

                            pstmt.setString(1, loginUsername.getText());
                            pstmt.setString(2, DigestUtils.sha1Hex(loginPassword.getText()));
                            
                           
                            if (isMasterAdmin.isSelected()) {
                               pstmt.setBoolean(3, false);
                           } else {
                                pstmt.setBoolean(3, true);
                           }

                            int i = pstmt.executeUpdate(); // load data into the database             
                            if (i>0) {
                                //show alert before registering data of employee
                            String msg = " "+loginUsername.getText()+ " has been registered as system user.";
                            Alert al = new Alert(Alert.AlertType.INFORMATION);
                            al.setTitle("Registration Information Dialog");
                            al.setGraphic(imageSuccess);
                            al.setHeaderText(null);
                            al.setContentText(msg);
                            al.showAndWait();
                            
                             clearUserField();// clear fields.
                             refreshUserList();
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Failed");
                                error.setGraphic(errorImage);
                                error.setContentText("OOPS! Could not register user due  to internal error.");
                                error.showAndWait();
                            } 
                            pstmt.close();

                        } catch (Exception e) {
                            Alert at = new Alert(Alert.AlertType.ERROR);
                            at.setTitle("Error in registration of student");
                            at.setGraphic(errorImage);
                            at.setHeaderText(null);
                            at.setContentText("OOPS! Could not register user due  to internal error. " +e);
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
    private void deleteUserOperation(ActionEvent event) {
        
        if (loginUsername.getText().isEmpty() || userDBID.isEmpty()) {
            
            Alert errorIssue = new Alert(Alert.AlertType.ERROR);
            errorIssue.setTitle("Delete Failed");
            errorIssue.setGraphic(errorImage);
            errorIssue.setHeaderText(null);
            errorIssue.setContentText("The delete operation failed. Select a user you want to remove as system user.");
            errorIssue.showAndWait(); 
            
        }else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete system user");
            alert.setGraphic(userImageView);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure to remove "+ loginUsername.getText() +" from being a system user?");         
            Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
                try {
                    String query = "DELETE FROM userinfo WHERE id = ? AND userName = ?";
                    pstmt = con.prepareStatement(query);
                    pstmt.setString(1, userDBID);
                    pstmt.setString(2, loginUsername.getText());
                    pstmt.executeUpdate();            
                    pstmt.close(); 

                    clearUserField();
                    refreshUserList();
               
               } catch (SQLException ex) {
                   Logger.getLogger(SchoolClassController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void changeAdeminDetailOperation(ActionEvent event) { 

        if (loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty() || userDBID.isEmpty()) {
             
                 String msg = null;
            
                if (loginUsername.getText().isEmpty()) {
                     msg = "Username is empty, you must enter it";                 
                } else if (loginPassword.getText().isEmpty()) {
                    msg = "Password is empty, you must enter it";
                } else if (userDBID.isEmpty()) {
                    msg = "YOU MUST choose Master Admin to update/Change their details";
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
                    String ms = " Are you sure you want to update = "+loginUsername.getText()+"'s details as Master Admin?";           
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Update");
                    alert.setGraphic(userImageView);
                    alert.setHeaderText(null);
                    alert.setContentText(ms);

                    Optional<ButtonType> response = alert.showAndWait();            
                    if (response.get()==ButtonType.OK) {
                       try {
                            String sql = "UPDATE userinfo SET userName=?,userPassword=?,userLevel=? WHERE id = '"+userDBID+"'";
                            pstmt= con.prepareStatement(sql);

                            pstmt.setString(1, loginUsername.getText());
                            pstmt.setString(2, DigestUtils.sha1Hex(loginPassword.getText()));
                            
                           
                            if (isMasterAdmin.isSelected()) {
                               pstmt.setBoolean(3, false);
                           } else {
                                pstmt.setBoolean(3, true);
                           }

                            int i = pstmt.executeUpdate(); // load data into the database             
                            if (i>0) {
                                //show alert before registering data of employee
                            String msg = " "+loginUsername.getText()+ " has been updated as master Admin.";
                            Alert al = new Alert(Alert.AlertType.INFORMATION);
                            al.setTitle("Update Information Dialog");
                            al.setGraphic(imageSuccess);
                            al.setHeaderText(null);
                            al.setContentText(msg);
                            al.showAndWait();
                            
                             clearUserField();// clear fields.
                             refreshUserList();
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
        loginPassword.clear();
        loginUsername.clear();
        isMasterAdmin.setSelected(false);
        onlyAdminChangesButton.setVisible(false);
    }
    
    private void refreshUserList(){        
        userListData.clear();
        refreshList();         
    }

    private void refreshList() {
        try {
            String sql = "SELECT * FROM userinfo ";
            pstmt = con.prepareStatement(sql);
            rs=pstmt.executeQuery();
            while (rs.next()) {
                      userListData.add(rs.getString("userName"));            
               }
                                
                pstmt.close(); 
        } catch (SQLException ex) {
            Logger.getLogger(SchoolClassController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // show this details on the listview
        userListView.getItems().setAll(userListData);
    }    

    @FXML
    private void userListViewMouseClicked(MouseEvent event) {
        
        try {
                    String user = userListView.getSelectionModel().getSelectedItem();
                    String sql = "SELECT * FROM userinfo WHERE userName = ? ";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, user);
                    rs=pstmt.executeQuery();
                    
                    if (rs.next()) {
                        
                        userDBID= rs.getString("id");
                        
                        String add1 = rs.getString("userName");
                        loginUsername.setText(add1); 
                        String add2 = DigestUtils.sha1Hex(rs.getString("userPassword"));
                        loginPassword.setText(add2);                        
                        boolean add4 = rs.getBoolean("userLevel");
                        if (add4) {
                            isMasterAdmin.setSelected(false);
                            onlyAdminChangesButton.setVisible(false);
                        } else {
                            isMasterAdmin.setSelected(true);
                            onlyAdminChangesButton.setVisible(true);
                        }
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid user");
                        alert.setHeaderText(null);
                        alert.setContentText("Record not Found or user does not Existing!");
                        alert.showAndWait(); 
                    }                    
                } catch (SQLException ev) {
                    ev.printStackTrace();
                }         
        
    }

    @FXML
    private void clearLogingFieldsOperation(ActionEvent event) {        
        clearUserField();
    }

    @FXML
    private void openDeleteStudentPanel(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/deleteStudents.fxml", "Delete student operation");
    }

    @FXML
    private void openDeleteBooksPanel(ActionEvent event) throws IOException {
        loadWindow("/daviscahtech_bookmanagement/Fxmls/deleteBooks.fxml", "Delete Text book operation");
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
    

    
}// End of class
