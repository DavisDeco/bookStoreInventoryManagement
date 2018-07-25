/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import daviscahtech_bookmanagement.Preferences.LocalServerPreference;
import java.net.URL;
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

/**
 * FXML Controller class
 *
 * @author davis
 */
public class ForgottenPassWordController implements Initializable {
    
     Image secureImage = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/secure.png").toExternalForm());
     ImageView secureSuccess = new ImageView(secureImage);
    
    Image errorFlag = new Image(getClass().getResource("/daviscahtech_bookmanagement/Resources/errorFlag.png").toExternalForm());
    ImageView errorImage = new ImageView(errorFlag);
    
    @FXML
    private TextField serverUsername;
    @FXML
    private PasswordField serverPassword;

 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void saveServerChangedDetails(ActionEvent event) {
        if (serverPassword.getText().isEmpty() || serverUsername.getText().isEmpty()) {
            String msg = null;
            if (serverPassword.getText().isEmpty()) {
                msg = "Server Password can not be empty, fill password";
            } if (serverUsername.getText().isEmpty()) {
                msg = "Server username can not be empty, fill username";
            }
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Setting failed.");
            alert.setGraphic(errorImage);
            alert.setHeaderText(null);
            alert.setContentText("Changing server username and password failed.\n" + msg); 
            alert.showAndWait();
            
        } else {
            LocalServerPreference preference = LocalServerPreference.getPreferences();
        
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Authenthication setting");
            alert.setGraphic(secureSuccess);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to change local server login detals from the current settings?");         
             Optional <ButtonType> obt = alert.showAndWait();

            if (obt.get()== ButtonType.OK) {
            preference.setE(serverUsername.getText());
            preference.setD(serverPassword.getText());

            LocalServerPreference.writePreferences(preference);

            serverPassword.clear();
            serverUsername.clear();

            }
        }
    }

   
    
}// end of class
