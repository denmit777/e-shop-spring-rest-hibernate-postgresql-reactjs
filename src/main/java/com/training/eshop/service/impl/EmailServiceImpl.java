package com.training.eshop.service.impl;

import com.training.eshop.converter.OrderConverter;
import com.training.eshop.dao.FeedbackDAO;
import com.training.eshop.dao.OrderDAO;
import com.training.eshop.dao.UserDAO;
import com.training.eshop.dto.GoodBuyerDto;
import com.training.eshop.dto.OrderBuyerDto;
import com.training.eshop.model.Feedback;
import com.training.eshop.model.Order;
import com.training.eshop.model.User;
import com.training.eshop.service.EmailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:mail/email.properties")
@ComponentScan(value = "com/training/eshop")
public class EmailServiceImpl implements EmailService {

    private static final String EMPTY_STRING = "";
    private static final String HEADER = "Your";
    private static final String FOOTER = "Thanks for your choice";
    private static final String ORDER_DETAILS_SUBJECT = "Order details";
    private static final String ORDER_DETAILS_TEMPLATE = "orderDetails.html";

    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final JavaMailSender emailSender;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    private final FeedbackDAO feedbackDAO;
    private final OrderConverter orderConverter;

    @Value("${mail.username}")
    private String sendFrom;

    public EmailServiceImpl(SpringTemplateEngine thymeleafTemplateEngine, JavaMailSender emailSender,
                            UserDAO userDAO, OrderDAO orderDAO, FeedbackDAO feedbackDAO, OrderConverter orderConverter) {
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
        this.emailSender = emailSender;
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
        this.feedbackDAO = feedbackDAO;
        this.orderConverter = orderConverter;
    }

    @Override
    public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel,
                                                  String template) {
        Context thymeleafContext = new Context();

        thymeleafContext.setVariables(templateModel);

        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        sendHtmlMessage(to, subject, htmlBody);
    }

    @SneakyThrows
    private void sendHtmlMessage(String to, String subject, String htmlBody) {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(sendFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        emailSender.send(message);
    }

    @Override
    public void sendOrderDetailsMessage(Long orderId, String login) {
        List<User> admins = userDAO.getAllAdmins();

        User buyer = userDAO.getByLogin(login);

        Order order = orderDAO.getById(orderId);
        OrderBuyerDto orderBuyerDto = orderConverter.convertToOrderBuyerDto(order);

        admins.forEach(recipient -> sendMessageUsingThymeleafTemplate(recipient.getEmail(),
                ORDER_DETAILS_SUBJECT,
                getTemplateModelForOrder(orderId, orderBuyerDto, "Admins", EMPTY_STRING, EMPTY_STRING),
                ORDER_DETAILS_TEMPLATE));

        sendMessageUsingThymeleafTemplate(buyer.getEmail(),
                ORDER_DETAILS_SUBJECT,
                getTemplateModelForOrder(orderId, orderBuyerDto, buyer.getName(), HEADER, FOOTER),
                ORDER_DETAILS_TEMPLATE);
    }

    @Override
    public void sendFeedbackMessage(Long orderId, Long feedbackId, String login) {
        User buyer = userDAO.getByLogin(login);

        List<User> recipients = userDAO.getAllAdmins();

        Feedback feedback = feedbackDAO.getByFeedbackIdAndOrderId(feedbackId, orderId);

        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("orderId", orderId);
        templateModel.put("login", buyer.getName());
        templateModel.put("feedbackRate", feedback.getRate());
        templateModel.put("feedbackComment", feedback.getText());

        recipients.forEach(recipient -> sendMessageUsingThymeleafTemplate(recipient.getEmail(),
                "Feedback was provided",
                templateModel, "orderFeedback.html"));

    }

    private Map<String, Object> getTemplateModelForOrder(Long orderId, OrderBuyerDto orderBuyerDto, String login,
                                                         String header, String footer) {
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("orderId", orderId);
        templateModel.put("login", login);
        templateModel.put("order", printOrder(orderBuyerDto));
        templateModel.put("header", header);
        templateModel.put("footer", footer);

        return templateModel;
    }

    private String printOrder(OrderBuyerDto orderBuyerDto) {
        StringBuilder sb = new StringBuilder();

        int count = 1;

        for (GoodBuyerDto goodBuyerDto : orderBuyerDto.getGoods()) {
            sb.append(count)
                    .append(") ")
                    .append(goodBuyerDto.getTitle())
                    .append(" ")
                    .append(goodBuyerDto.getPrice())
                    .append(" $\n");

            count++;
        }

        sb.append("Total: $ ").append(orderBuyerDto.getTotalPrice());

        return sb.toString();
    }
}

