package models;

public class Admin {
    public  String email;
    public  String username;
    public String password;
    public String gender;
    public String phone;
    public String address;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public Admin(String email,
                 String username,
                 String password,
                 String gender,
                 String phone,
                 String address) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }
}





