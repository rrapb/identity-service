package com.ubt.identityservice.services;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.ubt.identityservice.entities.EmailNotification;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration freemarkerConfig;

    public void sendEmail(EmailNotification emailNotification)
    {
        try
        {
            freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/emailTemplates");
            Template t = freemarkerConfig.getTemplate("email-template.ftl");
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, emailNotification.getContent());

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailNotification.getReceiver());
            helper.setSubject(emailNotification.getSubject());
            helper.setFrom(emailNotification.getSender());
            helper.setText(text, true);
            javaMailSender.send(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
