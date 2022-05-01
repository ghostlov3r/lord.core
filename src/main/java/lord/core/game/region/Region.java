package lord.core.game.region;

import beengine.util.config.Config;

import java.nio.file.Path;
import java.util.ArrayList;

public class Region extends Config {
	
	/* Название региона */
	public String name;
	
	/** Имя владельца */
	public String owner;
	
	/** Список участников */
	public ArrayList<String> memberList;
	
	public boolean pvp;
	public boolean build;
	public boolean door;
	public boolean chest;
	
	public transient int x;
	public transient int z;
	
	/** Требуется ли сохранение */
	public transient boolean dirty;
	
	/** Служебный несохраняемый регион */
	public transient boolean service;
	
	public boolean isOwner (String playerName) {
		return playerName.equals(this.owner);
	}
	
	public boolean isMember (String playerName) {
		return this.memberList.contains(playerName);
	}
	
	public Path getRegionFile() {
		return null; // Regions.getRegionFile(this.x, this.z);
	}
	
	@Override
	public void save () {
		if (this.service) return;
		if (this.dirty) {
			super.save();
			this.dirty = false;
		}
	}
	
	/** Имеет ли регион такие же координаты */
	public boolean equals (Region region) {
		return (this.x == region.x) && (this.z == region.z);
	}
}
