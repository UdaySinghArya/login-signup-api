package com.example.registerapp.service;

import com.example.registerapp.entity.Details;
import com.example.registerapp.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    public boolean send(Details details) {
        try {
            validateDetails(details);
            userDetailRepository.save(details);
            return true;
        } catch (MissingDetailsException e) {
            // Handle the exception with a custom error message
            System.out.println("Custom error message: " + e.getMessage());
            return false;
        }
    }

    private void validateDetails(Details details) throws MissingDetailsException {
        if (!isDetailsValid(details)) {
            throw new MissingDetailsException("Please provide all details");
        }
    }

    private boolean isDetailsValid(Details details) {
        return details.getDurationFrom() != null && !details.getDurationFrom().isEmpty() &&
                details.getDurationTo() != null &&
                !details.getDurationTo().isEmpty() &&
                details.getType() != null &&
                !details.getType().isEmpty() &&
                details.getProject() != null &&
                !details.getProject().isEmpty() &&
                details.getDescription() != null &&
                !details.getDescription().isEmpty();
    }

}

class MissingDetailsException extends RuntimeException {
    public MissingDetailsException(String message) {
        super(message);
    }
}

