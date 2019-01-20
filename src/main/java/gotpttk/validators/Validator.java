package gotpttk.validators;

import gotpttk.entities.Book;

public abstract class Validator {
    BadgeLevel badgeLevel;

    void setBadgeLevel(BadgeLevel badgeLevel) {
        this.badgeLevel = badgeLevel;
    }

    public abstract void validate(Book book);

    abstract String getCurrentSummary(Book book);
}
