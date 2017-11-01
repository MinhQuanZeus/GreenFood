package greenlife.com.vn.greenfood.utils;

/**
 * Created by thean on 11/1/2017.
 */

public class ValidateInput{

        private static ValidateInput validateInput;

        public static ValidateInput getInstance(){
            if(validateInput == null){
                validateInput = new ValidateInput();
            }
            return validateInput;
        }

        public static String checkStringLength(String inputString, String message){
            return inputString.length() == 0 ? message : "";
        }
}
