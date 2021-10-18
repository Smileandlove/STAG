import Entities.Artefact;
import Entities.Character;
import Entities.Furniture;
import Entities.Location;

import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class StagParser{
    private final String entityFile;
    private final String actionFile;
    private final LinkedHashMap<String,Location> locations;//Record all locations on the graph.
    private final HashSet<Action> actions;//Record all actions from JSON.

    public StagParser(String entityFile, String actionFile) {
        this.entityFile = entityFile;
        this.actionFile = actionFile;
        locations = new LinkedHashMap<>();
        actions = new HashSet<>();
    }

    public void graphParser() {
        try {
            File entityFile = new File(this.entityFile);
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFile);
            parser.parse(reader);
            ArrayList<Graph> graphs = parser.getGraphs();
            ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
            for (Graph g : subGraphs) {
                ArrayList<Graph> subGraphs1 = g.getSubgraphs();
                for (Graph g1 : subGraphs1) {
                    ArrayList<Node> nodesLoc = g1.getNodes(false);
                    Node nLoc = nodesLoc.get(0);
                    Location location = new Location(nLoc.getId().getId(),nLoc.getAttribute("description"));
                    ArrayList<Graph> subGraphs2 = g1.getSubgraphs();
                    for (Graph g2 : subGraphs2) {
                        ArrayList<Node> nodesEnt = g2.getNodes(false);
                        for (Node nEnt : nodesEnt) {
                            switch (g2.getId().getId()){
                                case "artefacts":
                                    location.addArtefact(new Artefact(nEnt.getId().getId(),
                                            nEnt.getAttribute("description")));
                                    break;
                                case "furniture":
                                    location.addFurniture(new Furniture(nEnt.getId().getId(),
                                            nEnt.getAttribute("description")));
                                    break;
                                case "characters":
                                    location.addCharacter(new Character(nEnt.getId().getId(),
                                            nEnt.getAttribute("description")));
                                    break;
                                default:
                                    System.err.println("Wrong type of Entities: "+nEnt.getId().getId());
                            }
                        }
                    }
                    this.locations.put(location.getName(),location);
                }

                ArrayList<Edge> edges = g.getEdges();
                for (Edge e : edges) {
                    locations.get(e.getSource().getNode().getId().getId()).
                            addPath(locations.get(e.getTarget().getNode().getId().getId()));
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File could not be found!");
        } catch (com.alexmerz.graphviz.ParseException e) {
            System.out.println("Parser has some errors!");
        }
    }

    public void actionParser(){
        JSONParser jsonParser = new JSONParser();
        try(Reader reader = new FileReader(actionFile)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("actions");

            for (Object object : jsonArray){//I wrote the specific implementation in the constructor.
                Action action = new Action((JSONObject) object);
                actions.add(action);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String, Location> getLocations() {
        return locations;
    }

    public HashSet<Action> getActions() {
        return actions;
    }
}
