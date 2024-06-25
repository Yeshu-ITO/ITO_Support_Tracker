package ITO.Application.ItoSupportTracker.utility;

import org.springframework.stereotype.Component;

@Component
public class Constants {

    public final String COUNTER = "Counter";
    public final String SUBCATEGORY_COLLECTION = "SubCategory";
    public final String SUBCATEGORY_ID_COUNTER = "/subcategory-id-counter";
    public final String CATEGORY_ID_COUNTER = "/category-id-counter";
    public final String COMMENT_COLLECTION = "Comment";
    public final String CATEGORY_COLLECTION = "Category";
    public final String COMMENT_ID_COUNTER = "/comment-id-counter";
    public final String TICKET_COLLECTION = "Ticket";
    public final String ITO_TRACKER = "itotracker";
    public final String TICKET_ID_COUNTER = "/ticket-id-counter";
    public final String ADMIN_ID_COUNTER = "/admin-id-counter";
    public final String ADMIN_COLLECTION = "IT_Team_Admin";
    public final String USER_ID_COUNTER = "/user-id-counter";
    public final String USER_COLLECTION = "User_Team";

    public enum Priority{
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum Status{
        OPEN,
        ASSIGNED,
        IN_PROGRESS,
        COMPLETED
    }

}
