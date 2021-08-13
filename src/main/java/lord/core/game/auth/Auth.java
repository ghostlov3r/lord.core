package lord.core.game.auth;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerToggleSneakEvent;
import cn.nukkit.potion.Effect;
import lord.core.api.CoreApi;
import lord.core.api.TaskApi;
import lord.core.gamer.Gamer;
import lord.core.gamer.GamerData;

import java.util.HashMap;
import java.util.Map;

public class Auth implements Listener {
	
	/** Попытки авторизации игрока */
	private final Map<String, LoginTry> loginTries = new HashMap<>();
	
	private AuthForms forms = new AuthForms(this);
	
	public Auth () {
		CoreApi.listeners(this);
	}
	
	/** При приседании */
	@EventHandler
	public void onSneak (PlayerToggleSneakEvent event) {
		if (event.isSneaking()) {
			Gamer player = Gamer.from(event.getPlayer());
			if (!player.isAuthorized()) {
				if (player.isRegistered()) {
					this.forms.login(player);
				} else {
					this.forms.registerStart(player);
				}
			}
		}
	}
	
	/** Запрашивает авторизацию у игрока */
	public void requestAuth (Gamer player) {
		this.disallowActions(player);
		TaskApi.delayedRepeat(4, 2.7, new AuthTask(player));
	}
	
	/** Обработка данных для входа в аккаунт */
	public void handleLoginData (Gamer player, String password) {
		LoginTry loginTry = this.loginTries.get(player.getName());
		if (loginTry != null) {
			if (loginTry.isLimitReached(player)) {
				player.delayedKick("Вы ввели пароль неверно " + LoginTry.maxCount + " раз." +
					System.lineSeparator() + "Авторизация более не доступна.");
				return;
			}
		}
		
		if (password.trim().equals(player.getData().getPassword())) {
			if (loginTry != null) {
				if (loginTry.remove(player)) {
					this.loginTries.remove(player.getName());
				}
			}
			player.setAuthorized(); // Суть
		} else {
			if (loginTry == null) {
				loginTry = new LoginTry();
				this.loginTries.put(player.getName(), loginTry);
			}
			loginTry.increment(player);
			player.delayedKick("Введённый пароль не подходит");
		}
	}
	
	/** Обработка данных регистрации */
	public void handleRegisterData (Gamer player, RegisterData data) {
		GamerData lordData = player.getData();
		lordData.setRegData(data);
		
		player.setAuthorized();
		
		TaskApi.delay(3, () -> this.forms.firstWelcome(player));
	}
	
	public void disallowActions (Player player) {
		player.setGamemode(Player.ADVENTURE);
		player.setImmobile(true);
		Effect effect = Effect.getEffect(Effect.BLINDNESS);
		effect.setAmplifier(1).setDuration(20 * 400);
		player.addEffect(effect);
	}
	
	public void allowActions (Player player) {
		player.setGamemode(Player.SURVIVAL);
		player.setImmobile(false);
		player.removeEffect(Effect.BLINDNESS);
	}

}
