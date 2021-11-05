package lord.core.game.auth;

import dev.ghostlov3r.beengine.entity.effect.Effect;
import dev.ghostlov3r.beengine.entity.effect.EffectInstance;
import dev.ghostlov3r.beengine.entity.effect.Effects;
import dev.ghostlov3r.beengine.event.player.PlayerToggleSneakEvent;
import dev.ghostlov3r.beengine.player.GameMode;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import lord.core.gamer.Gamer;

import java.util.HashMap;
import java.util.Map;

public class Auth {
	
	/** Попытки авторизации игрока */
	private final Map<String, LoginTry> loginTries = new HashMap<>();
	
	private AuthForms forms = new AuthForms(this);

	public void onSneak (PlayerToggleSneakEvent<Gamer> event) {
		if (event.isSneaking()) {
			if (!event.player().authorized()) {
				if (event.player().isRegistered()) {
					this.forms.login(event.player());
				} else {
					this.forms.registerStart(event.player());
				}
			}
		}
	}
	
	/** Запрашивает авторизацию у игрока */
	public void requestAuth (Gamer player) {
		this.disallowActions(player);
		Scheduler.delayedRepeat(80, 50, new AuthTask(player));
	}
	
	/** Обработка данных для входа в аккаунт */
	public void handleLoginData (Gamer player, String password) {
		LoginTry loginTry = this.loginTries.get(player.name());
		if (loginTry != null) {
			if (loginTry.isLimitReached(player)) {
				player.delayedKick("Вы ввели пароль неверно " + LoginTry.maxCount + " раз." +
					System.lineSeparator() + "Авторизация более не доступна.");
				return;
			}
		}
		
		if (password.trim().equals(player.password())) {
			if (loginTry != null) {
				if (loginTry.remove(player)) {
					this.loginTries.remove(player.name());
				}
			}
			player.setAuthorized(); // Суть
		} else {
			if (loginTry == null) {
				loginTry = new LoginTry();
				this.loginTries.put(player.name(), loginTry);
			}
			loginTry.increment(player);
			player.delayedKick("Введённый пароль не подходит");
		}
	}
	
	/** Обработка данных регистрации */
	public void handleRegisterData (Gamer player, RegisterData data) {
		player.setRegData(data);
		
		player.setAuthorized();
		
		Scheduler.delay(3, () -> this.forms.firstWelcome(player));
	}
	
	public void disallowActions (Player player) {
		player.setGamemode(GameMode.ADVENTURE);
		player.setImmobile(true);
		EffectInstance effect = new EffectInstance(Effects.BLINDNESS);
		effect.setAmplifier(1);
		effect.setDuration(20 * 400);
		player.effects().add(effect);
	}
	
	public void allowActions (Player player) {
		player.setGamemode(GameMode.SURVIVAL);
		player.setImmobile(false);
		player.effects().remove(Effects.BLINDNESS);
	}

}
