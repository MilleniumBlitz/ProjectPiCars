package fr.millenium_blitz.projectpicars.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class IPAddressValidator{
	
    private static Pattern pattern = Pattern.compile(
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip){
        Matcher matcher = pattern.matcher(ip);
	  return matcher.matches();	    	    
    }
}
