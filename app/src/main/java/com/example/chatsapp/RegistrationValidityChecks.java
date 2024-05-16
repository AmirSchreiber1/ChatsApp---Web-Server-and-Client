package com.example.chatsapp;
import java.util.regex.Pattern;

public class RegistrationValidityChecks {

    static String passwordChanged(String currentPassword) {
        // Creating 'flag' variables to indicate whether we've found in the password the required characters:
        boolean isLongEnough = false, hasDigit = false, hasLetter = false, hasSpecialCharacter = false;

        // Checking that the length of the password is long enough:
        if (currentPassword.length() >= 6) {
            isLongEnough = true;
        } else {
            isLongEnough = false;
        }

        // Checking whether the password contains at least one digit (using Reg-Ex!):
        hasDigit = Pattern.compile("\\d").matcher(currentPassword).find();


        // Now, checking whether the password includes at least one special character (using Reg-Ex!):
        hasSpecialCharacter = currentPassword.matches(".*[!@#$*].*");

        // Finally, checking whether the password includes at least one letter (using Reg-Ex!):
        hasLetter = Pattern.compile("[a-zA-Z]").matcher(currentPassword).find();

        // Now, resets the total strength of the password (because it has changed, so the strength of the previous password is no longer relevant):
        String currPassError = "";


        // Adding 25 'points' for every criteria the given password meets:
        if (!isLongEnough) {
            currPassError = "Password must be at least 6 characters long. ";
        }

        if (!hasDigit) {
            currPassError += "Password must include at least one digit. ";
        }

        if (!hasLetter) {
            currPassError += "Password must include at least one letter. ";
        }

        if (!hasSpecialCharacter) {
            currPassError += "Password must include at least one special character (!@#$*). ";
        }

        return currPassError;
    }

    static String usernameChanged(String currentUsername) {
        // Creating a variable to present the error message, if needed to:
        String currUsernameError = "";

        // Checking that the length of the password is long enough:
        if (currentUsername.length() < 6) {
            currUsernameError = "Username must be at least 6 characters long. ";
        }

        return currUsernameError;
    }

    static String confirmPasswordChanged(String currentPassword, String currentConfirmPassword) {
        // Creating a variable to present the error message, if needed to:
        String currConfirmPasswordError = "";

        // Checking for a match with the password (with the '===' operator, since we want equality in both value & type):
        if (currentPassword.equals(currentConfirmPassword)) {
            if (currentPassword.equals("")) {
                currConfirmPasswordError = "You can't choose an empty password!";
            }
        } else {
            currConfirmPasswordError = "Password & Confirm-Password don't match!";
        }

        return currConfirmPasswordError;
    }

    static String displayNameChanged(String currentDisplayName) {
        // Creating a variable to present the error message, if needed to:
        String currDisplayNameError = "";

        // Creating 'flag' variables to indicate whether the display name meets the required criteria:
        // First, checking whether the display name contains only letters (using Reg-Ex!):
        boolean containsOnlyLetters = currentDisplayName.matches("[A-Za-z]+");

        if (!containsOnlyLetters) {
            currDisplayNameError = "Display name must contain only letters. ";
        }


        // Now, checking whether the length of the display name is long enough:
        if (currentDisplayName.length() < 6) {
            currDisplayNameError += "Display name must be at least 6 characters long.";
        }

        return currDisplayNameError;
    }







}
