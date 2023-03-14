package com.example.smartcontactmanager.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
public class User {
    //encapsulation: cannot access directly by other classes

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name must be filled")
    @Size(min = 2,max = 20, message = "minimum 2 and maximum 20 character are allowed")
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private boolean enabled;
    private String imageUrl;
    @Column(length = 500)
    private String about;

    //creates blank contacts with ArrayList
    //one user has many contacts
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();

    //default constructor
    public User(){

    }

    public User(String name, String email, String password, String role, boolean enabled, String imageUrl, String about) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.imageUrl = imageUrl;
        this.about = about;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", imageUrl='" + imageUrl + '\'' +
                ", about='" + about + '\'' +
                ", contacts=" + contacts +
                '}';
    }
}


//copy
//    @PostMapping("/process_contact")
//    //principal le form ko name ma ra contact entity ko name same vayesi Contact contact ma automatic aauxa data
//    public String processContact(@ModelAttribute("contact") Contact contact,
//                                 @RequestParam("imageProfile") MultipartFile file,
//                                 Principal principal){
//
//
//        try {
//            //getting data of the logged in user(the one who is filling contact form)
//            String name = principal.getName();
//            //the one who is adding contact will get here
//            User user = this.userRepositroy.getUserByEmail(name);
//
//            //contact ma user pass gareko
//            contact.setUser(user);
//
//            //processing and uploading file
//            if(file.isEmpty()){
//                //if the file  is empty then try our message
//                System.out.println("===FIle is empty====");
//
//            }else{
//                //upload image file to folder and update the name to contact
//                //getting file name
//                contact.setImage(file.getOriginalFilename());
//
//                File saveFile = new ClassPathResource("static/image").getFile();
//
//                Path path = Paths.get(saveFile.getAbsoluteFile()+File.separator + file.getOriginalFilename());
//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//                System.out.println("======Image is uploaded======");
//            }
//
//            //user bata contact nikaleko
//            //adding new contact to the list of contacts of that user
//            user.getContacts().add(contact);
//
//
//
//            //users contact will be updated
//            this.userRepositroy.save(user);
//
//            System.out.println("Data " + contact);
//
//        }catch(Exception e){
//            System.out.println("Error " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return "normal/add_contact_form";
//
//    }
