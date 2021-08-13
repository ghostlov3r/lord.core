package lord.core.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.gamer.Gamer;

public class ScoreTask extends Task {
	
	@Override
	public void onRun (int i) {
		
		for (Player player0 : LordCore.server.getOnlinePlayers().values()) {
			if (player0.isOnline() && player0.isAlive()) {
				Gamer player = (Gamer) player0;
				Scoreboard score = player.getScore();
				if (score != null && score.isShowing()) {
					CoreApi.getCore().scoreMan().onUpdate().accept(score);
					score.update();
				}
			}
		}
		
		/*String online = String.valueOf(LordCore.server.getOnlinePlayers().size());
		
		for (Player gamer : LordCore.server.getOnlinePlayers().values()) {
			if (gamer.isOnline()) {
				LordPlayer player1 = (LordPlayer) gamer;
				if (player1.isAlive() && player1.score != null) {
					player1.score.set(2, "В сети: " + online);
					player1.score.set(5, "Рандом число : " + ChunkRemaker.random.nextInt(200));
					player1.score.update();
				}
			}
		}*/
	}
	
}
