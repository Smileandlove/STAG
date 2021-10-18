import Entities.*;
import Entities.Character;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StagController {
    private final StagParser parser;
    private Player currentPlayer;
    private final HashMap<String,Player> players;
    private Location startLoc;

    public StagController(StagParser parser) {
        this.parser = parser;
        players = new HashMap<>();
    }

    /*
    About why I used BufferedWriter:
    At the beginning I wanted to use this method to return a string, using BufferedWriter in the Server class.
    But the string returned by other methods has a problem that I cannot solve temporarily.
    I can only write the out every time.
    */

    public void processCommand(BufferedWriter out,String line) throws IOException {
        String [] wholeCommand = line.split(": ");

        //Check if it's a new player.
        if (isNewPlayer(wholeCommand[0])){
            Player newPlayer = new Player(wholeCommand[0],"One of players",getStartLoc());
            setCurrentPlayer(newPlayer);
            getStartLoc().addCharacter(newPlayer);
            players.put(newPlayer.getName(),newPlayer);
        }else {
            setCurrentPlayer(players.get(wholeCommand[0]));
        }

        //When the user does not enter or only enters a space, avoid NullPointer.
        if (wholeCommand.length==1){
            out.write("Null action");
            return;
        }
        if (wholeCommand[1].isBlank()){
            out.write("Null action");
            return;
        }

        //Parsing command.
        ArrayList<String> command = new ArrayList<>(Arrays.asList(wholeCommand[1].split(" ")));
        if (checkStandard(command)){
            standardCommands(out,command);
            return;
        }
        out.write(otherCommands(command) + "\n");
    }

    //Check whether the command is a standard command type.
    public boolean checkStandard(ArrayList<String> command){
        for (String s : Arrays.asList("inventory","inv", "get", "drop", "goto", "look", "health")) {
            if (checkContain(command, s)){
                return true;
            }
        }
        return false;
    }

    //Run standard commands.
    public void standardCommands(BufferedWriter out, ArrayList<String> command) throws IOException {
        Location location = getCurrentPlayer().getLocation();
        if (checkContain(command,"inventory") || checkContain(command,"inv")){
            out.write(actionInv() + "\n");
            return;
        }
        if (checkContain(command,"get")){
            for (String artefact : location.getArtefacts().keySet()){
                if (checkContain(command,artefact)){
                    getCurrentPlayer().addArtefact(location.getArtefacts().get(artefact));
                    location.removeArtefact(artefact);
                    out.write("You picked up the " + artefact + "\n");
                    return;
                }
            }
            out.write("No such artefact here" + "\n");
            return;
        }
        if (checkContain(command,"drop")){
            for (String artefact : getCurrentPlayer().getArtefacts().keySet()){
                if (checkContain(command,artefact)){
                    location.addArtefact(getCurrentPlayer().getArtefacts().get(artefact));
                    getCurrentPlayer().removeArtefact(artefact);
                    out.write("You dropped the " + artefact + "\n");
                    return;
                }
            }
            out.write("No such artefact in your inventory" + "\n");
            return;
        }
        if (checkContain(command,"goto")){
            for (String loc : location.getPaths().keySet()){
                if (checkContain(command,loc)){
                    location.removeCharacter(getCurrentPlayer().getName());
                    getCurrentPlayer().setLocation(parser.getLocations().get(loc));
                    parser.getLocations().get(loc).addCharacter(getCurrentPlayer());
                    out.write(actionLook()+"\n");
                    return;
                }
            }
            out.write("No path to the location" + "\n");
            return;
        }
        if (checkContain(command,"look")){
            out.write(actionLook()+"\n");
        }
        if (checkContain(command,"health")){
            out.write("Your health level is "+ getCurrentPlayer().getHealth() +"\n");
        }
    }

    //Complete the output required for the "inv" command.
    public String actionInv(){
        String response = "You have:" + "\n";
        for (String artefact : getCurrentPlayer().getArtefacts().keySet()){
            response = response.concat("\t"+artefact+"\n");
        }
        return response;
    }

    //Complete the output required for the "look" command.
    public String actionLook(){
        Location location = getCurrentPlayer().getLocation();
        String response = "You are in " + location.getDescription().toLowerCase() +
                ". You can see:" + "\n";
        for (Artefact artefact : location.getArtefacts().values()){
            response = response.concat("\t"+artefact.getName() +": " + artefact.getDescription()+"\n");
        }

        for (Character character: location.getCharacters().values()){
            if (character.getName().equals(getCurrentPlayer().getName())){
                response = response.concat("\t"+character.getName()+": "+"Yourself"+"\n");
            }else {
                response = response.concat("\t"+character.getName()+": "+character.getDescription()+"\n");
            }
        }

        for (Furniture furniture : location.getFurniture().values()){
            response = response.concat("\t"+furniture.getName()+": "+furniture.getDescription()+"\n");
        }
        response = response.concat("You can access from here:"+"\n");
        for (String path : location.getPaths().keySet()){
            response = response.concat("\t"+ path + "\n");
        }
        return response;
    }

    //Run other commands, which are actually actions in data.
    public String otherCommands(ArrayList<String> command) throws IOException {
        Location location = getCurrentPlayer().getLocation();
        if (checkAction(command)){//Check if command is an usable action.
            Action action = getAction(command);
            if (checkSubjects(action,getCurrentPlayer())){//Check if there are subjects required.
                return "No subjects here";
            }
            else {
                consumed(getCurrentPlayer(),action);//Complete consuming.

                if (getCurrentPlayer().getHealth()<=0){//After consuming, check whether the current player has died.
                    location.getArtefacts().putAll(getCurrentPlayer().getArtefacts());
                    getCurrentPlayer().getArtefacts().clear();
                    location.removeCharacter(getCurrentPlayer().getName());
                    getStartLoc().addCharacter(getCurrentPlayer());
                    getCurrentPlayer().setLocation(getStartLoc());
                    getCurrentPlayer().setHealth(3);
                    return action.getNarration() + "\n" + "You have died and gone back to start location.";
                }

                produced(getCurrentPlayer(),action);//Complete producing.
                return action.getNarration();
            }
        }
        return "Meaningless action";
    }

    //Check if there are subjects required.
    public boolean checkSubjects(Action action,Player player){
        Location location = player.getLocation();
        for (String subject : action.getSubjects()){
            if (!player.getArtefacts().containsKey(subject) && !location.getArtefacts().containsKey(subject)
                    && !location.getCharacters().containsKey(subject)
                    && !location.getFurniture().containsKey(subject)
                    && !location.getPaths().containsKey(subject)) {
                return true;
            }
        }
        return false;
    }

    //Check if command is an usable action.
    public boolean checkAction(ArrayList<String> command){
        for (Action action : parser.getActions()){
            for (String trigger : action.getTriggers()){
                if (checkContain(command,trigger)){
                    if (command.size()<=1){
                        return false;
                    }
                    for (String subject : action.getSubjects()){
                        if (checkContain(command,subject)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //Get the action corresponding to the command.
    public Action getAction(ArrayList<String> command) throws IOException {
        if (checkAction(command)){
            for (Action action : parser.getActions()){
                for (String trigger : action.getTriggers()){
                    if (checkContain(command,trigger)){
                        return action;
                    }
                }
            }
        }
        throw new IOException("Meaningless action");
    }

    //Check whether the action consumed the health of player.
    public boolean checkConsumedHealth(Action action) {
        for (String consumed: action.getConsumed()){
            if (consumed.equalsIgnoreCase("health")){
                return true;
            }
        }
        return false;
    }

    //Check whether the action produced the health for player.
    public boolean checkProducedHealth(Action action) {
        for (String produced: action.getProduced()){
            if (produced.equalsIgnoreCase("health")){
                return true;
            }
        }
        return false;
    }

    public void consumed(Player player,Action action) {
        if (action.getConsumed().isEmpty()){
            return;
        }
        if (checkConsumedHealth(action)){
            player.setHealth(player.getHealth()-1);
            return;
        }
        for (String ent : action.getConsumed()){
            if (player.getArtefacts().get(ent)!=null){
                player.removeArtefact(ent);
            }
            if (player.getLocation().getArtefacts().get(ent)!=null){
                player.getLocation().removeArtefact(ent);
            }
            if (player.getLocation().getFurniture().get(ent)!=null){
                player.getLocation().removeFurniture(ent);
            }
            if (player.getLocation().getCharacters().get(ent)!=null){
                player.getLocation().removeCharacter(ent);
            }
            if (player.getLocation().getPaths().get(ent)!=null){
                player.getLocation().removePath(ent);
            }
        }
    }

    public void produced(Player player,Action action) {
        if (action.getProduced().isEmpty()){
            return;
        }
        if (checkProducedHealth(action)){
            player.setHealth(player.getHealth()+1);
            return;
        }
        for (String ent : action.getProduced()){
            if(parser.getLocations().get(ent) != null){
                player.getLocation().addPath(parser.getLocations().get(ent));
            }
            for (Location location : parser.getLocations().values()){
                if (location.getArtefacts().get(ent)!=null){
                    player.getLocation().addArtefact(location.getArtefacts().get(ent));
                }
                if (location.getFurniture().get(ent)!=null){
                    player.getLocation().addFurniture(location.getFurniture().get(ent));
                }
                if (location.getCharacters().get(ent)!=null){
                    player.getLocation().addCharacter(location.getCharacters().get(ent));
                }
            }
        }
    }


    public Location getStartLoc(){
        return startLoc;
    }

    public void setStartLoc(){
        startLoc = (Location) parser.getLocations().values().toArray()[0];
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isNewPlayer(String name){
        if (!players.isEmpty()){
            for (String temp : players.keySet()){
                if (name.equals(temp)){
                    return false;
                }
            }
        }
        return true;
    }

    //Check whether the command contains keywords and is not case sensitive.
    public boolean checkContain(ArrayList<String> command, String keyword){
        for (String word : command){
            if (keyword.equalsIgnoreCase(word)){
                return true;
            }
        }
        return false;
    }

}
