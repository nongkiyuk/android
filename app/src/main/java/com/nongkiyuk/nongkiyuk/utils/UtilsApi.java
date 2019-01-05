package com.nongkiyuk.nongkiyuk.utils;

import com.nongkiyuk.nongkiyuk.network.ApiClient;
import com.nongkiyuk.nongkiyuk.network.ApiInterface;

public class UtilsApi {

    // BASE API URL
    public static final String BASE_URL = "https://nongkiyuk.ajikamaludin.id/api/";
    public static final String API_VERSION = "v1";
    public static final String BASE_URL_API = BASE_URL + API_VERSION + "/";

    // Deklarasi Interface ApiInterface
    public static ApiInterface getApiInterface() {
        return ApiClient.getClient(BASE_URL_API).create(ApiInterface.class);
    }

}
