package com.tkt.quizedu.service.notification;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
  private final JavaMailSender mailSender;
  private final SpringTemplateEngine springTemplateEngine;

  @Value("${spring.mail.from}")
  @NonFinal
  String from;

  @Value("${admin.email}")
  @NonFinal
  String supportEmail;

  @KafkaListener(topics = "confirm-account-topic", groupId = "quizedu-group")
  private void sendConfirmEmailByKafka(String message)
      throws MessagingException, UnsupportedEncodingException {
    String[] parts = message.split(",");
    String email = parts[0].substring(parts[0].indexOf("=") + 1);
    String name = parts[1].substring(parts[1].indexOf("=") + 1);
    String code = parts[2].substring(parts[2].indexOf("=") + 1);
    log.info("Sending confirmation email to: {}, name: {}, code: {}", email, name, code);

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    helper.setFrom(from, "QuizEdu Support");
    helper.setTo(email);
    helper.setSubject("Verification for Your QuizEdu Account");

    Map<String, Object> variables = new HashMap<>();
    variables.put("name", name);
    variables.put("code", code);
    variables.put("supportEmail", supportEmail);

    Context context = new Context();
    context.setVariables(variables);
    String content = springTemplateEngine.process("confirm-account.html", context);
    helper.setText(content, true);

    mailSender.send(mimeMessage);
  }

  @KafkaListener(topics = "send-class-code-to-emails", groupId = "quizedu-group")
  private void sendClassCodeToEmails(String message)
      throws MessagingException, UnsupportedEncodingException {
    // Fix the splitting - current code splits by empty string which separates each character
    String[] parts = message.split(",");

    // Parse emails and class code
    String emailsString = parts[0].substring(parts[0].indexOf("=") + 1);
    String classCode = parts[1].substring(parts[1].indexOf("=") + 1);
    String teacherName = parts[2].substring(parts[2].indexOf("=") + 1);
    String classroomName = parts[3].substring(parts[3].indexOf("=") + 1);

    // Split the email addresses by semicolon
    String[] emails = emailsString.split(";");

    log.info("Sending class code {} to {} recipients", classCode, emails.length);

    for (String email : emails) {
      sendClassCodeEmail(email.trim(), classCode, teacherName, classroomName);
    }
  }

  private void sendClassCodeEmail(String email, String classCode, String teacherName, String classroomName)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    helper.setFrom(from, "QuizEdu Support");
    helper.setTo(email);
    helper.setSubject("Your Class Code for QuizEdu");

    Map<String, Object> variables = new HashMap<>();
    variables.put("classCode", classCode);
    variables.put("teacherName", teacherName);
    variables.put("classroomName", classroomName);
    variables.put("supportEmail", supportEmail);

    Context context = new Context();
    context.setVariables(variables);
    String content = springTemplateEngine.process("class-code-email.html", context);
    helper.setText(content, true);

    mailSender.send(mimeMessage);
    log.info("Class code email sent to: {}", email);
  }
}
