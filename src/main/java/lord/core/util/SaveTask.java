package lord.core.util;

import cn.nukkit.scheduler.Task;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.gamer.Gamer;

public class SaveTask extends Task {
	
	private boolean inProcess = false;
	
	@Override
	public void onRun (int tick) {
		this.saveAll();
	}
	
	public synchronized void saveAll () {
		if (this.inProcess) {
			return;
		}
		this.inProcess = true;
		LordCore.log.info("[Saver] Saving!");
		this.saveGamers();
		// saveRegions();
		// saveWarps();
		this.inProcess = false;
		LordCore.log.info("[Saver] Completed!");
	}
	
	public void saveGamers () {
		CoreApi.getCore().getServer().getOnlinePlayers().forEach(((uuid, player) -> {
			Gamer.from(player).saveData();
		}));
	}
	
	public void saveRegions () {
		/* Region.regions.forEach((x, yMap) -> yMap.forEach((y, region) -> {
			if (!region.saved) {
				Region.save(region);
				region.saved = true;
				LordCore.write("Region saved: " + x + " - " + y); //debug
			}
		})); */
	}
	
	public void saveWarps () {
		Warpold.storage.forEach((s, warp) -> warp.save() );
	}
	
	public void saveHomes () {
	
	}
	
}
