/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import daviscahtech_bookmanagement.Preferences.LoginPreferences;
import daviscahtech_bookmanagement.dao.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * FXML Controller class
 *
 * @author davis
 */
public class LoggingMasterWindowController implements Initializable {

    //###############################For DB link##########################
    Connection con = null;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement st;
    
    LoginPreferences preferences;
    
    @FXML
    private TextField masterUsername;
    @FXML
    private PasswordField masterPassword;
    @FXML
    private HBox masterLoggingStatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //connect to the database
        con = DatabaseConnection.connectDb();

        preferences = LoginPreferences.getPreferences();
    }    

    @FXML
    private void masterLoggingOperation(ActionEvent event) throws IOException, SQLException {
        
        // get hashed details of login 
        String uName = DigestUtils.sha1Hex(masterUsername.getText().trim());
        String pWord = DigestUtils.sha1Hex(masterPassword.getText().trim());
        
        //Retrieve master details from DB where user level is equal to 0       
        String sql = "SELECT * FROM userinfo WHERE userName=? AND userPassword= ? AND userLevel= ?";
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, masterUsername.getText());
        pstmt.setString(2, pWord);
        pstmt.setBoolean(3, false);

        rs=pstmt.executeQuery();        
                
        if (uName.equals(preferences.getN()) && pWord.equals(preferences.getZ())) {
            closeStage();
             loadWindow("/daviscahtech_bookmanagement/Fxmls/loginSetting.fxml", "Master user panel");
        
        } else if(rs.next()){
            closeStage();
            loadWindow("/daviscahtech_bookmanagement/Fxmls/loginSetting.fxml", "Master user panel");
        }  else {
            masterLoggingStatus.setVisible(true);
        }        
        
        //close connections
        pstmt.close();
        
        
        
        
        
    }
    
    
    //method to close stage after login in
    private void closeStage(){
        ( (Stage)masterUsername.getScene().getWindow() ).close();
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
    
    
    
    
} // END OF CLASS
