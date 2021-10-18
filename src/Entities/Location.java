package Entities;

import java.util.HashMap;

public class Location extends Entity{
    private final HashMap<String,Location> paths;
    private final HashMap<String,Character> characters;
    private final HashMap<String,Artefact> artefacts;
    private final HashMap<String,Furniture> furniture;

    public Location(String name, String description) {
        super(name, description);

        paths = new HashMap<>();
        characters = new HashMap<>();
        artefacts = new HashMap<>();
        furniture = new HashMap<>();
    }

    public void addPath(Location path){
        paths.put(path.getName(),path);
    }

    public void addCharacter(Character character){
        characters.put(character.getName(),character);
    }

    public void addArtefact(Artefact artefact) {
        artefacts.put(artefact.getName(),artefact);
    }

    public void addFurniture(Furniture furniture){
        this.furniture.put(furniture.getName(),furniture);
    }

    public void removePath(String name){
        paths.remove(name);
    }

    public void removeCharacter(String name){
        characters.remove(name);
    }

    public void removeArtefact(String name){
        artefacts.remove(name);
    }

    public void removeFurniture(String name){
        this.furniture.remove(name);
    }

    public HashMap<String, Location> getPaths() {
        return paths;
    }

    public HashMap<String, Character> getCharacters() {
        return characters;
    }

    public HashMap<String, Artefact> getArtefacts() {
        return artefacts;
    }

    public HashMap<String, Furniture> getFurniture() {
        return furniture;
    }
}
