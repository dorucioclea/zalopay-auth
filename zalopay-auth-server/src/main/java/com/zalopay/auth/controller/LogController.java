package com.zalopay.auth.controller;

import com.zalopay.auth.model.Log;
import com.zalopay.auth.payload.PagedResponse;
import com.zalopay.auth.security.CurrentUser;
import com.zalopay.auth.security.JwtTokenProvider;
import com.zalopay.auth.security.UserPrincipal;
import com.zalopay.auth.service.LogService;
import com.zalopay.auth.util.AppConstants;
import org.json.JSONObject;
import org.keycloak.common.VerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private static final String KONG_LOG_REQUEST = "request";
    private static final String KONG_HEADER_REQUEST = "headers";
    private static final String KONG_URI_REQUEST = "uri";
    private static final String KONG_SERVICE_REQUEST ="service";
    private static final String KONG_QUERY_STRING ="querystring";

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private LogService logService;

    @PostMapping("/logs")
    public void insertLog(@RequestBody String kongLogRequest) throws VerificationException {
        JSONObject obj = new JSONObject(kongLogRequest);

        String upstream_uri = (String) obj.get("upstream_uri");
        if(upstream_uri.startsWith("/api")) {
            JSONObject request = (JSONObject) obj.get(KONG_LOG_REQUEST);
            String uri = (String) request.get(KONG_URI_REQUEST);
            JSONObject queryString = (JSONObject) request.get(KONG_QUERY_STRING);
            JSONObject service = (JSONObject) obj.get(KONG_SERVICE_REQUEST);
            JSONObject header = (JSONObject) request.get(KONG_HEADER_REQUEST);

            String jwt = (String) header.get("x-access-token");
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authenticationToken = tokenProvider.getUserInfoFromJWT(jwt);
                UserPrincipal userInRequest = (UserPrincipal) authenticationToken.getPrincipal();
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logService.createLog((String) service.get("name"), uri, request.toString(),
                  userInRequest.getUsername(), queryString.toString());
                logger.info("logged request:" + request.toString());
            }
        }
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Log> getLogs(@CurrentUser UserPrincipal currentUser,
                             @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_QUERY_SIZE) int page) {
        PagedResponse<Log> logs = logService.getAllLogs(currentUser, page,
          Integer.valueOf(AppConstants.DEFAULT_QUERY_SIZE));
        return logs.getList();
    }
}
