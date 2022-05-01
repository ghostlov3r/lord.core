package lord.core.auth;

import beengine.Server;
import beengine.scheduler.AsyncTask;
import beengine.scheduler.Scheduler;
import beengine.util.TextFormat;
import beengine.util.Utils;
import beengine.world.GenerationOptions;
import beengine.world.World;
import beengine.world.generator.FlatGenerator;
import lord.core.Lord;
import lord.core.gamer.Gamer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Auth {

	/** Попытки авторизации игрока */
	private final Map<String, LoginTry> loginTries = new HashMap<>();

	public AuthForms forms = new AuthForms(this);

	public World world;

	public Auth () {
		var options = new GenerationOptions();
		options.name = Lord.instance.config().authWorld;
		options.generator = "flat";
		world = World.generate(options).result();
	}

	/** Запрашивает авторизацию у игрока */
	public void requestAuth (Gamer player) {
		Scheduler.delayedRepeat(20, 50, new AuthTask(player));
	}

	/** Обработка данных для входа в аккаунт */
	public void handleLoginData (Gamer player, String password) {
		player.logger().debug("Calling handleLoginData");

		if (player.isSneaking()) {
			player.setSneaking(false);
		}

		final LoginTry loginTry = this.loginTries.get(player.name());
		if (loginTry != null) {
			if (loginTry.isLimitReached(player)) {
				player.disconnect("Пароль введен неверно " + LoginTry.maxCount + " раз." +
					System.lineSeparator() + "Авторизация более не доступна.");
				return;
			}
		}

		if (player.handlingPassword) {
			return;
		}
		player.handlingPassword = true;
		Server.asyncPool().execute(new AsyncTask() {
			boolean matches;

			@Override
			public void run() {
				matches = Arrays.equals(player.password, doHash(password, player.name()));
			}

			@Override
			public String name() {
				return "Login hash ("+player.name()+")";
			}

			@Override
			protected void onCompletion() {
				if (matches) {
					if (loginTry != null) {
						if (loginTry.remove(player)) {
							loginTries.remove(player.name());
						}
					}
					player.setAuthorized(); // Суть
				} else {
					if (loginTry == null) {
						var newLoginTry = new LoginTry();
						loginTries.put(player.name(), newLoginTry);
						newLoginTry.increment(player);
					} else {
						loginTry.increment(player);
					}
					player.sendMessage(TextFormat.RED+"Введённый пароль не подходит");
				}
				player.handlingPassword = false;
			}
		});
	}

	/** Обработка данных регистрации */
	public void handleRegisterData (Gamer player, RegisterData data) {
		if (player.handlingPassword) {
			return;
		}
		player.handlingPassword = true;
		Server.asyncPool().execute(new AsyncTask() {
			byte[] result;

			@Override
			public void run() {
				result = doHash(data.password, player.name());
			}

			@Override
			public String name() {
				return "Register hash ("+player.name()+")";
			}

			@Override
			protected void onCompletion() {
				player.password = result;
				player.email(data.email);
				player.vklink(data.vklink);
				player.handlingPassword = false;
				player.setAuthorized();
				Scheduler.delay(10, () -> forms.firstWelcome(player));
			}
		});
	}

	private static final byte ITERATIONS = 64;
	private static final byte[] SALT = "#@&0()-'".getBytes(StandardCharsets.UTF_8);

	private byte[] doHash (String password, String player) {
		MessageDigest digest = sha512.get();
		byte[] result = password.trim().getBytes(StandardCharsets.UTF_8);
		byte[] playerBytes = player.toLowerCase().getBytes(StandardCharsets.UTF_8);
		byte firstByte = playerBytes[0];
		byte lastByte = playerBytes[playerBytes.length - 1];
		for (byte i = 0; i < ITERATIONS; i++) {
			result = digest.digest(Utils.merge(SALT, result, new byte[] {firstByte, lastByte, i}));
		}
		return result;
	}

	private static ThreadLocal<MessageDigest> sha512 = ThreadLocal.withInitial(() -> {
		try {
			return MessageDigest.getInstance("SHA-512");
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	});
}
