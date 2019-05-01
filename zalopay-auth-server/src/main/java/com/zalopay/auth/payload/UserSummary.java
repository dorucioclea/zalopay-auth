package com.zalopay.auth.payload;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private Long userid;
    private String realmId;
    private String username;
    private String name;
    private String avatar = "https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png";
    private String email;
    private String title;
    private String country;
    private List<Services> services = new ArrayList<>();

    public void addService(String serviceRealmId, String serviceName, String serviceUrl, String serviceLogo,
                           List<String> serviceRoles) {
        services.add(new Services(serviceRealmId, serviceName, serviceUrl, serviceLogo, serviceRoles));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class Services {
        private String serviceRealmId;
        private String serviceName;
        private String serviceUrl;
        private String serviceLogo;
        private List<String> serviceRoles;
    }
}
