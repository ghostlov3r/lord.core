package lord.core.game.sanction;

import cn.nukkit.Player;
import lombok.var;
import lord.core.command.service.CmdArgs;
import lord.core.command.service.LordCommand;
import lord.core.util.form.FormV1;
import lord.core.util.form.response.CustomFormResponseV1;
import lord.core.gamer.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class BanCommand extends LordCommand {
	
	private final Sanction sanction;
	
	/** Причины бана в формате ID :: Text */
	private final List<String> reasons = new ArrayList<>();
	
	/** Заготовка вариантов выбора часов бана */
	private final List<String> hours = new ArrayList<>();
	
	/** Заготовка вариантов выбора дней бана */
	private final List<String> days = new ArrayList<>();
	
	public BanCommand (Sanction sanction) {
		super("ban");
		setDescription("Блокировка игрока");
		setPermission("lordcmd.ban");
		
		this.sanction = sanction;
		
		sanction.getReasons().forEach(reason -> {
			reasons.add(reason.getName() + " :: " + reason.getText());
		});
		
		this.hours.addAll(Arrays.asList("1", "2", "3", "5", "7", "10", "12", "15", "20"));
		this.days.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "15", "20", "30"));
	}
	
	private static final String CHOOSE_PLAYER = "Выберите игрока";
	private static final String CHOOSE_REASON = "Укажите причину блокировки";
	private static final String CHOOSE_HOURS = "Выберите количество часов";
	private static final String CHOOSE_DAYS = "Выберите количество дней";
	private static final String WARNING = "За выбор неверной причины последует наказание!";
	
	@Override
	public boolean handle (Gamer gamer, CmdArgs args) {
		var form = FormV1.custom();
		var names = new ArrayList<String>();
		this.server.getOnlinePlayers().forEach((uuid, player) -> {
			names.add(player.getName());
		});
		
		form.title("Блокировка игрока");
		form.dropdown(CHOOSE_PLAYER, names);
		form.dropdown(CHOOSE_REASON, this.reasons);
		form.stepSlider(CHOOSE_HOURS, this.hours);
		form.stepSlider(CHOOSE_DAYS, this.days);
		form.label(WARNING);
		
		form.onSubmit(this.banFormHandler);
		
		gamer.sendForm(form.build());
		return true;
	}
	
	/** Обработчик формы блокировки игрока */
	private BiConsumer<Player, CustomFormResponseV1> banFormHandler = (player, resp) -> {
		int hours;
		int days;
		try {
			hours = Integer.parseInt(resp.getStepSlider(1).getOption());
			days = Integer.parseInt(resp.getStepSlider(2).getOption());
		} catch (NumberFormatException e) {
			player.sendMessage("Данные были испорчены");
			return;
		}
		
		String name = resp.getDropdown(1).getOption();
		String reason = resp.getDropdown(2).getOption();
		String ruleID = reason.split("::")[0].trim();
		
		var entry = BanCommand.this.sanction.ban(name, player.getName(), ruleID, days, hours);
		
		Player target = this.server.getPlayer(name);
		if (target != null && target.isOnline()) {
			String message = entry.generateMessage();
			target.close(message);
		}
		
		this.successBanForm(Gamer.from(player), entry);
	};
	
	/** Отправяет форму об успешной блокировке игрока */
	private void successBanForm (Gamer gamer, BanEntry entry) {
		var message = new StringBuilder()
			.append("Новая блокировка добавлена!")
			.append(System.lineSeparator())
			.append(System.lineSeparator())
			.append("Игрок: ")
			.append(entry.getName())
			.append(System.lineSeparator())
			.append("Причина: ")
			.append(entry.getReason().getText())
			.append("Время разбана: ");
		if (entry.isForever()) {
			message.append("никогда");
		} else {
			message.append(entry.getUnbanDate());
		}
		
		var form = FormV1.simple();
		form.button("Отменить блокировку", player -> {
			this.sanction.unban(entry.getName());
			player.sendMessage("Игрок успешно разблокирован: " + entry.getName());
		});
		form.button("Закрыть");
		gamer.sendForm(form.build());
	}
}
