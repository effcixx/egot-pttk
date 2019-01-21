package gotpttk.controllers;

import gotpttk.entities.Badge;
import gotpttk.entities.Book;
import gotpttk.entities.BookRoute;
import gotpttk.entities.Category;
import gotpttk.pdf.PdfCreator;
import gotpttk.service.*;
import gotpttk.validators.ValidatorManager;
import gotpttk.validators.ValidatorWGory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trasy")
public class BookRouteController {

    @Autowired
    private BookRouteService bookRouteService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private PdfCreator pdfCreator;

    @Autowired
    private ValidatorManager validatorManager;

    private java.util.Date[] getPeriodOfScoringGivenBadge(List<Badge> userBadges, Badge badge){
        var userBadgesSorted = userBadges.stream()
                .sorted(Comparator.comparing(Badge::getAchievingDate))
                .collect(Collectors.toList());
        boolean found = false;
        java.util.Date[] dates = null;
        for (int i=0; i<userBadgesSorted.size() && !found; i++){
            if (userBadgesSorted.get(i).equals(badge)){
                found = true;
                if (i == 0){
                    dates =  new java.util.Date[]{new java.util.Date(0), badge.getAchievingDate()};
                }
                else{
                    dates= new java.util.Date[]{userBadgesSorted.get(i-1).getAchievingDate(),
                            badge.getAchievingDate()};
                }
            }
        }
        return dates;
    }


    @RequestMapping("/show")
    public String showUsersRoutes(Model model){
        int userId = 1;
        List<BookRoute> bookRoutes = bookRouteService.readAll()
                .stream()
                .sorted((b1, b2) -> b2.getDateOfCompletion().compareTo(b1.getDateOfCompletion()))
                .collect(Collectors.toList());

        var publicRoutes = routeService.readAllPublic();
        var userRoutes = routeService.readRoutesDefinedByUser(1);
        var badgesScored = badgeService.getAllBadgesScoredByUser(1)
                .stream().sorted((b1, b2) -> b2.getAchievingDate().compareTo(b1.getAchievingDate()))
                .collect(Collectors.toList());
        //var badgeRouteMap = new HashMap<Badge, List<BookRoute>>();
        var badgeRouteMap = new TreeMap<Badge, List<BookRoute>>(
                        (b1, b2) -> b2.getAchievingDate().compareTo(b1.getAchievingDate())
        );

        for (var badge : badgesScored){
            var badgeScoringPeriod = getPeriodOfScoringGivenBadge(badgesScored, badge);
            var listOfRoutes = bookRoutes.stream()
                    .filter(bookRoute ->
                                    bookRoute.getCurrentBadgeCategory().equals(badge.getCategory())
                                    && bookRoute.getDateOfCompletion().getTime() >= badgeScoringPeriod[0].getTime()
                                    && bookRoute.getDateOfCompletion().getTime() <= badgeScoringPeriod[1].getTime()
                    )
                    .sorted(new BookRouteComparator())
                    .collect(Collectors.toList());
            badgeRouteMap.put(badge, listOfRoutes);
        }

        var lastBadgeScored = badgeService.getLastBadgeScored(userId);
        List<BookRoute> currentBookRoutes;

        if (lastBadgeScored == null){
            currentBookRoutes = bookRoutes;
        }
        else{
            currentBookRoutes = bookRoutes.stream().filter(bookRoute ->
                    bookRoute.getDateOfCompletion().getTime() >= lastBadgeScored.getAchievingDate().getTime()
                            && !bookRoute.getCurrentBadgeCategory().equals(lastBadgeScored.getCategory()))
                    //.sorted((b1, b2) -> b2.getDateOfCompletion().compareTo(b1.getDateOfCompletion()))
//                    .sorted(
//                            Comparator.comparing(BookRoute::getDateOfCompletion)
//                            .reversed()
//                            .thenComparing((b1, b2)->b2.getId() - b1.getId()))
//                    .collect(Collectors.toList());
                    .sorted(new BookRouteComparator())
                    .collect(Collectors.toList());
        }


        var badge = new Badge();
        badge.setAchievingDate(new Date(new java.util.Date().getTime()));
        badge.setCategory(categoryService.getCategoryOfCurrentBadge(userId));
        badgeRouteMap.put(badge, currentBookRoutes);

        int currentNumberOfPoints = bookService.getCurrentNumberOfPoints(userId);

        model.addAttribute("currentNumberOfPoints", currentNumberOfPoints);
        model.addAttribute("badgeRouteMap", badgeRouteMap);
        model.addAttribute("currentBookRoutes", currentBookRoutes);
        model.addAttribute("userRoutes", userRoutes);
        model.addAttribute("publicRoutes", publicRoutes);
        model.addAttribute("bookRouteWrapper", new BookRouteWrapper());
        return "show-routes";
    }

    @RequestMapping("/showSummary")
    public String showSummary(Model model){
        int userId = 1;
        var userBadges = badgeService.getAllBadgesScoredByUser(userId);
        userBadges.sort((b1, b2) -> b2.getAchievingDate().compareTo(b1.getAchievingDate()));
        var currentBadgeCategory = categoryService.getCategoryOfCurrentBadge(userId);
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("currentBadgeCategory", currentBadgeCategory);
        return "show-summary";
    }

    private List<BookRoute> readRoutesCompletedUnderGivenBadge(Badge badge, int userId){
        var userBadges = badgeService.getAllBadgesScoredByUser(userId);
        var period = getPeriodOfScoringGivenBadge(userBadges, badge);
        var userBookRoutes = bookRouteService.readUserRoutes(userId);
        return userBookRoutes.stream()
                .filter(bookRoute -> bookRoute.getCurrentBadgeCategory().equals(badge.getCategory())
                        && bookRoute.getDateOfCompletion().getTime() >= period[0].getTime()
                        && bookRoute.getDateOfCompletion().getTime() <= period[1].getTime())
                .collect(Collectors.toList());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @GetMapping("/showDetailed")
    public String showDetailed(@RequestParam int badgeId, Model model){
        var badgeToDisplay = badgeService.readById(badgeId);
        var currentBadgeCategory = categoryService.getCategoryOfCurrentBadge(1);
        var routesCompletedUnderGivenBadge = readRoutesCompletedUnderGivenBadge(badgeToDisplay, 1);
        routesCompletedUnderGivenBadge.sort(new BookRouteComparator());
        model.addAttribute("badgeToDisplay", badgeToDisplay);
        model.addAttribute("currentBadgeCategory", currentBadgeCategory);
        model.addAttribute("routesCompletedUnderGivenBadge", routesCompletedUnderGivenBadge);
        var list = new String[4];
        model.addAttribute("optionWrapper", new PdfOptionWrapper());
        for (var b : routesCompletedUnderGivenBadge){
            System.out.println(b.getDateOfCompletion() +": " + b.getRoute().getStartingPoint().getName()
                    + " - " + b.getRoute().getEndPoint().getName());
        }
        System.out.println("Hello, " + badgeId);
        System.out.println(badgeToDisplay);
        System.out.println("%%%%%%");
        return "show-detailed-badge";
    }

    private static class PdfOptionWrapper{
        private String option1;
        private String option2;
        private String option3;
        //private String option4;

        private static String DESCRIPTION_1 = "przebyte trasy";
        private static String DESCRIPTION_2 = "przebyte wycieczki";
        private static String DESCRIPTION_3 = "stopień spełnienia warunków";
        //private static String DESCRIPTION_4 = "lista tras do przebycia";


        public PdfOptionWrapper() {
        }

        public static String getDescription1() {
            return DESCRIPTION_1;
        }

        public static String getDescription2() {
            return DESCRIPTION_2;
        }

        public static String getDescription3() {
            return DESCRIPTION_3;
        }

//        public static String getDescription4() {
//            return DESCRIPTION_4;
//        }

        public String getOption1() {
            return option1;
        }

        public void setOption1(String option1) {
            this.option1 = option1;
        }

        public String getOption2() {
            return option2;
        }

        public void setOption2(String option2) {
            this.option2 = option2;
        }

        public String getOption3() {
            return option3;
        }

        public void setOption3(String option3) {
            this.option3 = option3;
        }

//        public String getOption4() {
//            return option4;
//        }

//        public void setOption4(String option4) {
//            this.option4 = option4;
//        }

        @Override
        public String toString() {
            return "PdfOptionWrapper{" +
                    "option1='" + option1 + '\'' +
                    ", option2='" + option2 + '\'' +
                    ", option3='" + option3 + '\'' +
                   // ", option4='" + option4 + '\'' +
                    '}';
        }
    }

    @RequestMapping("/showDetailedCurrent")
    public String showDetailedCurrent(Model model){
        int userId = 1;
        var book = bookService.getBookWithUserId(userId);
        var currentBadgeCategory = categoryService.getCategoryOfCurrentBadge(userId);
        var routes = bookRouteService.readRoutesUnderCurrentBadge(userId);
        routes.sort(new BookRouteComparator());
        var summary = validatorManager.getSummaryOfProcess(book);
        model.addAttribute("currentBadgeCategory", currentBadgeCategory);
        model.addAttribute("routes", routes);
        model.addAttribute("summaryString", summary);
        model.addAttribute("optionWrapper", new PdfOptionWrapper());
        System.out.println("Podsumowanie: " + summary);
        return "show-detailed-current";
    }

//    @GetMapping("/chooseRoute")
//    public String showRouteInformation(@RequestParam int bookRouteId){
//
//    }

    @RequestMapping("/addRouteToBook")
    public String addRouteToBook(Model model){
        var publicRoutes = routeService.readAllPublic();
        var userRoutes = routeService.readRoutesDefinedByUser(1);
        model.addAttribute("publicRoutes", publicRoutes);
        model.addAttribute("userRoutes", userRoutes);
        model.addAttribute("newBookRoute", new BookRoute());
        model.addAttribute("bookRouteWrapper", new BookRouteWrapper());
        return "add-public-route";
    }

    //@GetMapping("/getPdfSummary")
//    public String showGeneratedPdf(@RequestParam int badgeId){
//        System.out.println("@@@@@ Badge: " + badgeId);
//        PdfCreator pdfCreator = new PdfCreator();
//        var path = pdfCreator.saveFile();
//        return path;
//    }

//    @GetMapping("/getPdfSummary")
//    public ResponseEntity<byte[]> showGeneratedPdf(@RequestParam int badgeId) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/pdf"));
//        var badge = badgeService.readById(badgeId);
//        var routes = readRoutesCompletedUnderGivenBadge(badge, 1);
//        pdfCreator.setCategory(badge.getCategory());
//        pdfCreator.setBookRoutes(routes);
//        pdfCreator.setExcursions(null);
//        pdfCreator.setAchieved(true);
//        return getResponseEntityPdf(headers);
//    }

    @PostMapping("/getPdfSummary")
    public ResponseEntity<byte[]> showGeneratedPdfWithRestrictions
                        (@ModelAttribute PdfOptionWrapper optionWrapper) throws IOException {
        System.out.println("POST");
        System.out.println(optionWrapper);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        var badge = badgeService.readById(8);
        var routes = readRoutesCompletedUnderGivenBadge(badge, 1);
        pdfCreator.reset();
        pdfCreator.setCategory(badge.getCategory());
        pdfCreator.setBookRoutes(routes);
        pdfCreator.setExcursions(null);
        pdfCreator.setAchieved(true);
        if (optionWrapper.getOption1() != null){
            System.out.println("Pierwaja");
            pdfCreator.setShouldGenerateBookRoutes(true);
        }
        if (optionWrapper.getOption2() != null){
            System.out.println("Drugaja");
            pdfCreator.setShouldGenerateExcursions(true);
        }
        if (optionWrapper.getOption3() != null){
            System.out.println("Trzeciaja");
            pdfCreator.setShouldGenerateSummary(true);
        }
//        if (optionWrapper.getOption4() != null){
//            System.out.println("Czwartaja");
//            pdfCreator.setShouldGenerateRemainingRoutes(true);
//        }
        return getResponseEntityPdf(headers);
    }
//    @GetMapping("/getPdfSummaryCurrent")
//    public ResponseEntity<byte[]> showGeneratedPdfForCurrent() throws IOException{
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/pdf"));
//        var category = categoryService.getCategoryOfCurrentBadge(1);
//        var routes = bookRouteService.readRoutesUnderCurrentBadge(1);
//        var book = bookService.getBookWithUserId(1);
//        pdfCreator.setCategory(category);
//        pdfCreator.setBookRoutes(routes);
//        pdfCreator.setExcursions(null);
//        pdfCreator.setAchieved(false);
//        pdfCreator.setSummary(validatorManager.getSummaryOfProcess(book));
//        return getResponseEntityPdf(headers);
//    }

    @PostMapping("/getPdfSummaryCurrent")
    public ResponseEntity<byte[]> showGeneratedPdfForCurrentWithRestrictions
            (@ModelAttribute PdfOptionWrapper optionWrapper) throws IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        var category = categoryService.getCategoryOfCurrentBadge(1);
        var routes = bookRouteService.readRoutesUnderCurrentBadge(1);
        var book = bookService.getBookWithUserId(1);
        pdfCreator.reset();
        pdfCreator.setCategory(category);
        pdfCreator.setBookRoutes(routes);
        pdfCreator.setExcursions(null);
        pdfCreator.setAchieved(false);
        pdfCreator.setSummary(validatorManager.getSummaryOfProcess(book));
        if (optionWrapper.getOption1() != null){
            System.out.println("Firsty");
            pdfCreator.setShouldGenerateBookRoutes(true);
        }
        if (optionWrapper.getOption2() != null){
            System.out.println("Secondy");
            pdfCreator.setShouldGenerateExcursions(true);
        }
        if (optionWrapper.getOption3() != null){
            System.out.println("Thirdy");
            pdfCreator.setShouldGenerateSummary(true);
        }
//        if (optionWrapper.getOption4() != null){
//            System.out.println("Fourthy");
//            pdfCreator.setShouldGenerateRemainingRoutes(true);
//        }
        return getResponseEntityPdf(headers);
    }

    private ResponseEntity<byte[]> getResponseEntityPdf(HttpHeaders headers) throws IOException {
        String filename = pdfCreator.getFile();
        File file = new File(filename);
        byte[] bytes = Files.readAllBytes(file.toPath());
        headers.add("content-disposition", "inline;filename=" + filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @RequestMapping("/fakeForm")
    public String tester(){
        return "fake-form";
    }

    @PostMapping("/submitRoute")
    public String submitPostRoute(@Valid @ModelAttribute BookRouteWrapper bookRouteWrapper,
                                  BindingResult bindingResult,
                                  Model model){
        System.out.println(bookRouteWrapper.chosenRouteId + " "
                + bookRouteWrapper.dateOfCompletion + " " + bookRouteWrapper.isFromStartToEnd);
        if (bindingResult.hasErrors()){
            System.out.println("Has errors.");
            var errs = bindingResult.getAllErrors();
            errs.forEach(System.out::println);
            return "add-public-route";
        }
        var dateOfCompletion = bookRouteWrapper.dateOfCompletion;
        var category = categoryService.getCategoryOfBadgeUnderCompletionOnGivenDate(dateOfCompletion, 1);
        // get Badge

        var book = bookService.readById(5);
        var route = routeService.readById(bookRouteWrapper.chosenRouteId);

        var bookRouteToAdd = new BookRoute(bookRouteWrapper.isFromStartToEnd, new Date(dateOfCompletion.getTime()),
                category, route, book);
        System.out.println(bookRouteToAdd);

        model.addAttribute("crudMessage", "Trasa dodana poprawnie");
        System.out.println("Hej1");
        bookRouteService.saveOrUpdate(bookRouteToAdd);
        System.out.println("Hej2");
        bookService.updatePointsAndBadgesAfterCompletionOfRoute(book);
        return showUsersRoutes(model);
    }



    @GetMapping("/submitRoute")
    public String submitRoute(@RequestParam int chosenRouteId,
                              @RequestParam boolean direction,
                              @RequestParam(name = "date") String dateToParse) {
        System.out.println(chosenRouteId);
        System.out.println(direction);
        System.out.println(dateToParse);
        var dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date sqlDate;
        int bookId = 2;
        var category = categoryService.readById(2);
        var route = routeService.readById(chosenRouteId);
        var book = bookService.readById(5);
        try {
            var date = dateFormat.parse(dateToParse);
            sqlDate = new Date(date.getTime());
            System.out.println(date);
            //var bookRoute = new BookRoute(direction, sqlDate, category, route, book);
            //System.out.println(bookRoute);
            //bookRouteService.saveOrUpdate(bookRoute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if (bindingResult.hasErrors()){
//            System.out.println(bindingResult.getAllErrors().get(0));
//        }
        return "fake-form";
    }

    @GetMapping("/submitUserRoute")
    public String submitUserRoute(){
        return "fake-form";
    }

    public static class BookRouteComparator implements Comparator<BookRoute>{
        @Override
        public int compare(BookRoute firstBookRoute, BookRoute secondBookRoute) {
            boolean areOnSameDay = firstBookRoute.getDateOfCompletion()
                                    .equals(secondBookRoute.getDateOfCompletion());
            int result;
            if (areOnSameDay){
                if (firstBookRoute.getRoute().equals(secondBookRoute.getRoute())){
                    result = firstBookRoute.getPointsAwarded() - secondBookRoute.getPointsAwarded();
                }
                else{
                    result = secondBookRoute.getId() - firstBookRoute.getId();
                }
            }
            else{
                result = secondBookRoute.getDateOfCompletion()
                                .compareTo(firstBookRoute.getDateOfCompletion());
            }
            return result;
        }
    }

    static class BookRouteWrapper{
        @NotNull
        private int chosenRouteId;
        @NotNull
        private boolean isFromStartToEnd;

        @NotNull
        //@DateTimeFormat(pattern = "MM/dd/yyyy")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        @PastOrPresent
        private java.util.Date dateOfCompletion;

        public int getChosenRouteId() {
            return chosenRouteId;
        }

        public void setChosenRouteId(int chosenRouteId) {
            this.chosenRouteId = chosenRouteId;
        }

        public boolean getIsFromStartToEnd() {
            return isFromStartToEnd;
        }

        public void setIsFromStartToEnd(boolean fromStartToEnd) {
            isFromStartToEnd = fromStartToEnd;
        }

        public java.util.Date getDateOfCompletion() {
            System.out.println(" ===> " + dateOfCompletion);
            return dateOfCompletion;
        }

        public void setDateOfCompletion(java.util.Date dateOfCompletion) {
            System.out.println(" ===> " + dateOfCompletion);
            this.dateOfCompletion = dateOfCompletion;
        }
    }
}
