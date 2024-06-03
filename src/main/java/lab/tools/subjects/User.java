package lab.tools.subjects;

public class User {

    private String username;
    private String passwordHash;

    public User( String username, String passwordHash) {
        // this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername(){
        return username;
    }
    public String getPasswordHash(){
        return passwordHash;
    }


}


