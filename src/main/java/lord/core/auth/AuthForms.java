package lord.core.auth;

import dev.ghostlov3r.beengine.form.CustomForm;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.utils.TextFormat;
import lombok.RequiredArgsConstructor;
import lord.core.gamer.Gamer;

@RequiredArgsConstructor
public class AuthForms {
	
	private final Auth auth;
	
	public void login (Gamer player) {
		player.sendForm(
			Form.custom()
				  .label(player.name() + ", ваш сеанс истёк.")
				  .input("Используйте свой пароль, чтобы играть снова")
				  .label("Если вы забыли пароль, используйте команду /iforgot")
				  .onSubmit((p, r) -> this.auth.handleLoginData(player, r.getInput(0)))
		);
	}
	
	public void registerStart (Gamer player) {
		player.sendForm(
			Form.simple()
				  .content("Доброго времени суток, " + player.name() + "! \n\n"
				+ "Этот сервер использует регистрацию, чтобы под этим ником могли играть только Вы, поэтому давайте пройдет пару простых шагов")
				  .button("ДАЛЕЕ", (p) -> this.registerStep1(player, null))
		);
	}
	
	/** Пользователь вводит пароль */
	public void registerStep1 (Gamer player, String error) {
		CustomForm form = Form.custom();
		if (error != null) {
			form.label(TextFormat.RED+error+TextFormat.RESET);
		}
		form.label("Придумайте Ваш пароль. Он будет использоваться, чтобы войти на сервер.")
			.label("Обратите внимание: ")
			.label("1. пароль должен содержать минимум 6 символов.")
			.label("2. пароль должен иметь и цифры, и буквы")
			.label("3. пароль НЕ должен иметь пробелы")
			.input("", "Пароль вводить сюда")
			.input("Теперь во второе поле введите опять этот пароль:", "Еще раз пароль вводить сюда");
		
		form.onSubmit((p, r) -> {
			String password = r.getInput(0);
			if (password.length() < 6) {
				this.registerStep1(player, "Обратите внимание на Первый пункт.");
				return;
			}
			if (password.contains(" ")) {
				this.registerStep1(player, "Обратите внимание на Третий пункт.");
				return;
			}
			if (!password.equals(r.getInput(1))) {
				this.registerStep1(player, "Пароль в первом и втором поле должен быть одинаковым.");
				return;
			}
			RegisterData data = new RegisterData();
			data.password = password;
			this.registerStep2(player, data,null);
		});
		
		player.sendForm(form);
	}
	
	/** Пользователь вводит почту */
	public void registerStep2 (Gamer player, RegisterData data, String error) {
		CustomForm form = Form.custom();
		if (error != null) {
			form.label(TextFormat.RED+error+TextFormat.RESET);
		}
		form.label("Может случиться так, что Ваш пароль будет утерян. \n\n")
			.input("Укажите Ваш адрес электронной почты и вы всегда сможете восстановить пароль.", "Email вводить сюда")
			.label("P.S.: Этот шаг можно пропустить, оставив поле пустым.");
		
		form.onSubmit((p, r) -> {
			String email = r.getInput(0).trim();
			if ("".equals(email)) {
				data.email = "skip";
			} else {
				//check email syntax
				data.email = email;
			}
			this.registerStep3(player, data,null);
		});
		
		player.sendForm(form);
	}
	
	/** Пользователь вводит ссылку вк */
	public void registerStep3 (Gamer player, RegisterData data, String error) {
		CustomForm form = Form.custom();
		if (error != null) {
			form.label(TextFormat.RED+error+TextFormat.RESET);
		}
		form.label("Пожалуйста, укажите ссылку на Ваш профиль Вконтакте. Она Также сможет использоваться, чтобы вернуть доступ к аккаунту или решить проблему")
			.input("", "Ссылку вводить сюда")
			.label("P.S.: Этот шаг можно пропустить, оставив поле пустым.");
		
		form.onSubmit((p, r) -> {
			String vklink = r.getInput(0).trim();
			if ("".equals(vklink)) {
				data.vklink = "skip";
			} else {
				//check vk syntax
				data.vklink = vklink;
			}
			this.registerStep4(player, data);
		});
		
		player.sendForm(form);
		
	}
	
	/** Пользователь видит и проверяет введенные данные */
	public void registerStep4 (Gamer player, RegisterData data) {
		if (data == null) {
			player.sendMessage("К сожалению, случилась ошибка. Попробуйте снова или обратитесь в Поддержку.");
			return;
		}
		player.sendForm(
			Form.simple()
				  .content("Проверьте введенную информацию.\n\n" +
					"- Пароль: " + data.password + "\n" +
					"- Email: " + data.email + "\n" +
					"- VK-ссылка: " + data.vklink + "\n\n" +
					"Если все верно, нажмите Подтвердить.")
				  .button("ПОДТВЕРДИТЬ", (p) -> {
					this.auth.handleRegisterData(player, data);
				})
				  .button("НАЧАТЬ СНАЧАЛА", (p) -> this.registerStart(player))
		);
	}
	
	public void firstWelcome (Gamer player) {
		player.sendForm(
			Form.simple()
				  .content("Команда NEKRAFT рада, что теперь Вы с нами!\n\n" +
					"Сохраните этот пароль: " + player.password +
					"\n\nЭто NEKRAFT! Строй, ломай, стань лучшим!\n" +
					"Получайте опыт, чтобы открыть новые возможности.\n" +
					"Пидарасы на спауне будут давать Вам задания\n\n" +
					"Пиздуйте копать шахты и покупайте донат скидки 99%. Этот текст подлежит переработке")
		);
	}
	
}
