package org.onstage.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.onstage.exceptions.BadRequestException;
import org.onstage.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class SendGridService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${email.invite.to.team.template}")
    private String inviteToTeamTemplateId;

    @Value("${email.test.template}")
    private String testTemplateId;

    public void sendEmail(String to, String subject, String templateId, Map<String, String> substitutions) {
        Email fromEmail = new Email("work.onstage@gmail.com");
        Email toEmail = new Email(to);
        Mail mail = new Mail(fromEmail, subject, toEmail, new Content("text/html", "will be replaced"));
        mail.setTemplateId(templateId);

        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            mail.personalization.get(0).addDynamicTemplateData(entry.getKey(), entry.getValue());
        }

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw BadRequestException.emailNotSent();
        }
    }

    public void sendInviteToTeamEmail(User user, String teamName) {
        Map<String, String> substitutions = Map.of(
                "name", user.name(),
                "teamName", teamName
        );
        sendEmail(user.email(), "Test email", inviteToTeamTemplateId, substitutions);
    }

    public void sendTestEmail(String emailTo) {
        sendEmail(emailTo, "Test email", testTemplateId, Map.of());
    }
}
