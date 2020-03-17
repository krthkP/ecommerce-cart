package com.ecommerce.ecommercecart.API;

import com.ecommerce.ecommercecart.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ProductAPI{

    final private static String STR_URL = "http://localhost:9000/products/";

    public Product getProductByID(int itemId) throws IOException {

        HttpURLConnection httpClient = (HttpURLConnection) new URL(STR_URL+itemId).openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        return (new ObjectMapper()).readValue(response.toString(), Product.class);
    }
}