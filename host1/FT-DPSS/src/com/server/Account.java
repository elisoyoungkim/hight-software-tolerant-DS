package com.server;

public class Account
{
    public String firstName;
    public String lastName;
    public int age;
    public String username;
    public String password;
    public String ipAddress;
    public boolean isSignedIn;
    public String status;

    public Account(String firstName, String lastName, int age, String username, String password, String ipAddress,String status)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.ipAddress = ipAddress;
        this.status=status;
    }

    public void signIn()
    {
        isSignedIn = true;
    }

    public void signOut()
    {
        isSignedIn = false;
    }

    public boolean isSignedIn()
    {
        return isSignedIn;
    }
}
