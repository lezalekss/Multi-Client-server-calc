package main;


public class BCryptHelper {


    private String GetSalt(){
        String salt = org.mindrot.jbcrypt.BCrypt.gensalt(12);
        return salt;
    }

    public String GetHash(String passwordToHash){
        String hashedPassword =null;

        hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(passwordToHash, GetSalt());
        return hashedPassword;

    }

    public boolean CheckHash(String password,String hash){
        return org.mindrot.jbcrypt.BCrypt.checkpw(password,hash);
    }



}