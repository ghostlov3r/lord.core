package lord.core.game.region;

/** Управление действями внутри и вне Region */
public class RegionActions {
	
	public boolean onBuild () {
		return false;
	}
	
	public boolean onDamage () {
		return false;
	}
	
	public boolean onDoor () {
		return false;
	}
	
	public boolean onChest () {
		return false;
	}
	
	
	/* public boolean onBuild (LordPlayer gamer, BlockEvent event) {
		Region region = getRegion(event.getBlock());
		if (region == null) {
			return buildAbsent.apply(gamer, event);
		}
		return buildPresent.apply(gamer, region);
	}*/
	
	/* @Setter
	private BiFunction<LordPlayer, Region, Boolean> buildPresent = (gamer, region) -> {
		if (region.build) return true;
		return region.isMember(gamer.getName());
	}; */
	
}
