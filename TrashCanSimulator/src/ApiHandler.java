import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiHandler {
    private String url;

    public ApiHandler(String url) {
        this.url = url;
    }

    public String getRequest(String location) {
        try {
            URL url = new URL(this.url + location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int response = conn.getResponseCode();
            String inline = "";
            if (response != 200) {
                System.out.println("Connection error");
            } else {
                Scanner scan = new Scanner(url.openStream());

                while (scan.hasNext()) {
                    inline += scan.nextLine();
                }
                scan.close();
            }
            conn.disconnect();
            return inline;
        }
        catch (MalformedURLException e) {
            System.out.println("urle");
        }
        catch (IOException IOe) {
            System.out.println("Connection unsuccessful");
        }
        return "";
    }

    public String postRequest(String location, String json) {
        try {
            URL url = new URL(this.url + location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                conn.disconnect();
                return response.toString();
            }
        } catch (MalformedURLException e) {
            System.out.println("urle");
        } catch (IOException IOe) {
            System.out.println("ioe");
        }
        return "";
    }

    public String putRequest(String location, String json) {
        try {
            URL url = new URL(this.url + location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                conn.disconnect();
                return response.toString();
            }
        } catch (MalformedURLException e) {
            System.out.println("urle");
        } catch (IOException IOe) {
            System.out.println("ioe");
        }
        return "";
    }

    public String deleteRequest(String location){
        String response = "";
        try {
            URL url = new URL(this.url + location);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty("Content-Type", "application/json" );
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            response = String.valueOf(httpCon.getResponseCode());
            System.out.println();
            //TODO miesto vratenia hodnoty vyparsovat JSON a vratit ten!
        } catch (MalformedURLException e) {
            System.out.println("urle");
        } catch (IOException IOe) {
            System.out.println("ioe");
        }
        return response;
    }

    public ArrayList<JsonObject> stringToJson(String inline) {
        String strJson = inline;
        strJson = strJson.replace("[","");
        strJson = strJson.replace("]","");
        ArrayList<JsonObject> jsonObjs = new ArrayList<>();
        String[] jsons = strJson.split("},");

        for (int i = 0; i < jsons.length; i++) {
            if (i != jsons.length - 1){
                jsons[i] += "}";
            }
        }
        for (String json : jsons) {
            if (!json.equals("")) {
                jsonObjs.add(JsonParser.parseString(json).getAsJsonObject());
            }
        }

        return jsonObjs;
    }
}
