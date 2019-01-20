package gotpttk.validators;

import gotpttk.entities.Book;
import gotpttk.service.BookService;
import gotpttk.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ValidatorManager {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ApplicationContext applicationContext;

    public final void manageValidation(Book book) {
        int userId = book.getOwner().getId();
        int hierarchyOfCurrentBadge = categoryService.getCategoryOfCurrentBadge(userId).getHierarchyLevel();
        System.out.println("Hierarchia: " + hierarchyOfCurrentBadge);
        switch (hierarchyOfCurrentBadge) {
            case 1:
                setUpProperValidation(ValidatorWGory.class, BadgeLevel.BRAZOWY, book);
                break;
            case 2:
                setUpProperValidation(ValidatorWGory.class, BadgeLevel.SREBRNY, book);
                break;
            case 3:
                setUpProperValidation(ValidatorWGory.class, BadgeLevel.ZLOTY, book);
                break;
            default: {
                setUpProperValidation(ValidatorWGory.class, BadgeLevel.BRAZOWY, book);
            }
        }
    }

    private void setUpProperValidation(Class<? extends Validator> aClass, BadgeLevel badgeLevel, Book book) {
        var bean = applicationContext.getBean(aClass);
        bean.setBadgeLevel(badgeLevel);
        bean.validate(book);
    }

    private String getSummaryOfProcess(Class<? extends Validator> aClass, BadgeLevel badgeLevel, Book book) {
        var bean = applicationContext.getBean(aClass);
        bean.setBadgeLevel(badgeLevel);
        return bean.getCurrentSummary(book);
    }

    public String getSummaryOfProcess(Book book) {
        int userId = book.getOwner().getId();
        int hierarchyOfCurrentBadge = categoryService.getCategoryOfCurrentBadge(userId).getHierarchyLevel();
        String summary = "";
        switch (hierarchyOfCurrentBadge) {
            case 1:
                summary = getSummaryOfProcess(ValidatorWGory.class, BadgeLevel.BRAZOWY, book);
                break;
            case 2:
                summary = getSummaryOfProcess(ValidatorWGory.class, BadgeLevel.SREBRNY, book);
                break;
            case 3:
                summary = getSummaryOfProcess(ValidatorWGory.class, BadgeLevel.ZLOTY, book);
                break;
            default: {
                summary = getSummaryOfProcess(ValidatorWGory.class, BadgeLevel.BRAZOWY, book);
            }
        }
        return summary;
    }

//    ///TODO przemyslec:
//    public void awardBadge(Book book){
////        var category = new Category();
////        var badge = new Badge(new Date(), )
//    }
//
//    public static Validator getValidatorBasedOnLastBadgeAwarded(Category categoryOfLastBadge){
//        Validator validator;
//        switch(categoryOfLastBadge.getHierarchyLevel()){
//            case 1:
//                validator = new ValidatorWGory();
//                break;
//            default:
//                validator = new ValidatorWGory();
//        }
//        return validator;
//    }
}
