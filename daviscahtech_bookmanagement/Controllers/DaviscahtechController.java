/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Controllers;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


/**
 * FXML Controller class
 *
 * @author davis
 */
public class DaviscahtechController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    
    //meyhod to call when opening any website page
    public static void openWebPage(String url){
    
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
        }    
    }

    @FXML
    private void loadDaviscahtechWebsite(ActionEvent event) {
        
        openWebPage("http://daviscahtech.com");
    }

    @FXML
    private void loadJavelingraphixWebsite(ActionEvent event) {
        
        openWebPage("http://javelingraphix.com");
    }
    
}
