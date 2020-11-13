package com.example.printme.ui.model;

public class User {

    private String firstName;
    private String name;
    private String email;
    private Integer nbPicture;

    /**
     * Creates a new User with a default home directory
     * @param firstName of the user
     * @param name of the user
     * @param email of the user
     */

    public User(String firstName , String name, String email){
        this.firstName = firstName;
        this.name = name;
        this.email = email;
    }

    /**
     * Creates a new User with a default home directory
     * @param firstName of the user
     * @param name of the user
     * @param email of the user
     * @param nbPicture rest in the abonment
     */
    public User(String firstName , String name, String email , Integer nbPicture){
        this.firstName = firstName;
        this.name = name;
        this.email = email;
        this.nbPicture = nbPicture;
    }

    /**
     * @return the firstName of the user
     */
    public String getFirstName(){
        return this.firstName;
    }

    /**
     * @return the Name of the user
     */
    public String getName(){
        return this.firstName;
    }

    /**
     * @return the email of the user
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @return the nb of the picture rest to the user
     */
    public Integer getNbPicture() {
        return this.nbPicture;
    }


}
