package dbp.connect.Mail;

import org.springframework.context.ApplicationEvent;

public class MailRegistroEvent extends ApplicationEvent {
    private final String email;

    public MailRegistroEvent(String email) {
        super(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}