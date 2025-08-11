package com.tkt.quizedu.service.notification;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.tkt.quizedu.service.s3.IS3Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MAIL-SERVICE")
public class MailService {
  private final JavaMailSender mailSender;
  private final SpringTemplateEngine springTemplateEngine;
  private final IS3Service s3Service; // Assuming you have an S3 service for file handling

  @Value("${spring.mail.from}")
  @NonFinal
  String from;

  @Value("${admin.email}")
  @NonFinal
  String supportEmail;

  private void sendEmailWithAttachments(
      String recipients,
      String subject,
      String template,
      Map<String, Object> variables,
      List<File> attachments)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    helper.setFrom(from, "QuizEdu Support");

    // Handle multiple recipients
    if (recipients.contains(",")) {
      helper.setTo(InternetAddress.parse(recipients));
      log.info("Sending email to multiple recipients: {}", recipients);
    } else {
      helper.setTo(recipients);
      log.info("Sending email to: {}", recipients);
    }

    helper.setSubject(subject);

    // Process template
    Context context = new Context();
    context.setVariables(variables);
    String content = springTemplateEngine.process(template, context);
    helper.setText(content, true);

    // Attach files
    for (File file : attachments) {
      helper.addAttachment(file.getName(), file);
    }

    try {
      mailSender.send(mimeMessage);
      log.info("Email sent successfully with subject: {}", subject);
    } catch (Exception e) {
      log.error("Failed to send email with attachments: {}", e.getMessage());
      throw e;
    }
  }

  @KafkaListener(topics = "confirm-account-topic", groupId = "quizedu-group")
  private void sendConfirmEmailByKafka(String message) {
    try {
      String[] parts = message.split(",");
      String email = extractValue(parts[0]);
      String name = extractValue(parts[1]);
      String code = extractValue(parts[2]);

      log.info("Processing confirmation email to: {}, name: {}", email, code);

      Map<String, Object> variables = new HashMap<>();
      variables.put("name", name);
      variables.put("code", code);
      variables.put("supportEmail", supportEmail);

      sendEmail(email, "Verification for Your QuizEdu Account", "confirm-account.html", variables);
    } catch (Exception e) {
      log.error("Failed to send confirmation email: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(topics = "send-class-code-to-emails", groupId = "quizedu-group")
  private void sendClassCodeToEmails(String message) {
    try {
      String[] parts = message.split(";");
      String emailsString = extractValue(parts[0]);
      String classCode = extractValue(parts[1]);
      String teacherName = extractValue(parts[2]);
      String classroomName = extractValue(parts[3]);

      Map<String, Object> variables = new HashMap<>();
      variables.put("classCode", classCode);
      variables.put("teacherName", teacherName);
      variables.put("classroomName", classroomName);
      variables.put("supportEmail", supportEmail);

      sendEmail(emailsString, "Your Class Code for QuizEdu", "class-code-email.html", variables);
    } catch (Exception e) {
      log.error("Failed to send class code email: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(topics = "send-access-code-to-emails", groupId = "quizedu-group")
  public void sendAccessCodeToEmail(String message) {
    try {
      String[] parts = message.split(";");
      String emailsString = extractValue(parts[0]);
      String accessCode = extractValue(parts[1]);
      String quizName = extractValue(parts[2]);

      Map<String, Object> variables = new HashMap<>();
      variables.put("accessCode", accessCode);
      variables.put("quizName", quizName);
      variables.put("supportEmail", supportEmail);

      sendEmail(
          emailsString, "Access Code for Quiz: " + quizName, "access-code-email.html", variables);
    } catch (Exception e) {
      log.error("Failed to send access code email: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(topics = "send-notification-to-emails", groupId = "quizedu-group")
  public void sendNotificationToEmail(String message) {
    try {
      String[] parts = message.split(";");
      String emailsString = extractValue(parts[0]);
      String teacherName = extractValue(parts[1]);
      String classRoomName = extractValue(parts[2]);
      String notification = extractValue(parts[3]);
      String files = extractValue(parts[4]);

      // Parse file URLs
      List<String> fileUrls =
          Arrays.stream(files.replaceAll("[\\[\\]]", "").split(","))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .toList();

      // Download files from S3
      List<File> attachments = new ArrayList<>();
      for (String url : fileUrls) {
        File file = s3Service.downloadFileToTemp(url); // Implement this method in your S3 service
        if (file != null) attachments.add(file);
      }

      Map<String, Object> variables = new HashMap<>();
      variables.put("teacherName", teacherName);
      variables.put("classroomName", classRoomName);
      variables.put("notification", notification);
      variables.put("files", fileUrls); // For displaying links in the email body if needed
      variables.put("supportEmail", supportEmail);

      String subject = "Notification for class: " + classRoomName;

      sendEmailWithAttachments(
          emailsString, subject, "notification-email.html", variables, attachments);
    } catch (Exception e) {
      log.error("Failed to send notification email: {}", e.getMessage(), e);
    }
  }

  private String extractValue(String part) {
    int index = part.indexOf("=");
    return index >= 0 ? part.substring(index + 1) : "";
  }

  private void sendEmail(
      String recipients, String subject, String template, Map<String, Object> variables)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

    helper.setFrom(from, "QuizEdu Support");

    // Handle multiple recipients
    if (recipients.contains(",")) {
      helper.setTo(InternetAddress.parse(recipients));
      log.info("Sending email to multiple recipients: {}", recipients);
    } else {
      helper.setTo(recipients);
      log.info("Sending email to: {}", recipients);
    }

    helper.setSubject(subject);

    // Process template
    Context context = new Context();
    context.setVariables(variables);
    String content = springTemplateEngine.process(template, context);
    helper.setText(content, true);

    try {
      mailSender.send(mimeMessage);
      log.info("Email sent successfully with subject: {}", subject);
    } catch (Exception e) {
      log.error("Failed to send email: {}", e.getMessage());
      throw e;
    }
  }
}
