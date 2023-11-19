package models;

public class User {
    public  String email;
    public  String username;
    public String password;
    public String gender;
    public String phone;
    public String address;
    public float balance;

    public User(){
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(String email, String username, String password,String gender, String phone, String address, float balance) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.balance = balance;
    }
}
