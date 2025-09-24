package com.hotelworks.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    private final SendGrid sendGrid;

    public EmailService(@Value("${sendgrid.api.key}") String sendGridApiKey) {
        this.sendGrid = new SendGrid(sendGridApiKey);
        System.out.println("SendGrid initialized with API key: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
    }
    
    public void logConfiguration() {
        System.out.println("SendGrid from email: " + fromEmail);
        System.out.println("SendGrid API key present: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
    }

    public boolean sendEmail(String toEmail, String subject, String content) {
        if (fromEmail == null) {
            logConfiguration();
        }
        
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Email not sent: Recipient email is null or empty");
            return false;
        }

        if (subject == null || subject.isEmpty()) {
            System.err.println("Email not sent: Subject is null or empty");
            return false;
        }

        if (content == null || content.isEmpty()) {
            System.err.println("Email not sent: Content is null or empty");
            return false;
        }
        
        String actualFromEmail = (fromEmail != null && !fromEmail.isEmpty()) ? fromEmail : "sathishdhuda25@gmail.com";

        try {
            Email from = new Email(actualFromEmail);
            Email to = new Email(toEmail);
            Content emailContent = new Content("text/html", content);
            Mail mail = new Mail(from, subject, to, emailContent);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            System.out.println("Sending email to: " + toEmail + " with subject: " + subject);
            System.out.println("From email: " + actualFromEmail);
            
            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("Email sent successfully to: " + toEmail);
                System.out.println("Response status: " + response.getStatusCode());
                return true;
            } else {
                System.err.println("Failed to send email. Status code: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                System.err.println("Response headers: " + response.getHeaders());
                return false;
            }
        } catch (IOException ex) {
            System.err.println("Error sending email to " + toEmail + ": " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            System.err.println("Unexpected error sending email to " + toEmail + ": " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public boolean sendReservationConfirmation(String toEmail, String guestName, String reservationNo) {
        String subject = "Reservation Confirmation - " + reservationNo;
        String content = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Reservation Confirmation</title>" +
                "<style>" +
                "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f5f7fa; color: #333; }" +
                "  .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "  .header { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 30px; text-align: center; color: white; }" +
                "  .header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
                "  .header p { margin: 10px 0 0; font-size: 16px; opacity: 0.9; }" +
                "  .content { padding: 40px 30px; }" +
                "  .greeting { font-size: 20px; margin-bottom: 20px; color: #1e3c72; }" +
                "  .message { font-size: 16px; line-height: 1.6; margin-bottom: 30px; }" +
                "  .details { background-color: #f8f9fa; border-left: 4px solid #2a5298; padding: 20px; border-radius: 4px; margin: 25px 0; }" +
                "  .details h3 { margin-top: 0; color: #1e3c72; font-size: 18px; }" +
                "  .details p { margin: 8px 0; font-size: 16px; }" +
                "  .highlight { font-weight: 600; color: #1e3c72; }" +
                "  .footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 14px; color: #6c757d; border-top: 1px solid #e9ecef; }" +
                "  .footer p { margin: 5px 0; }" +
                "  .cta { display: inline-block; background-color: #2a5298; color: white; padding: 12px 24px; border-radius: 4px; text-decoration: none; font-weight: 500; margin-top: 20px; }" +
                "  @media only screen and (max-width: 600px) {" +
                "    .container { width: 100%; }" +
                "    .header { padding: 20px; }" +
                "    .content { padding: 20px 15px; }" +
                "  }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1>Reservation Confirmed</h1>" +
                "    <p>Thank you for choosing our hotel</p>" +
                "  </div>" +
                "  <div class='content'>" +
                "    <p class='greeting'>Dear " + guestName + ",</p>" +
                "    <p class='message'>We're delighted to confirm your reservation. Your booking details are below:</p>" +
                "    <div class='details'>" +
                "      <h3>Reservation Details</h3>" +
                "      <p><span class='highlight'>Reservation Number:</span> " + reservationNo + "</p>" +
                "    </div>" +
                "    <p class='message'>We look forward to providing you with an exceptional experience during your stay. Should you have any questions or special requests, please don't hesitate to contact our concierge team.</p>" +
                "    <a href='#' class='cta'>Manage Your Reservation</a>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>© 2023 Luxury Hotels International. All rights reserved.</p>" +
                "    <p>123 Hospitality Boulevard, Resort City | contact@luxuryhotels.com | +1 (555) 123-4567</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendCheckInConfirmation(String toEmail, String guestName, String folioNo) {
        String subject = "Check-In Confirmation - " + folioNo;
        String content = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Check-In Confirmation</title>" +
                "<style>" +
                "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f5f7fa; color: #333; }" +
                "  .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "  .header { background: linear-gradient(135deg, #2c7873, #53a8b6); padding: 30px; text-align: center; color: white; }" +
                "  .header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
                "  .header p { margin: 10px 0 0; font-size: 16px; opacity: 0.9; }" +
                "  .content { padding: 40px 30px; }" +
                "  .greeting { font-size: 20px; margin-bottom: 20px; color: #2c7873; }" +
                "  .message { font-size: 16px; line-height: 1.6; margin-bottom: 30px; }" +
                "  .details { background-color: #f0f7f7; border-left: 4px solid #2c7873; padding: 20px; border-radius: 4px; margin: 25px 0; }" +
                "  .details h3 { margin-top: 0; color: #2c7873; font-size: 18px; }" +
                "  .details p { margin: 8px 0; font-size: 16px; }" +
                "  .highlight { font-weight: 600; color: #2c7873; }" +
                "  .footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 14px; color: #6c757d; border-top: 1px solid #e9ecef; }" +
                "  .footer p { margin: 5px 0; }" +
                "  .cta { display: inline-block; background-color: #2c7873; color: white; padding: 12px 24px; border-radius: 4px; text-decoration: none; font-weight: 500; margin-top: 20px; }" +
                "  @media only screen and (max-width: 600px) {" +
                "    .container { width: 100%; }" +
                "    .header { padding: 20px; }" +
                "    .content { padding: 20px 15px; }" +
                "  }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1>Welcome to Our Hotel</h1>" +
                "    <p>Your check-in is complete</p>" +
                "  </div>" +
                "  <div class='content'>" +
                "    <p class='greeting'>Dear " + guestName + ",</p>" +
                "    <p class='message'>We're pleased to inform you that your check-in process has been completed successfully. Your accommodation details are below:</p>" +
                "    <div class='details'>" +
                "      <h3>Check-In Details</h3>" +
                "      <p><span class='highlight'>Folio Number:</span> " + folioNo + "</p>" +
                "    </div>" +
                "    <p class='message'>Our team is dedicated to making your stay memorable. If you need anything during your visit, simply dial '0' from your room phone to reach our guest services.</p>" +
                "    <a href='#' class='cta'>Explore Hotel Services</a>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>© 2023 Luxury Hotels International. All rights reserved.</p>" +
                "    <p>123 Hospitality Boulevard, Resort City | contact@luxuryhotels.com | +1 (555) 123-4567</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendBillConfirmation(String toEmail, String guestName, String billNo, String amount) {
        String subject = "Bill Confirmation - " + billNo;
        String content = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Bill Confirmation</title>" +
                "<style>" +
                "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f5f7fa; color: #333; }" +
                "  .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                "  .header { background: linear-gradient(135deg, #8e44ad, #9b59b6); padding: 30px; text-align: center; color: white; }" +
                "  .header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
                "  .header p { margin: 10px 0 0; font-size: 16px; opacity: 0.9; }" +
                "  .content { padding: 40px 30px; }" +
                "  .greeting { font-size: 20px; margin-bottom: 20px; color: #8e44ad; }" +
                "  .message { font-size: 16px; line-height: 1.6; margin-bottom: 30px; }" +
                "  .details { background-color: #f5f0f7; border-left: 4px solid #8e44ad; padding: 20px; border-radius: 4px; margin: 25px 0; }" +
                "  .details h3 { margin-top: 0; color: #8e44ad; font-size: 18px; }" +
                "  .details p { margin: 8px 0; font-size: 16px; }" +
                "  .highlight { font-weight: 600; color: #8e44ad; }" +
                "  .footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 14px; color: #6c757d; border-top: 1px solid #e9ecef; }" +
                "  .footer p { margin: 5px 0; }" +
                "  .cta { display: inline-block; background-color: #8e44ad; color: white; padding: 12px 24px; border-radius: 4px; text-decoration: none; font-weight: 500; margin-top: 20px; }" +
                "  @media only screen and (max-width: 600px) {" +
                "    .container { width: 100%; }" +
                "    .header { padding: 20px; }" +
                "    .content { padding: 20px 15px; }" +
                "  }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1>Bill Summary</h1>" +
                "    <p>Thank you for your stay</p>" +
                "  </div>" +
                "  <div class='content'>" +
                "    <p class='greeting'>Dear " + guestName + ",</p>" +
                "    <p class='message'>Your final bill has been generated. Please find the details of your charges below:</p>" +
                "    <div class='details'>" +
                "      <h3>Bill Details</h3>" +
                "      <p><span class='highlight'>Bill Number:</span> " + billNo + "</p>" +
                "      <p><span class='highlight'>Total Amount:</span> " + amount + "</p>" +
                "    </div>" +
                "    <p class='message'>Payment has been processed using the payment method on file. A detailed receipt is attached to this email for your records. We hope you enjoyed your stay and look forward to welcoming you back soon.</p>" +
                "    <a href='#' class='cta'>Download Detailed Receipt</a>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>© 2023 Luxury Hotels International. All rights reserved.</p>" +
                "    <p>123 Hospitality Boulevard, Resort City | contact@luxuryhotels.com | +1 (555) 123-4567</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }
}