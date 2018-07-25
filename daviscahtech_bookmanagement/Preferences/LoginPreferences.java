/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daviscahtech_bookmanagement.Preferences;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author davis
 */
public class LoginPreferences {
    
    public static final String LOGIN_CONFIG_FILE = "./Config/log.txt";
    
    
    
    private String n;
    private String z;
    private String userEmail;
    private String bc;

    public LoginPreferences() {
        
        setN("admin");
        userEmail = "info@daviscahtech.com";
        setBc("Nyandiri2017");
        setZ("daviscahtech2017%bookwatch");
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        if (n.length() < 35) {
            this.n = DigestUtils.sha1Hex(n);
        } else {
            this.n = n;
        }

        
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        if (z.length() < 35) {
            this.z = DigestUtils.sha1Hex(z);
        } else {
            this.z = z;
        }
        
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getBc() {
        return bc;
    }

    public void setBc(String bc) {
         if (bc.length() < 35) {
            this.bc = DigestUtils.sha1Hex(bc);
        } else {
            this.bc = bc;
        }
    }
    
    
     // method to store preferences into json file
    public static  void  initConfig(){
    
        Writer writer = null;
        try {  
            LoginPreferences preferences = new LoginPreferences();
            Gson gson = new Gson();
            writer = new FileWriter(LOGIN_CONFIG_FILE);
            gson.toJson(preferences,writer);
        } catch (IOException ex) {
            Logger.getLogger(LoginPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }finally{        
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(LoginPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //method to get preferences from the json file
    public static LoginPreferences getPreferences(){
        
        Gson gson = new Gson();
        LoginPreferences preferences = new LoginPreferences();
        
        try {
            preferences = gson.fromJson(new FileReader(LOGIN_CONFIG_FILE), LoginPreferences.class);
        } catch (FileNotFoundException ex) {
            initConfig();
            Logger.getLogger(LoginPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return preferences;
    }
    
    //method to update preferences to the system json file
    
    public static void writePreferences(LoginPreferences preferences){
        
         Writer writer = null;
        try { 
            Gson gson = new Gson();
            writer = new FileWriter(LOGIN_CONFIG_FILE);
            gson.toJson(preferences,writer);
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);  
            successAlert.setTitle("SUCCESS");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Setting changes were successful");
            successAlert.showAndWait();
            
        } catch (IOException ex) {
            Alert bookError = new Alert(Alert.AlertType.ERROR);  
            bookError.setTitle("FAILED");
            bookError.setHeaderText(null);
            bookError.setContentText("Changes update failed!\nDefault values will still be used");
            bookError.showAndWait();
        }finally{        
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(LoginPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
        
    } 
    
    
    
    
}//end of class
