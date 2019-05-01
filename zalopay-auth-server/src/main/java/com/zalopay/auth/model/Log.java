package com.zalopay.auth.model;

import com.zalopay.auth.model.audit.UserDateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "logs_kong")
@Getter
@Setter
public class Log extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String logBody;

    @NotBlank
    private String serviceName;

    @NotBlank
    private String requestUri;

    @NotBlank
    private String userLoggedRequest;

    private String queryString;

}
