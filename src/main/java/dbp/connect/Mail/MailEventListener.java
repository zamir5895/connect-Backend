package dbp.connect.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MailEventListener {
    @Autowired
    private MailService mailService;

    @EventListener
    @Async
    public void enviarEmail(MailRegistroEvent pe) {
        mailService.enviarEmailRegistro( pe.getEmail(), "Registro");
    }

}