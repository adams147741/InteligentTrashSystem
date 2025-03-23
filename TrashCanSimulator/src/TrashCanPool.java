import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class TrashCanPool {
    private ArrayList<TrashCan> cans = new ArrayList<>();
    private volatile boolean running;
    ApiHandler handler = new ApiHandler("http://127.0.0.1:8000/devices/");
    public TrashCanPool() {
        String inline = this.handler.getRequest("");
//        inline = inline.replace("[","");
//        inline = inline.replace("]","");
//        ArrayList<JsonObject> jsonObjs = new ArrayList<>();
//        String[] jsons = inline.split("},");
//
//        for (int i = 0; i < jsons.length; i++) {
//            if (i != jsons.length - 1){
//                jsons[i] += "}";
//            }
//        }
//        for (String json : jsons) {
//            if (!json.equals("")) {
//                jsonObjs.add(JsonParser.parseString(json).getAsJsonObject());
//            }
//        }
        ArrayList<JsonObject> jsonObjs = handler.stringToJson(inline);

        //Vytvorenie kosov z databazy
        for (JsonObject jsonObj : jsonObjs) {
            //not null things
            int volumeMax = jsonObj.get("volume_max").getAsInt();
            int weightMax = jsonObj.get("weight_max").getAsInt();
            String description = jsonObj.get("description").getAsString();
            int id = jsonObj.get("id").getAsInt();

            //things that can be null
            int volumeCurrent = convertJsonToInt(jsonObj.get("volume_current"));
            int weightCurrent = convertJsonToInt(jsonObj.get("weight_current"));
            int aqi_inside = convertJsonToInt(jsonObj.get("aqi_inside"));
            int aqi_outside = convertJsonToInt(jsonObj.get("aqi_outside"));
            cans.add(new TrashCan(volumeCurrent, volumeMax, weightCurrent, weightMax, aqi_inside, aqi_outside, id, description));
        }
    }

    public void printCans() {
        String header = "| %-4s | %-25s | %-6s | %-6s |%n";
        String divider = "+------+---------------------------+--------+--------+";
        System.out.println(divider);
        System.out.format(header, "ID", "Description", "Weight","Volume");
        System.out.println(divider);
        String data = "| %-4d | %-25s | %-6d | %-6d |%n";
        for (TrashCan can : cans) {
            System.out.format(data, can.getId(), can.getDescription(), can.getWeightCurrent(), can.getVolumeCurrent());
        }
        System.out.println(divider);
    }

    public void addNewCan(int volumeCurrent,int volumeMax,int weightCurrent,int weightMax,int aqi_inside,int aqi_outside,String description) {
        JsonObject json = new JsonObject();
        json.addProperty("volume_max", volumeMax);
        json.addProperty("weight_max", weightMax);
        json.addProperty("description", description);
        String response = this.handler.postRequest("new", json.toString());
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        int id = object.get("id").getAsInt();
        System.out.println("New trashcan added:" + description + " id:" + id);
        cans.add(new TrashCan(volumeCurrent, volumeMax, weightCurrent, weightMax, aqi_inside, aqi_outside, id, description));
    }

    public void simulation() {
        this.running = true;
        Random rand = new Random();
        this.printCans();
        Scanner scan = new Scanner(System.in);
        System.out.println("generating new trash every 5 seconds");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        //something
                    }
                    if (!running) {
                        return;
                    }
                    //addTrash(weight(g), volume(ml));
                    getCurrentTrashCanState();
                    TrashCan can = cans.get(rand.nextInt(cans.size()));
                    int weight = rand.nextInt(500);
                    int volume = rand.nextInt(250);
                    can.addTrash(weight, volume);
                    System.out.println("can:" + can.getId() + " - " + "added: " + weight + "g and " + volume + "ml");
                    printCans();
                    updateTrashCans();
                }
            }
        });
        t.start();
        if (scan.hasNext()) {
            running = false;
        }
    }

    public static int convertJsonToInt(JsonElement json) {
        if (json.isJsonNull()) {
            return 0;
        }
        return json.getAsInt();
    }

    public void removeTrashCan(int id) {
        TrashCan canToRemove = null;
        for (TrashCan can : cans) {
            if (can.getId() == id) {
                canToRemove = can;
                break;
            }
        }
        if (canToRemove != null) {
            if (Integer.parseInt(this.handler.deleteRequest(String.valueOf(canToRemove.getId()))) == 200){
                cans.remove(canToRemove);
                System.out.println("Trashcan:" + id + " was removed");
                return;
            }
            System.out.println("Trhashcan: " + id + " was not removed");

            /*try {
                URL url = new URL ("http://127.0.0.1:8000/devices/" + canToRemove.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("DELETE");
                conn.connect();
            } catch (MalformedURLException e) {
                System.out.println("urle");
            }
            catch (IOException IOe) {
                System.out.println("ioe");
            }*/

        }
    }

    private void updateTrashCans() {
        for (TrashCan can : cans) {
            int id = can.getId();
            JsonObject json = new JsonObject();
            json.addProperty("volume_current", can.getVolumeCurrent());
            json.addProperty("weight_current", can.getWeightCurrent());
            json.addProperty("aqi_inside", can.getAqi_inside());
            json.addProperty("aqi_outside", can.getAqi_outside());
            this.handler.putRequest(Integer.toString(id), json.toString());
        }
    }

    private void downloadCans() {

    }

    public void clearTrashCans() {
        for (TrashCan can : cans) {
            can.setAqi_inside(0);
            can.setAqi_outside(0);
            can.setVolumeCurrent(0);
            can.setWeightCurrent(0);
        }
        this.updateTrashCans();
    }

    private void getCurrentTrashCanState() {
        this.createTrashCans(handler.stringToJson(handler.getRequest("")));
    }

    private void createTrashCans(ArrayList<JsonObject> jsonObjs){
        this.cans.clear();
        for (JsonObject jsonObj : jsonObjs) {
            //not null things
            int volumeMax = jsonObj.get("volume_max").getAsInt();
            int weightMax = jsonObj.get("weight_max").getAsInt();
            String description = jsonObj.get("description").getAsString();
            int id = jsonObj.get("id").getAsInt();

            //things that can be null
            int volumeCurrent = convertJsonToInt(jsonObj.get("volume_current"));
            int weightCurrent = convertJsonToInt(jsonObj.get("weight_current"));
            int aqi_inside = convertJsonToInt(jsonObj.get("aqi_inside"));
            int aqi_outside = convertJsonToInt(jsonObj.get("aqi_outside"));
            cans.add(new TrashCan(volumeCurrent, volumeMax, weightCurrent, weightMax, aqi_inside, aqi_outside, id, description));
        }
    }
}
