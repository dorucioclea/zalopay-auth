package com.zalopay.auth.service;

import com.zalopay.auth.payload.Client.ClientPayload;
import com.zalopay.auth.repository.KongRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KongService {

    @Autowired
    private KongRepository kongRepository;

    private static final Logger logger = LoggerFactory.getLogger(KongService.class);

    public void createApi(ClientPayload client) {
        kongRepository.createService(client);
    }

    public void updateApi(ClientPayload client) {
        kongRepository.updateService(client);
    }

    public JSONArray getRoutes() {
        JSONObject routes = kongRepository.getKong("routes");
        if(routes != null) {
            return kongRepository.getKong("routes").getJSONArray("data");
        } else {
            return null;
        }
    }

    public JSONArray getPlugins() {
        JSONObject plugins = kongRepository.getKong("plugins");
        if(plugins != null) {
            return kongRepository.getKong("plugins").getJSONArray("data");
        } else {
            return null;
        }
    }

    public JSONArray getServices() {
        JSONObject routes = kongRepository.getKong("services");;
        if(routes != null) {
            return kongRepository.getKong("services").getJSONArray("data");
        } else {
            return null;
        }
    }

    public void deleteService(ClientPayload client) {
        if (!StringUtils.isEmpty(client.getRouteId()) &&
            !StringUtils.isEmpty(client.getServiceId())) {
            kongRepository.deleteService(client);
        }
    }
}
