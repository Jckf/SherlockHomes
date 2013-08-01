package it.flaten.homes.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Homes")
public class Home implements Serializable {
    @Id     private int id;
    @Column private String player;
    @Column private String name;
    @Column private String world;
    @Column private int x;
    @Column private int y;
    @Column private int z;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
    public String getPlayer() {
        return this.player;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setWorld(String world) {
        this.world = world;
    }
    public String getWorld() {
        return this.world;
    }

    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return this.x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getY() {
        return this.y;
    }

    public void setZ(int z) {
        this.z = z;
    }
    public int getZ() {
        return this.z;
    }
}
