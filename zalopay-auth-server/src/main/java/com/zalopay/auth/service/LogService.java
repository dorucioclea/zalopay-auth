package com.zalopay.auth.service;

import com.zalopay.auth.exception.BadRequestException;
import com.zalopay.auth.model.Log;
import com.zalopay.auth.payload.PagedResponse;
import com.zalopay.auth.repository.LogRepository;
import com.zalopay.auth.security.UserPrincipal;
import com.zalopay.auth.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    public Log createLog(String serviceName, String requestURI, String logRequest, String username,
                         String requestParam) {
        Log log = new Log();
        log.setServiceName(serviceName);
        log.setRequestUri(requestURI);
        log.setLogBody(logRequest);
        log.setUserLoggedRequest(username);
        log.setQueryString(requestParam);
        return logRepository.save(log);
    }

    public PagedResponse<Log> getAllLogs(UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Log> logs = logRepository.findAll(pageable);

        if(logs.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), 0,
              0, 1);
        }

        List<Log> logsResponses = logs.map(log -> log).getContent();

        return new PagedResponse<>(logsResponses, logs.getSize(),
          logs.getSize(), 1);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
