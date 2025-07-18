package com.bimalsha.ee.bank.customer.servlet;

import com.bimalsha.ee.bank.customer.ejb.remote.TransactionHistoryLoader;
import com.bimalsha.ee.bank.entity.Account;
import com.bimalsha.ee.bank.entity.TransactionHistory;
import com.bimalsha.ee.bank.entity.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/downloadStatement")
public class StatementServlet extends HttpServlet {

    @EJB
    private TransactionHistoryLoader transactionHistoryLoader;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get account number from request parameter
        String accountNumber = request.getParameter("accountNumber");

        if (accountNumber == null || accountNumber.isEmpty()) {
            // If no account specified, use the first account
            List<Account> accounts = (List<Account>) session.getAttribute("userAccounts");
            if (accounts != null && !accounts.isEmpty()) {
                accountNumber = accounts.get(0).getAccountNumber();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No account available");
                return;
            }
        }

        try {
            // Prepare PDF document
            response.setContentType("application/pdf");
            String fileName = "BankStatement_" + accountNumber + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Create PDF document
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add content to PDF
            addStatementHeader(document, user);
            addAccountDetails(document, accountNumber);
            addTransactionTable(document, accountNumber, user.getId());
            addFooter(document);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating statement");
        }
    }

    private void addStatementHeader(Document document, User user) throws DocumentException {
        // Add bank logo and header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        Paragraph header = new Paragraph("NATIONAL BANK", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph subHeader = new Paragraph("Account Statement",
                new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, BaseColor.DARK_GRAY));
        subHeader.setAlignment(Element.ALIGN_CENTER);
        document.add(subHeader);

        document.add(Chunk.NEWLINE);

        // Add customer details
        Paragraph customerInfo = new Paragraph();
        customerInfo.add(new Chunk("Customer Name: ", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        customerInfo.add(new Chunk(user.getName(), normalFont));
        document.add(customerInfo);

        Paragraph customerEmail = new Paragraph();
        customerEmail.add(new Chunk("Email: ", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        customerEmail.add(new Chunk(user.getEmail(), normalFont));
        document.add(customerEmail);

        Paragraph statementDate = new Paragraph();
        statementDate.add(new Chunk("Statement Date: ", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        statementDate.add(new Chunk(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), normalFont));
        document.add(statementDate);

        document.add(Chunk.NEWLINE);
    }

    private void addAccountDetails(Document document, String accountNumber) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

        Paragraph accountHeader = new Paragraph("Account Details", sectionFont);
        document.add(accountHeader);

        document.add(Chunk.NEWLINE);

        PdfPTable accountTable = new PdfPTable(2);
        accountTable.setWidthPercentage(100);

        accountTable.addCell(createCell("Account Number", true));
        accountTable.addCell(createCell(accountNumber, false));

        document.add(accountTable);
        document.add(Chunk.NEWLINE);
    }

    private void addTransactionTable(Document document, String accountNumber, Integer userId)
            throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);

        Paragraph transactionHeader = new Paragraph("Transaction History", sectionFont);
        document.add(transactionHeader);

        document.add(Chunk.NEWLINE);

        // Create transaction table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        // Set column widths
        float[] columnWidths = {3f, 2f, 1.5f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        // Add table headers
        table.addCell(createCell("Description", true));
        table.addCell(createCell("Date", true));
        table.addCell(createCell("Type", true));
        table.addCell(createCell("Amount", true));
        table.addCell(createCell("Status", true));

        // Get transactions from database
        List<TransactionHistory> transactions = transactionHistoryLoader.loadAccountTransactions(accountNumber);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (transactions.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("No transactions found"));
            emptyCell.setColspan(5);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(10);
            table.addCell(emptyCell);
        } else {
            for (TransactionHistory transaction : transactions) {
                boolean isOutgoing = transaction.getFromAccount().getUser() != null &&
                        transaction.getFromAccount().getUser().getId().equals(userId);

                // Description cell
                String otherParty = isOutgoing ?
                        (transaction.getToAccount().getUser() != null ?
                                transaction.getToAccount().getUser().getName() :
                                "Account #" + transaction.getToAccount().getAccountNumber()) :
                        (transaction.getFromAccount().getUser() != null ?
                                transaction.getFromAccount().getUser().getName() :
                                "Account #" + transaction.getFromAccount().getAccountNumber());

                String description = (isOutgoing ? "To: " : "From: ") + otherParty + "\n" +
                        "From: " + transaction.getFromAccount().getAccountNumber() + "\n" +
                        "To: " + transaction.getToAccount().getAccountNumber();

                if (transaction.getReason() != null && !transaction.getReason().isEmpty()) {
                    description += "\nReason: " + transaction.getReason();
                }

                table.addCell(createCell(description, false));

                // Date cell
                table.addCell(createCell(dateFormat.format(transaction.getDateTime()), false));

                // Type cell
                table.addCell(createCell("Transfer", false));

                // Amount cell with color coding
                BaseColor amountColor = isOutgoing ? BaseColor.RED : new BaseColor(0, 128, 0); // Green
                String amountStr = String.format("%s LKR %.2f", isOutgoing ? "-" : "+", transaction.getAmount());
                table.addCell(createCellWithColor(amountStr, false, amountColor));

                // Status cell
                table.addCell(createCell("Completed", false));
            }
        }

        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Paragraph footer = new Paragraph("This is an electronic statement and does not require a signature.",
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph contactInfo = new Paragraph("For any queries, please contact our customer service at support@nationalbank.com",
                new Font(Font.FontFamily.HELVETICA, 8));
        contactInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(contactInfo);
    }

    private PdfPCell createCell(String content, boolean isHeader) {
        Font font = isHeader ?
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD) :
                new Font(Font.FontFamily.HELVETICA, 9);

        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);

        if (isHeader) {
            cell.setBackgroundColor(new BaseColor(240, 240, 240));
        }

        return cell;
    }

    private PdfPCell createCellWithColor(String content, boolean isHeader, BaseColor color) {
        Font font = isHeader ?
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, color) :
                new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, color);

        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);

        if (isHeader) {
            cell.setBackgroundColor(new BaseColor(240, 240, 240));
        }

        return cell;
    }
}