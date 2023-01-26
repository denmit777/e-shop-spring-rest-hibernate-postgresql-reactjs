package com.training.eshop.service.impl;

import com.training.eshop.converter.FeedbackConverter;
import com.training.eshop.dao.FeedbackDAO;
import com.training.eshop.dao.UserDAO;
import com.training.eshop.dto.FeedbackDto;
import com.training.eshop.exception.AccessDeniedException;
import com.training.eshop.model.Feedback;
import com.training.eshop.model.User;
import com.training.eshop.model.enums.Role;
import com.training.eshop.service.EmailService;
import com.training.eshop.service.FeedbackService;
import com.training.eshop.service.HistoryService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private static final Logger LOGGER = LogManager.getLogger(FeedbackServiceImpl.class.getName());

    private static final String ACCESS_DENIED_FOR_ADMIN = "Access is allowed only for buyer";

    private final FeedbackDAO feedbackDAO;
    private final FeedbackConverter feedbackConverter;
    private final UserDAO userDAO;
    private final HistoryService historyService;
    private final EmailService emailService;

    @Override
    @Transactional
    public Feedback save(FeedbackDto feedbackDto, Long orderId, String login) {
        Feedback feedback = feedbackConverter.fromFeedbackDto(feedbackDto, orderId);

        User user = userDAO.getByLogin(login);

        checkAccess(user);

        feedbackDAO.save(feedback);

        historyService.saveHistoryForLeftFeedback(feedback, orderId);

        emailService.sendFeedbackMessage(orderId, feedback.getId(), login);

        LOGGER.info("New feedback has just been added to order {}: rate - {}", orderId, feedback.getRate());

        return feedback;
    }

    @Override
    @Transactional
    public List<FeedbackDto> getAllByOrderId(Long orderId, String buttonValue) {
        List<Feedback> feedbacks;

        if (buttonValue.equals("Show All")) {
            feedbacks = feedbackDAO.getAllByOrderId(orderId);

            LOGGER.info("All feedbacks for order {}: {}", orderId, feedbacks);
        } else {
            feedbacks = feedbackDAO.getLastFiveByOrderId(orderId);

            LOGGER.info("Last 5 feedbacks for order {}: {}", orderId, feedbacks);
        }

        return feedbacks.stream()
                .map(feedbackConverter::convertToFeedbackDto)
                .collect(Collectors.toList());
    }

    private void checkAccess(User user) {
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            LOGGER.error(ACCESS_DENIED_FOR_ADMIN);

            throw new AccessDeniedException(ACCESS_DENIED_FOR_ADMIN);
        }
    }
}
