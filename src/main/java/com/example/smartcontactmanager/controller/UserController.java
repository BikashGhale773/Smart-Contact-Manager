package com.example.smartcontactmanager.controller;

import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;
import com.example.smartcontactmanager.helper.Message;
import com.example.smartcontactmanager.repository.ContactRepository;
import com.example.smartcontactmanager.repository.UserRepositroy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    //to get the user form database
    private UserRepositroy userRepositroy;

    @ModelAttribute
    //to add user in all the handler
    //method for adding common data to response
    public void addCommonData(Model model, Principal principal){
        //get logged in email and stored in userName
        String userName =  principal.getName();
        System.out.println("userName: " + userName);

        //get the user using userName(email);
        User user = userRepositroy.getUserByEmail(userName);
        System.out.println("User " + user);

        model.addAttribute("user", user);
    }


    //home dashboard
    @RequestMapping("/index")
    //Model model to send data to the view
    //we can get id or username with the help of principal
    public String dashboard(Model model, Principal principal){
        model.addAttribute("title", "User Dashboard");

        return "normal/user_dashboard";
    }


    //open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){

        model.addAttribute("title", "Add Contact");

        //adding blank Contact object
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }




    //copy
    @PostMapping("/process_contact")
    //principal le form ko name ma ra contact entity ko name same vayesi Contact contact ma automatic aauxa data
    public String processContact(@ModelAttribute("contact") Contact contact,
                                 @RequestParam("imageProfile") MultipartFile file,
                                 Principal principal, HttpSession session){


        try {
            //getting data of the logged in user(the one who is filling contact form)
            String name = principal.getName();
            //the one who is adding contact will get here
            User user = this.userRepositroy.getUserByEmail(name);

            //contact ma user pass gareko
            contact.setUser(user);

            //processing and uploading file
            if(file.isEmpty()){
                //if the file  is empty then try our message
                System.out.println("===File is empty====");

                contact.setImage("contact.png");

            }else{
                //upload image file to folder and update the name to contact
                //getting file name
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsoluteFile()+File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("======Image is uploaded======");
            }

            //user bata contact nikaleko
            //adding new contact to the list of contacts of that user
            user.getContacts().add(contact);



            //users contact will be updated
            this.userRepositroy.save(user);

            System.out.println("Data " + contact);

            //Contact successfully added Message
            session.setAttribute("message", new Message("Contact added Successfully","success"));


        }catch(Exception e){
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();

            //Contact addition error Message
            session.setAttribute("message",new Message("Something went wrong","danger"));
        }

        return "normal/add_contact_form";

    }

    //show contacts handler
    //per page = 5 User
    //current page = 0 [replace 0 with page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal){

        model.addAttribute("title","Show Contact");

        //send the list of contacts
        //get the logged in user
        //This is one way to get contact to pass in view contacts
       /* String userName = principal.getName();

        //get user from database
        User user = this.userRepositroy.getUserByEmail(userName);

        List<Contact> contacts = user.getContacts();
        */

        //userName ma form user ko email aauxa
        String userName = principal.getName();
        //email bata User fetch gareko
        User user =  this.userRepositroy.getUserByEmail(userName);

        //has two page== current page and Contact per page -5
        Pageable pageable = PageRequest.of(page, 4);
        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);


        //id ko help le contact fetch gareko
        //List<Contact> contacts = this.contactRepository.findContactByUser(user.getId());

        //all the list of contact in contacts
        model.addAttribute("contacts", contacts);
        //current page
        model.addAttribute("currentPage", page);
        //total pages after breajing into pages by 5 contacts
        model.addAttribute("totalPage", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    //showing contact detail by clicking email
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal){

        System.out.println("cId: " + cId);

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        System.out.println("=====" + contact);

        //security bug one user cannot see another user contact
        String userName = principal.getName();
        User user = this.userRepositroy.getUserByEmail(userName);

        if(user.getId()==contact.getUser().getId())
            model.addAttribute("contact", contact);

        return "normal/contact_detail";
    }

    //delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession session, Principal principal){
        Contact contact = this.contactRepository.findById(cId).get();
        //making user unlink with contact
        //delete old photo (user ma vako orphanRemoval true rakhesi yo line hatauda hunxa)
        //contact.setUser(null);

        //getting current user
        User user = this.userRepositroy.getUserByEmail(principal.getName());

        //to remove two object should match
        user.getContacts().remove(contact);
        this.userRepositroy.save(user);

        //check...
        this.contactRepository.delete(contact);
        session.setAttribute("message", new Message("Contact deleted Successfully!!!", "success"));

        return "redirect:/user/show-contacts/0";
    }

    //open update form handler
    @PostMapping("/update-contact/{cId}")
    public String updataForm(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title","Update Contact");

        Contact contact = this.contactRepository.findById(cId).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    //update contact handler
    @PostMapping("/process_update")
    public String updatedForm(@ModelAttribute("contact") Contact contact, @RequestParam("imageProfile") MultipartFile file,
                              Model model, Principal principal, HttpSession session){
        try{
            //old contact details
            Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();

            //image
            if(!file.isEmpty()){
                //delete old image
                File deleteFile = new ClassPathResource("static/image").getFile();
                File file1 = new File(deleteFile, oldContactDetails.getImage());
                file1.delete();

                //upload new image
                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsoluteFile()+File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());
            }else{
                contact.setImage(oldContactDetails.getImage());
            }

            User user = this.userRepositroy.getUserByEmail(principal.getName());
            contact.setUser(user);

            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Your contact is Updated...!!!", "success"));


        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Contact Name======================= " +contact.getName());
        return "redirect:/user/" +contact.getcId() + "/contact";
    }

}
