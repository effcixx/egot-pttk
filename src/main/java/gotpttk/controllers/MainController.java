package gotpttk.controllers;

import gotpttk.entities.Admin;
import gotpttk.entities.Country;
import gotpttk.entities.Region;
import gotpttk.service.AdminService;
import gotpttk.service.CountryService;
import gotpttk.service.RangeService;
import gotpttk.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
public class MainController {
    private CountryService countryService;
    private RangeService rangeService;
    private RegionService regionService;
    private AdminService adminService;

    @Autowired
    public MainController(CountryService countryService, RangeService rangeService, RegionService regionService
                            , AdminService adminService) {
        this.countryService = countryService;
        this.rangeService = rangeService;
        this.regionService = regionService;
        this.adminService = adminService;
    }

    @RequestMapping("/showSaved")
    public String showMain(Model model){
        var countryList = countryService.readAll();
        model.addAttribute("number", 9);
        model.addAttribute("country", new Country());
        model.addAttribute("countryList", countryList);
        return "number";
    }

    @RequestMapping("/addRegion")
    public String showAddingRegionForm(Model model){
        var countryList = countryService.readAll();
        var rangeList = rangeService.readAll();
        model.addAttribute("countryList", countryList);
        model.addAttribute("rangeList", rangeList);
        model.addAttribute("region", new Region());
        return "add-region";
    }

    @RequestMapping("/savedRegion")
    public String addedRegion(@ModelAttribute Region region, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            region.setRange(rangeService.readById(1));
            region.setCountry(countryService.readById(1));
        }
        System.out.println(region);
       // regionService.saveOrUpdate(region);
        return "saved";
    }

    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute Country country, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "number";
        }
        countryService.saveOrUpdate(country);
//        var countryList = new ArrayList<Country>();
//        countryList.add(countryService.readById(1));
//        countryList.add(countryService.readById(4));
//        model.addAttribute("countryList", countryList);
        return "saved";
    }


    @RequestMapping("/addAdmin")
    public String addAdminForm(Model model){
        var admin = new Admin("admin", "admin", "admin@op.pl");
        adminService.saveOrUpdate(admin);
        return "add-admin";
    }
}
