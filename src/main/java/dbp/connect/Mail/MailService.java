package dbp.connect.Mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    public void enviarEmailRegistro(String emailto, String asunto){
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'a las' HH:mm 'hrs'", new Locale("es", "PE"));
        String formattedDate = zonedDateTime.withZoneSameInstant(ZoneId.of("America/Lima")).format(formatter);
        Context context = new Context();


        context.setVariable("date", formattedDate);

        String body = templateEngine.process("BienvenidaMail",  context);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("zamir.lizardo@utec.edu.pe");
            messageHelper.setTo(emailto);
            messageHelper.setSubject(asunto);
            messageHelper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}