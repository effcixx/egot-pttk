package gotpttk.controllers;

import gotpttk.entities.Point;
import gotpttk.entities.Range;
import gotpttk.entities.Route;
import gotpttk.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/admin")
public class EditRouteController {
    private RegionService regionService;
    private RouteService routeService;
    private RangeService rangeService;
    private PointService pointService;

    @Autowired
    public EditRouteController(RouteService routeService, RangeService rangeService, RegionService regionService, PointService pointService) {
        this.rangeService = rangeService;
        this.regionService = regionService;
        this.routeService = routeService;
        this.pointService = pointService;
    }

    @RequestMapping("/adminRoute")
    public String showOption() {
        return "admin-route-option";
    }


    @RequestMapping(value = "/editRoute")
    public String showRangeRegion(Model model) {
        var rangeList = rangeService.readAll();
        //var regionList = regionService.readAll();
        var regionList = regionService.readAll();
        var routeList = routeService.readAll();

        model.addAttribute("rangeList", rangeList);
        model.addAttribute("regionList", regionList);
        model.addAttribute("routeList", routeList);
        return "edit-route";
    }


    @RequestMapping("/newRegion")
    public String showRegion(@RequestParam("rangeName") Range range, Model model) {
        //var rangeList = rangeService.readAll();
        var regionList = regionService.readAll();
        model.addAttribute("regionList", regionList);
        //var regionList = regionService.readAll();
        //model.addAttribute("regionList", regionList);
        //model.addAttribute("rangeList", rangeList);
        System.out.println("dasca");
        return "edit-route";
    }


    @GetMapping(value = "/editSpecyficRoute")
    public String upadteRoute(@RequestParam("idRoute") int idRoute, Model model) {

        Route route = routeService.readById(idRoute);
        System.out.println("id ounktu start " + route.getStartingPoint().getId());
        int a = route.getStartingPoint().getId();
        Point startPoint = pointService.readById(a);
        //Point startPoint = route.getStartingPoint();
        Point endPoint = route.getEndPoint();
        System.out.println(startPoint.toString());
        model.addAttribute("currentRoute", route);
        model.addAttribute("currentStartPoint", startPoint);
        model.addAttribute("currentEndPoint", endPoint);
        return "edit-current-route";
    }

    @RequestMapping(value = "/saveRoute", method = RequestMethod.POST)
    public String saveRoute(@ModelAttribute("currentRoute") Route theRoute,
                            @ModelAttribute("currentStartPoint") Point startPoint,
                            @ModelAttribute("currentEndPoint") Point endPoint) {
        System.out.println(startPoint.toString());
        System.out.println(endPoint.toString());
        System.out.println(theRoute.toString());

        //routeService.saveOrUpdate(theRoute);
        return "redirect:/admin/editRoute";
    }

}