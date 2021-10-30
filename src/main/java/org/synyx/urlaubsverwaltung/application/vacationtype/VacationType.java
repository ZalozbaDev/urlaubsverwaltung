package org.synyx.urlaubsverwaltung.application.vacationtype;

import java.util.Objects;

/**
 * Describes a type of vacation.
 */
public class VacationType {

    private Integer id;
    private boolean active;
    private VacationCategory category;
    private String messageKey;

    public VacationType() {
        // ok
    }

    public VacationType(Integer id, boolean active, VacationCategory category, String messageKey) {
        this.id = id;
        this.active = active;
        this.category = category;
        this.messageKey = messageKey;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean enabled) {
        this.active = enabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VacationCategory getCategory() {
        return category;
    }

    public void setCategory(VacationCategory category) {
        this.category = category;
    }

    public boolean isOfCategory(VacationCategory category) {
        return getCategory().equals(category);
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String toString() {
        return "VacationType{" +
            "id=" + id +
            ", active=" + active +
            ", category=" + category +
            ", messageKey='" + messageKey + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacationType that = (VacationType) o;
        return active == that.active && category == that.category && Objects.equals(messageKey, that.messageKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, category, messageKey);
    }
}
