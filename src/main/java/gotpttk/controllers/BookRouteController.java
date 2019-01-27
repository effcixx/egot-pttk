package gotpttk.controllers;

import com.sun.istack.Nullable;
import gotpttk.entities.BookRoute;
import gotpttk.pdf.PdfCreator;
import gotpttk.service.*;
import gotpttk.validators.ValidatorManager;
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
import javax.validation.constraints.PastOrPresent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;

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


    /**
     * Metoda mapująca url '/show' na widok przedstawiający trasy wpisane do książeczki użytkownika.
     * @param model
     * Model używany do komunikacji między kontrolerem a widokiem.
     * Parametr wykorzystywany przy załadowaniu strony po dodaniu trasy do książeczki.
     * Zawiera informację o powodzeniu lub niepowodzeniu dodania trasy.
     * @param redirectAttributes
     * Parametry przekazywane automatycznie jeśli strona została otwarta poprzez przekierowanie
     * z innej metody.
     * @return
     * Nazwa widoku przedstawiającego trasy wpisane do książeczki użytkownika.
     */
    @RequestMapping("/show")
    public String showUsersRoutes(Model model,
                                  RedirectAttributes redirectAttributes){
        System.out.println("no mess");
        addStartupFieldsToModel(model);
        if (redirectAttributes != null){
            var message = model.asMap().get("message");
            if (message != null){
                model.addAttribute("crudMessage", message);
            }
        }
        return "show-routes";
    }


    private void addStartupFieldsToModel(Model model){
        int userId = 1;

        var publicRoutes = routeService.readAllPublic();
        var userRoutes = routeService.readRoutesDefinedByUser(userId);
        var badgeRouteMap = bookRouteService.getBookRoutesAndRespectiveBadges(userId);
        var currentBookRoutes = bookRouteService.readRoutesUnderCurrentBadge(userId);
        int currentNumberOfPoints = bookService.getCurrentNumberOfPoints(userId);

        model.addAttribute("publicRoutes", publicRoutes);
        model.addAttribute("userRoutes", userRoutes);
        model.addAttribute("badgeRouteMap", badgeRouteMap);
        model.addAttribute("currentBookRoutes", currentBookRoutes);
        model.addAttribute("currentNumberOfPoints", currentNumberOfPoints);
        model.addAttribute("bookRouteWrapper", new BookRouteWrapper());
        System.out.println("==============Elements:=======");
        for (var el : badgeRouteMap.entrySet()){
            System.out.println("Badge: " + el.getKey());
            for (var route : el.getValue()){
                System.out.println(route);
            }
        }
    }

    /**
     * Metoda mapująca url '/showSummary' na widok przedstawiający
     * zdobyte przez użytkownika odznaki.
     * @param model
     * Model używany do komunikacji między kontrolerem a widokiem.
     * @return
     * Nazwa widoku przedstawiającego odznaki zdobyte przez użytkownika.
     */
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


    /**
     * Metoda mapująca url '/showSummary' na widok przedstawiający szczegółowe informacje
     * o zdobytej w przeszłości odznace.
     * @param badgeId
     * Identyfikator (id) odznaki, atrybut pobrany automatycznie metodą GET.
     * @param model
     * Model używany do komunikacji między kontrolerem a widokiem.
     * @return
     * Nazwa widoku przedstawiającego podsumowanie zdobycia danej odznaki.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @GetMapping("/showDetailed")
    public String showDetailed(@RequestParam int badgeId, Model model){
        int userId = 1;
        var badgeToDisplay = badgeService.readById(badgeId);
        var currentBadgeCategory = categoryService.getCategoryOfCurrentBadge(userId);
        var routesCompletedUnderGivenBadge = bookRouteService.readRoutesCompletedUnderGivenBadge(badgeToDisplay, 1);
        routesCompletedUnderGivenBadge.sort(new BookRouteService.BookRouteComparator());
        model.addAttribute("badgeToDisplay", badgeToDisplay);
        model.addAttribute("currentBadgeCategory", currentBadgeCategory);
        model.addAttribute("routesCompletedUnderGivenBadge", routesCompletedUnderGivenBadge);
        model.addAttribute("optionWrapper", new PdfOptionWrapper());
        return "show-detailed-badge";
    }

    /**
     * Metoda mapująca url '/showDetailedCurrent' na widok przedstawiający szczegółowe informacje
     * o zdobywanej aktualnie odznace.
     * @param model
     * Model używany do komunikacji między kontrolerem a widokiem.
     * @return
     * Nazwa widoku przedstawiającego podsumowanie zdobywania aktualnej odznaki.
     */
    @RequestMapping("/showDetailedCurrent")
    public String showDetailedCurrent(Model model){
        int userId = 1;
        var book = bookService.getBookWithUserId(userId);
        var currentBadgeCategory = categoryService.getCategoryOfCurrentBadge(userId);
        var routes = bookRouteService.readRoutesUnderCurrentBadge(userId);
        routes.sort(new BookRouteService.BookRouteComparator());
        var summary = validatorManager.getSummaryOfProcess(book);
        model.addAttribute("currentBadgeCategory", currentBadgeCategory);
        model.addAttribute("routes", routes);
        model.addAttribute("summaryString", summary);
        model.addAttribute("optionWrapper", new PdfOptionWrapper());
        return "show-detailed-current";
    }

    /**
     * Metoda generująca plik PDF z uwzględnieniem części podsumowania, które mają być
     * wygenerowane w pliku. Wykorzystywana dla odznaki zdobytej w przeszłości.
     * @param optionWrapper
     * Obiekt typu PdfOptionWrapper określający, które części podsumowania mają być zawarte
     * w pliku.
     * @return
     * Plik pdf z odpowiednimi częściami podsumowania.
     * @throws IOException
     * Metoda może rzucić wyjątek IOException
     */

    @PostMapping("/getPdfSummary")
    public ResponseEntity<byte[]> showGeneratedPdfWithRestrictions
                        (@ModelAttribute PdfOptionWrapper optionWrapper) throws IOException {
        int userId = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        var badge = badgeService.readById(8);
        var routes = bookRouteService.readRoutesCompletedUnderGivenBadge(badge, userId);
        pdfCreator.reset();
        pdfCreator.setCategory(badge.getCategory());
        pdfCreator.setBookRoutes(routes);
        pdfCreator.setExcursions(null);
        pdfCreator.setAchieved(true);
        setPdfCreatorFieldsBasedOnUserPreferences(optionWrapper);
        return getResponseEntityPdf(headers);
    }

    /**
     * Metoda generująca plik PDF z uwzględnieniem części podsumowania, które mają być
     * wygenerowane w pliku. Wykorzystywana dla aktualnie zdobywanej odznaki.
     * @param optionWrapper
     * Obiekt typu PdfOptionWrapper określający, które części podsumowania mają być zawarte
     * w pliku.
     * @return
     * Plik pdf z odpowiednimi częściami podsumowania.
     * @throws IOException
     * Metoda może rzucić wyjątek IOException
     */
    @PostMapping("/getPdfSummaryCurrent")
    public ResponseEntity<byte[]> showGeneratedPdfForCurrentWithRestrictions
            (@ModelAttribute PdfOptionWrapper optionWrapper) throws IOException{
        int userId = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        var category = categoryService.getCategoryOfCurrentBadge(userId);
        var routes = bookRouteService.readRoutesUnderCurrentBadge(userId);
        var book = bookService.getBookWithUserId(userId);
        pdfCreator.reset();
        pdfCreator.setCategory(category);
        pdfCreator.setBookRoutes(routes);
        pdfCreator.setExcursions(null);
        pdfCreator.setAchieved(false);
        pdfCreator.setSummary(validatorManager.getSummaryOfProcess(book));
        setPdfCreatorFieldsBasedOnUserPreferences(optionWrapper);
        return getResponseEntityPdf(headers);
    }


    private void setPdfCreatorFieldsBasedOnUserPreferences(PdfOptionWrapper optionWrapper){
        if (optionWrapper.getOption1() != null){
            pdfCreator.setShouldGenerateBookRoutes(true);
        }
        if (optionWrapper.getOption2() != null){
            pdfCreator.setShouldGenerateExcursions(true);
        }
        if (optionWrapper.getOption3() != null){
            pdfCreator.setShouldGenerateSummary(true);
        }
    }

    private ResponseEntity<byte[]> getResponseEntityPdf(HttpHeaders headers) throws IOException {
        String filename = pdfCreator.getFile();
        File file = new File(filename);
        byte[] bytes = Files.readAllBytes(file.toPath());
        headers.add("content-disposition", "inline;filename=" + filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }


    /**
     * Metoda zapisuje nowe przejście trasy do książeczki.
     * @param bookRouteWrapper
     * Obiekt klasy BookRouteWrapper opakowującej parametry dotyczące trasy w książeczce
     * pobrane z widoku.
     * @param bindingResult
     * Obiekt reprezentujący wynik bindowania danych.
     * @param model
     * Model używany do komunikacji między kontrolerem a widokiem.
     * @param redirectAttributes
     * Atrybuty przekierowania pobierane automatycznie.
     * @return
     * Przekierowanie do strony wyświetlającej trasy w książeczce.
     */
    @PostMapping("/submitRoute")
    public String submitPostRoute(@Valid @ModelAttribute BookRouteWrapper bookRouteWrapper,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes){
        System.out.println(bookRouteWrapper.chosenRouteId + " "
                + bookRouteWrapper.dateOfCompletion + " " + bookRouteWrapper.isFromStartToEnd);
        if (bindingResult.hasErrors()){
            var errs = bindingResult.getAllErrors();
            return "add-public-route";
        }
        var dateOfCompletion = bookRouteWrapper.dateOfCompletion;
        var category = categoryService.getCategoryOfBadgeUnderCompletionOnGivenDate(dateOfCompletion, 1);

        var book = bookService.readById(5);
        var route = routeService.readById(bookRouteWrapper.chosenRouteId);

        var bookRouteToAdd = new BookRoute(bookRouteWrapper.isFromStartToEnd, new Date(dateOfCompletion.getTime()),
                category, route, book);
        System.out.println(bookRouteToAdd);

        bookRouteService.saveOrUpdate(bookRouteToAdd);
        bookService.updatePointsAndBadgesAfterCompletionOfRoute(book);
        //addStartupFieldsToModel(model);
        redirectAttributes.addFlashAttribute("message", "Trasa dodana poprawnie");
        return "redirect:/trasy/show";
//        return showUsersRoutes(model);
    }

    /**
     * Klasa opakowująca parametry dotyczące przejścia nowej trasy w książeczce.
     */
    static class BookRouteWrapper{
        @NotNull
        private int chosenRouteId;
        @NotNull
        private boolean isFromStartToEnd;

        @NotNull
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

    private static class PdfOptionWrapper{
        private String option1;
        private String option2;
        private String option3;

        private static String DESCRIPTION_1 = "przebyte trasy";
        private static String DESCRIPTION_2 = "przebyte wycieczki";
        private static String DESCRIPTION_3 = "stopień spełnienia warunków";


        public PdfOptionWrapper() { }

        public static String getDescription1() {
            return DESCRIPTION_1;
        }

        public static String getDescription2() {
            return DESCRIPTION_2;
        }

        public static String getDescription3() {
            return DESCRIPTION_3;
        }

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

        @Override
        public String toString() {
            return "PdfOptionWrapper{" +
                    "option1='" + option1 + '\'' +
                    ", option2='" + option2 + '\'' +
                    ", option3='" + option3 + '\'' +
                    '}';
        }
    }
}
