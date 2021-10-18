package Entities;

import java.util.HashMap;

public class Player extends Character {
    private Location location;
    private final HashMap<String,Artefact> artefacts;
    private int health; //Record health

    public Player(String name, String description,Location location) {
        super(name, description);
        this.location = location;
        health = 3;
        artefacts = new HashMap<>();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void addArtefact(Artefact artefact){
        artefacts.put(artefact.getName(),artefact);
    }

    public void removeArtefact(String name){
        artefacts.remove(name);
    }

    public HashMap<String, Artefact> getArtefacts() {
        return artefacts;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
