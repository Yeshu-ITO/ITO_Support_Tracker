package ITO.Application.ItoSupportTracker.controller;


import ITO.Application.ItoSupportTracker.model.AdminTeam;
import ITO.Application.ItoSupportTracker.model.Category;
import ITO.Application.ItoSupportTracker.model.SubCategory;
import ITO.Application.ItoSupportTracker.model.User;
import ITO.Application.ItoSupportTracker.repository.GlobalRepository;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/global")
public class GlobalController {


    @Autowired
    public  GlobalRepository globalRespository;

    @PostMapping("/addAdmin")
    public void addAdmins(@RequestBody AdminTeam adminTeam) throws JAXBException {
        globalRespository.addAdmin(adminTeam);
    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody User user) throws JAXBException {
        globalRespository.addUser(user);
    }

    @PostMapping("/addCategory")
    public void addCategory(@RequestBody Category category) throws JAXBException {
        globalRespository.addCategory(category);
    }

    @PostMapping("/addSubCategory")
    public void addSubCategory(@RequestBody SubCategory subCategory, @RequestParam Long categoryId, @RequestParam Long subCategoryId) throws JAXBException {
        globalRespository.addSubCategory(subCategory,categoryId,subCategoryId);
    }



}
