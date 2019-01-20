package gotpttk.validators;

import gotpttk.entities.Badge;
import gotpttk.entities.Book;
import gotpttk.entities.Tourist;
import gotpttk.service.BadgeService;
import gotpttk.service.BookService;
import gotpttk.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class ValidatorWGory extends Validator {

    private final BookService bookService;

    private final BadgeService badgeService;

    private final CategoryService categoryService;

    private static final int BRONZE_SETPOINT = 15;
    private static final int SILVER_SETPOINT = 30;
    private static final int GOLD_SETPOINT = 45;
    private static final int BOOK_ID = 5;

    @Autowired
    public ValidatorWGory(BookService bookService, BadgeService badgeService, CategoryService categoryService) {
        this.bookService = bookService;
        this.badgeService = badgeService;
        this.categoryService = categoryService;
       // badgeLevel = BadgeLevel.BRAZOWY;
    }

    @Override
    public void validate(Book book) {
        int currentNumberOfPoints = bookService.getCurrentNumberOfPoints(book.getOwner().getId());
        Badge badge;
        Tourist owner = book.getOwner();
        System.out.println("Masz punktow : " + bookService.getCurrentNumberOfPoints(book.getOwner().getId()));
        System.out.println("Jestes na poziomie: " + badgeLevel.name());
        if (badgeLevel == BadgeLevel.BRAZOWY){
            if (currentNumberOfPoints >= BRONZE_SETPOINT){
                saveBadge(owner);
            }
        }
        else if (badgeLevel == BadgeLevel.SREBRNY){
            if (currentNumberOfPoints >= SILVER_SETPOINT){
                saveBadge(owner);
            }
        }
        else if (badgeLevel == BadgeLevel.ZLOTY){
            if (currentNumberOfPoints >= GOLD_SETPOINT){
                saveBadge(owner);
            }
        }
        else{
            if (currentNumberOfPoints >= BRONZE_SETPOINT){
                saveBadge(owner);
            }
        }
    }

    @Override
    String getCurrentSummary(Book book) {
        int userId = 1;
        int currentPoints = bookService.getCurrentNumberOfPoints(userId);
        int necessaryPoints = BRONZE_SETPOINT;
        if (badgeLevel == BadgeLevel.SREBRNY){
            necessaryPoints = SILVER_SETPOINT;
        }
        else if (badgeLevel == BadgeLevel.ZLOTY){
            necessaryPoints = GOLD_SETPOINT;
        }
        return "Minimum: " + necessaryPoints + " pkt. - brakuje Ci " + (necessaryPoints-currentPoints) +
                " pkt. (obecnie: " + currentPoints + " pkt.)";
    }

    private void saveBadge(Tourist owner) {
        var category = categoryService.getCategoryOfCurrentBadge(owner.getId());
        var dateSql = new Date(new java.util.Date().getTime());
        var badge = new Badge(dateSql, category, owner);
        badgeService.saveOrUpdate(badge);
        System.out.println("Saved badge");
    }


}
