import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashSet;

public class Action {
    private final HashSet<String> triggers;
    private final HashSet<String> subjects;
    private final HashSet<String> consumed;
    private final HashSet<String> produced;
    private final String narration;

    //Get the action data from the json file.
    public Action(JSONObject action) {
        triggers = new HashSet<>();
        subjects = new HashSet<>();
        consumed = new HashSet<>();
        produced = new HashSet<>();

        setTriggers(action);
        setSubjects(action);
        setConsumed(action);
        setProduced(action);
        narration = (String) action.get("narration");
    }

    private void setTriggers(JSONObject action){
        JSONArray jsonArray;
        jsonArray = (JSONArray) action.get("triggers");
        for (Object object : jsonArray){
            triggers.add((String) object);
        }
    }

    private void setSubjects(JSONObject action){
        JSONArray jsonArray;
        jsonArray = (JSONArray) action.get("subjects");
        for (Object object : jsonArray){
            subjects.add((String) object);
        }
    }

    private void setConsumed(JSONObject action){
        JSONArray jsonArray;
        jsonArray = (JSONArray) action.get("consumed");
        for (Object object : jsonArray){
            consumed.add((String) object);
        }
    }

    private void setProduced(JSONObject action){
        JSONArray jsonArray;
        jsonArray = (JSONArray) action.get("produced");
        for (Object object : jsonArray){
            produced.add((String) object);
        }
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }

    public HashSet<String> getSubjects() {
        return subjects;
    }

    public HashSet<String> getConsumed() {
        return consumed;
    }

    public HashSet<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }
}
