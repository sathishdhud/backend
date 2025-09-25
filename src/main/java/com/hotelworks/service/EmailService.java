package com.hotelworks.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Attachments;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

// iText PDF imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    // Hotel information
    @Value("${hotel.name:Grand Plaza Hotel}")
    private String hotelName;
    
    @Value("${hotel.address:123 Hospitality Boulevard, Resort City}")
    private String hotelAddress;
    
    @Value("${hotel.phone:+1 (555) 123-4567}")
    private String hotelPhone;
    
    @Value("${hotel.email:contact@luxuryhotels.com}")
    private String hotelEmail;

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

    public boolean sendEmailWithAttachment(String toEmail, String subject, String content, byte[] attachmentData, String attachmentName, String attachmentType) {
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

            // Add attachment if provided
            if (attachmentData != null && attachmentData.length > 0 && attachmentName != null && attachmentType != null) {
                Attachments attachment = new Attachments();
                attachment.setContent(Base64.getEncoder().encodeToString(attachmentData));
                attachment.setType(attachmentType);
                attachment.setFilename(attachmentName);
                attachment.setDisposition("attachment");
                mail.addAttachments(attachment);
            }

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
                "    <p>© 2023 " + hotelName + ". All rights reserved.</p>" +
                "    <p>" + hotelAddress + " | " + hotelEmail + " | " + hotelPhone + "</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }

    public boolean sendDetailedReservationConfirmation(String toEmail, com.hotelworks.dto.response.ReservationResponse reservation) {
        String subject = "Reservation Confirmation - " + reservation.getReservationNo();
        
        // Create detailed HTML content with all reservation information
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<!DOCTYPE html>");
        contentBuilder.append("<html>");
        contentBuilder.append("<head>");
        contentBuilder.append("<meta charset='UTF-8'>");
        contentBuilder.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        contentBuilder.append("<title>Reservation Confirmation</title>");
        contentBuilder.append("<style>");
        contentBuilder.append("  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f5f7fa; color: #333; }");
        contentBuilder.append("  .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }");
        contentBuilder.append("  .header { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 30px; text-align: center; color: white; }");
        contentBuilder.append("  .header h1 { margin: 0; font-size: 28px; font-weight: 600; }");
        contentBuilder.append("  .header p { margin: 10px 0 0; font-size: 16px; opacity: 0.9; }");
        contentBuilder.append("  .content { padding: 40px 30px; }");
        contentBuilder.append("  .greeting { font-size: 20px; margin-bottom: 20px; color: #1e3c72; }");
        contentBuilder.append("  .message { font-size: 16px; line-height: 1.6; margin-bottom: 30px; }");
        contentBuilder.append("  .details { background-color: #f8f9fa; border-left: 4px solid #2a5298; padding: 20px; border-radius: 4px; margin: 25px 0; }");
        contentBuilder.append("  .details h3 { margin-top: 0; color: #1e3c72; font-size: 18px; }");
        contentBuilder.append("  .details p { margin: 8px 0; font-size: 16px; }");
        contentBuilder.append("  .details table { width: 100%; border-collapse: collapse; margin: 15px 0; }");
        contentBuilder.append("  .details table td { padding: 8px; border-bottom: 1px solid #ddd; }");
        contentBuilder.append("  .details table td:first-child { font-weight: 600; color: #1e3c72; width: 30%; }");
        contentBuilder.append("  .highlight { font-weight: 600; color: #1e3c72; }");
        contentBuilder.append("  .footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 14px; color: #6c757d; border-top: 1px solid #e9ecef; }");
        contentBuilder.append("  .footer p { margin: 5px 0; }");
        contentBuilder.append("  .cta { display: inline-block; background-color: #2a5298; color: white; padding: 12px 24px; border-radius: 4px; text-decoration: none; font-weight: 500; margin-top: 20px; }");
        contentBuilder.append("  @media only screen and (max-width: 600px) {");
        contentBuilder.append("    .container { width: 100%; }");
        contentBuilder.append("    .header { padding: 20px; }");
        contentBuilder.append("    .content { padding: 20px 15px; }");
        contentBuilder.append("  }");
        contentBuilder.append("</style>");
        contentBuilder.append("</head>");
        contentBuilder.append("<body>");
        contentBuilder.append("<div class='container'>");
        contentBuilder.append("  <div class='header'>");
        contentBuilder.append("    <h1>Reservation Confirmed</h1>");
        contentBuilder.append("    <p>Thank you for choosing our hotel</p>");
        contentBuilder.append("  </div>");
        contentBuilder.append("  <div class='content'>");
        contentBuilder.append("    <p class='greeting'>Dear " + reservation.getGuestName() + ",</p>");
        contentBuilder.append("    <p class='message'>We're delighted to confirm your reservation. Your booking details are below:</p>");
        contentBuilder.append("    <div class='details'>");
        contentBuilder.append("      <h3>Reservation Details</h3>");
        contentBuilder.append("      <table>");
        contentBuilder.append("        <tr><td>Reservation Number:</td><td>" + reservation.getReservationNo() + "</td></tr>");
        contentBuilder.append("        <tr><td>Guest Name:</td><td>" + reservation.getGuestName() + "</td></tr>");
        contentBuilder.append("        <tr><td>Arrival Date:</td><td>" + reservation.getArrivalDate() + "</td></tr>");
        contentBuilder.append("        <tr><td>Departure Date:</td><td>" + reservation.getDepartureDate() + "</td></tr>");
        contentBuilder.append("        <tr><td>Number of Days:</td><td>" + reservation.getNoOfDays() + "</td></tr>");
        contentBuilder.append("        <tr><td>Number of Persons:</td><td>" + reservation.getNoOfPersons() + "</td></tr>");
        contentBuilder.append("        <tr><td>Number of Rooms:</td><td>" + reservation.getNoOfRooms() + "</td></tr>");
        contentBuilder.append("        <tr><td>Rate:</td><td>" + (reservation.getRate() != null ? reservation.getRate() : "N/A") + "</td></tr>");
        contentBuilder.append("        <tr><td>Including GST:</td><td>" + (reservation.getIncludingGst() != null ? reservation.getIncludingGst() : "N/A") + "</td></tr>");
        contentBuilder.append("        <tr><td>Mobile Number:</td><td>" + reservation.getMobileNumber() + "</td></tr>");
        contentBuilder.append("        <tr><td>Email:</td><td>" + (reservation.getEmailId() != null ? reservation.getEmailId() : "N/A") + "</td></tr>");
        
        if (reservation.getCompanyName() != null && !reservation.getCompanyName().isEmpty()) {
            contentBuilder.append("        <tr><td>Company:</td><td>" + reservation.getCompanyName() + "</td></tr>");
        }
        
        if (reservation.getPlanName() != null && !reservation.getPlanName().isEmpty()) {
            contentBuilder.append("        <tr><td>Plan:</td><td>" + reservation.getPlanName() + "</td></tr>");
        }
        
        if (reservation.getRoomTypeName() != null && !reservation.getRoomTypeName().isEmpty()) {
            contentBuilder.append("        <tr><td>Room Type:</td><td>" + reservation.getRoomTypeName() + "</td></tr>");
        }
        
        if (reservation.getSettlementTypeName() != null && !reservation.getSettlementTypeName().isEmpty()) {
            contentBuilder.append("        <tr><td>Settlement Type:</td><td>" + reservation.getSettlementTypeName() + "</td></tr>");
        }
        
        if (reservation.getArrivalModeName() != null && !reservation.getArrivalModeName().isEmpty()) {
            contentBuilder.append("        <tr><td>Arrival Mode:</td><td>" + reservation.getArrivalModeName() + "</td></tr>");
        }
        
        if (reservation.getArrivalDetails() != null && !reservation.getArrivalDetails().isEmpty()) {
            contentBuilder.append("        <tr><td>Arrival Details:</td><td>" + reservation.getArrivalDetails() + "</td></tr>");
        }
        
        if (reservation.getNationalityName() != null && !reservation.getNationalityName().isEmpty()) {
            contentBuilder.append("        <tr><td>Nationality:</td><td>" + reservation.getNationalityName() + "</td></tr>");
        }
        
        if (reservation.getRefModeName() != null && !reservation.getRefModeName().isEmpty()) {
            contentBuilder.append("        <tr><td>Reference Mode:</td><td>" + reservation.getRefModeName() + "</td></tr>");
        }
        
        if (reservation.getResvSourceName() != null && !reservation.getResvSourceName().isEmpty()) {
            contentBuilder.append("        <tr><td>Reservation Source:</td><td>" + reservation.getResvSourceName() + "</td></tr>");
        }
        
        if (reservation.getRemarks() != null && !reservation.getRemarks().isEmpty()) {
            contentBuilder.append("        <tr><td>Remarks:</td><td>" + reservation.getRemarks() + "</td></tr>");
        }
        
        contentBuilder.append("      </table>");
        contentBuilder.append("    </div>");
        contentBuilder.append("    <p class='message'>We look forward to providing you with an exceptional experience during your stay. Should you have any questions or special requests, please don't hesitate to contact our concierge team.</p>");
        contentBuilder.append("    <a href='#' class='cta'>Manage Your Reservation</a>");
        contentBuilder.append("  </div>");
        contentBuilder.append("  <div class='footer'>");
        contentBuilder.append("    <p>© 2023 " + hotelName + ". All rights reserved.</p>");
        contentBuilder.append("    <p>" + hotelAddress + " | " + hotelEmail + " | " + hotelPhone + "</p>");
        contentBuilder.append("  </div>");
        contentBuilder.append("</div>");
        contentBuilder.append("</body>");
        contentBuilder.append("</html>");

        // Generate PDF attachment content
        byte[] attachmentData = generateReservationPDFAttachment(reservation);
        
        return sendEmailWithAttachment(toEmail, subject, contentBuilder.toString(), attachmentData, 
                                     "Reservation_" + reservation.getReservationNo() + ".pdf", "application/pdf");
    }

    private byte[] generateReservationPDFAttachment(com.hotelworks.dto.response.ReservationResponse reservation) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Initialize PDF document
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Add hotel header
            document.add(new Paragraph(hotelName)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());
            
            document.add(new Paragraph(hotelAddress)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));
            
            document.add(new Paragraph(hotelPhone + " | " + hotelEmail)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));
            
            // Add title
            document.add(new Paragraph("RESERVATION CONFIRMATION")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setBold()
                .setMarginTop(20));
            
            document.add(new Paragraph("Reservation Number: " + reservation.getReservationNo())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold()
                .setMarginBottom(20));
            
            // Create table for reservation details
            float[] columnWidths = {2, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Add reservation details to table
            table.addHeaderCell(new Cell().add(new Paragraph("Field").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Value").setBold()));
            
            table.addCell("Reservation Number");
            table.addCell(reservation.getReservationNo());
            
            table.addCell("Guest Name");
            table.addCell(reservation.getGuestName());
            
            table.addCell("Arrival Date");
            table.addCell(reservation.getArrivalDate().toString());
            
            table.addCell("Departure Date");
            table.addCell(reservation.getDepartureDate().toString());
            
            table.addCell("Number of Days");
            table.addCell(String.valueOf(reservation.getNoOfDays()));
            
            table.addCell("Number of Persons");
            table.addCell(String.valueOf(reservation.getNoOfPersons()));
            
            table.addCell("Number of Rooms");
            table.addCell(String.valueOf(reservation.getNoOfRooms()));
            
            table.addCell("Rate");
            table.addCell(reservation.getRate() != null ? reservation.getRate().toString() : "N/A");
            
            table.addCell("Including GST");
            table.addCell(reservation.getIncludingGst() != null ? reservation.getIncludingGst() : "N/A");
            
            table.addCell("Mobile Number");
            table.addCell(reservation.getMobileNumber());
            
            table.addCell("Email");
            table.addCell(reservation.getEmailId() != null ? reservation.getEmailId() : "N/A");
            
            if (reservation.getCompanyName() != null && !reservation.getCompanyName().isEmpty()) {
                table.addCell("Company");
                table.addCell(reservation.getCompanyName());
            }
            
            if (reservation.getPlanName() != null && !reservation.getPlanName().isEmpty()) {
                table.addCell("Plan");
                table.addCell(reservation.getPlanName());
            }
            
            if (reservation.getRoomTypeName() != null && !reservation.getRoomTypeName().isEmpty()) {
                table.addCell("Room Type");
                table.addCell(reservation.getRoomTypeName());
            }
            
            if (reservation.getSettlementTypeName() != null && !reservation.getSettlementTypeName().isEmpty()) {
                table.addCell("Settlement Type");
                table.addCell(reservation.getSettlementTypeName());
            }
            
            if (reservation.getArrivalModeName() != null && !reservation.getArrivalModeName().isEmpty()) {
                table.addCell("Arrival Mode");
                table.addCell(reservation.getArrivalModeName());
            }
            
            if (reservation.getArrivalDetails() != null && !reservation.getArrivalDetails().isEmpty()) {
                table.addCell("Arrival Details");
                table.addCell(reservation.getArrivalDetails());
            }
            
            if (reservation.getNationalityName() != null && !reservation.getNationalityName().isEmpty()) {
                table.addCell("Nationality");
                table.addCell(reservation.getNationalityName());
            }
            
            if (reservation.getRefModeName() != null && !reservation.getRefModeName().isEmpty()) {
                table.addCell("Reference Mode");
                table.addCell(reservation.getRefModeName());
            }
            
            if (reservation.getResvSourceName() != null && !reservation.getResvSourceName().isEmpty()) {
                table.addCell("Reservation Source");
                table.addCell(reservation.getResvSourceName());
            }
            
            if (reservation.getRemarks() != null && !reservation.getRemarks().isEmpty()) {
                table.addCell("Remarks");
                table.addCell(reservation.getRemarks());
            }
            
            document.add(table);
            
            // Add footer
            document.add(new Paragraph("Thank you for choosing " + hotelName + ". We look forward to providing you with an exceptional experience during your stay.")
                .setMarginTop(20)
                .setTextAlignment(TextAlignment.CENTER));
            
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            // Return a simple text version if PDF generation fails
            StringBuilder textContent = new StringBuilder();
            textContent.append(hotelName).append("\n");
            textContent.append(hotelAddress).append("\n");
            textContent.append(hotelPhone).append(" | ").append(hotelEmail).append("\n\n");
            textContent.append("========================================\n");
            textContent.append("         RESERVATION CONFIRMATION\n");
            textContent.append("========================================\n\n");
            textContent.append("Reservation Number: ").append(reservation.getReservationNo()).append("\n");
            textContent.append("Guest Name: ").append(reservation.getGuestName()).append("\n");
            textContent.append("Arrival Date: ").append(reservation.getArrivalDate()).append("\n");
            textContent.append("Departure Date: ").append(reservation.getDepartureDate()).append("\n");
            textContent.append("Number of Days: ").append(reservation.getNoOfDays()).append("\n");
            textContent.append("Number of Persons: ").append(reservation.getNoOfPersons()).append("\n");
            textContent.append("Number of Rooms: ").append(reservation.getNoOfRooms()).append("\n");
            textContent.append("Rate: ").append(reservation.getRate() != null ? reservation.getRate() : "N/A").append("\n");
            textContent.append("Including GST: ").append(reservation.getIncludingGst() != null ? reservation.getIncludingGst() : "N/A").append("\n");
            textContent.append("Mobile Number: ").append(reservation.getMobileNumber()).append("\n");
            textContent.append("Email: ").append(reservation.getEmailId() != null ? reservation.getEmailId() : "N/A").append("\n");
            
            if (reservation.getCompanyName() != null && !reservation.getCompanyName().isEmpty()) {
                textContent.append("Company: ").append(reservation.getCompanyName()).append("\n");
            }
            
            if (reservation.getPlanName() != null && !reservation.getPlanName().isEmpty()) {
                textContent.append("Plan: ").append(reservation.getPlanName()).append("\n");
            }
            
            if (reservation.getRoomTypeName() != null && !reservation.getRoomTypeName().isEmpty()) {
                textContent.append("Room Type: ").append(reservation.getRoomTypeName()).append("\n");
            }
            
            if (reservation.getSettlementTypeName() != null && !reservation.getSettlementTypeName().isEmpty()) {
                textContent.append("Settlement Type: ").append(reservation.getSettlementTypeName()).append("\n");
            }
            
            if (reservation.getArrivalModeName() != null && !reservation.getArrivalModeName().isEmpty()) {
                textContent.append("Arrival Mode: ").append(reservation.getArrivalModeName()).append("\n");
            }
            
            if (reservation.getArrivalDetails() != null && !reservation.getArrivalDetails().isEmpty()) {
                textContent.append("Arrival Details: ").append(reservation.getArrivalDetails()).append("\n");
            }
            
            if (reservation.getNationalityName() != null && !reservation.getNationalityName().isEmpty()) {
                textContent.append("Nationality: ").append(reservation.getNationalityName()).append("\n");
            }
            
            if (reservation.getRefModeName() != null && !reservation.getRefModeName().isEmpty()) {
                textContent.append("Reference Mode: ").append(reservation.getRefModeName()).append("\n");
            }
            
            if (reservation.getResvSourceName() != null && !reservation.getResvSourceName().isEmpty()) {
                textContent.append("Reservation Source: ").append(reservation.getResvSourceName()).append("\n");
            }
            
            if (reservation.getRemarks() != null && !reservation.getRemarks().isEmpty()) {
                textContent.append("Remarks: ").append(reservation.getRemarks()).append("\n");
            }
            
            return textContent.toString().getBytes();
        }
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
                "    <p>© 2023 " + hotelName + ". All rights reserved.</p>" +
                "    <p>" + hotelAddress + " | " + hotelEmail + " | " + hotelPhone + "</p>" +
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
                "    <p>© 2023 " + hotelName + ". All rights reserved.</p>" +
                "    <p>" + hotelAddress + " | " + hotelEmail + " | " + hotelPhone + "</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendEmail(toEmail, subject, content);
    }
}