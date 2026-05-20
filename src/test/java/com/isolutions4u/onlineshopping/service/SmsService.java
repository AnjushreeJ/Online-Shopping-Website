package com.isolutions4u.onlineshopping.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SmsService {

    @Value("${fast2sms.api.key}")
    private String apiKey;

    @Value("${fast2sms.to.number}")
    private String toNumber;

    public void sendOrderConfirmation(String customerName,
                                      int totalItems,
                                      double grandTotal) {
        try {
            String message = "Hello " + customerName
                    + " Your order is confirmed."
                    + " Items " + totalItems
                    + " Total Rs " + grandTotal
                    + " Thank you Vibe Shopping";

            // ── Use POST with JSON body ──
            URL url = new URL("https://www.fast2sms.com/dev/bulkV2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authorization", apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("cache-control", "no-cache");
            conn.setDoOutput(true);

            // JSON body
            String jsonBody = "{"
                    + "\"route\": \"q\","
                    + "\"message\": \"" + message + "\","
                    + "\"language\": \"english\","
                    + "\"flash\": 0,"
                    + "\"numbers\": \"" + toNumber + "\""
                    + "}";

            System.out.println(">>> Sending SMS with body: " + jsonBody);

            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println(">>> SMS Response Code: " + responseCode);

            BufferedReader in;
            if (responseCode == 200) {
                in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(">>> SMS Response Body: " + response);

        } catch (Exception e) {
            System.err.println(">>> SMS Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}