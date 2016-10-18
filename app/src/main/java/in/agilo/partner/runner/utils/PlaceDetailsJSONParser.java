package in.agilo.partner.runner.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceDetailsJSONParser {

    /**
     * Receives a JSONObject and returns a list
     */
    public List<HashMap<String, String>> parse(JSONObject jObject) {

        Double lat = Double.valueOf(0);
        Double lng = Double.valueOf(0);
        String formattedAddress = "";
        String area = "";
        String city = "";
        String state = "";
        String pincode = "";

        HashMap<String, String> hm = new HashMap<String, String>();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        try {
            lat = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat");
            lng = (Double) jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lng");
            JSONArray addressComponents = jObject.getJSONObject("result").getJSONArray("address_components");
            for (int i = 0; i < addressComponents.length(); i++) {
                JSONObject component = addressComponents.getJSONObject(i);
                String name = component.getString("long_name");
                JSONArray types = component.getJSONArray("types");
                String type = types.getString(0);
                if (type.equals("sublocality_level_1") || type.equals("sublocality_level_2") || type.equals("sublocality")) {
                    if (area.equals(""))
                        area = name;
                    else
                        area += ", " + name;
                }

                if (type.equals("administrative_area_level_2")) {
                    city = name;
                }

                if (type.equals("administrative_area_level_1")) {
                    state = name;
                }

                if (type.equals("postal_code")) {
                    pincode = name;
                }
            }

            formattedAddress = (String) jObject.getJSONObject("result").get("formatted_address");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        hm.put("lat", Double.toString(lat));
        hm.put("lng", Double.toString(lng));
        hm.put("area", area);
        hm.put("city", city);
        hm.put("state", state);
        hm.put("pincode", pincode);
        hm.put("formatted_address", formattedAddress);

        list.add(hm);

        return list;
    }
}
