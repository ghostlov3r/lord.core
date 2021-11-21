package lord.core.util;

import dev.ghostlov3r.beengine.form.CustomForm;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.item.Items;
import dev.ghostlov3r.beengine.utils.TextFormat;
import lord.core.gamer.Gamer;

public class LordNpcCommand extends LordCommand{

	public LordNpcCommand() {
		super("npc");
	}

	public static final String NPC_DELETER = "Удалятель NPC";

	@Override
	public void execute(Gamer gamer, String[] args) {
		if (args.length > 0) {
			switch (args[0]) {
				case "maingift" -> {
					gamer.createNpc(MainGiftNpc::new).spawn();
				}
				default -> {
					gamer.sendMessage("Unknown arg");
				}
			}
		}
		else {
			gamer.sendForm(Form.simple()
					.button("Создать", __ -> {
						LordNpc npc = gamer.createNpc();
						CustomForm form = Form.custom();
						form.input("Неймтэг", "", "NPC #" + npc.runtimeId());
						form.toggle("Неймтэг виден", true);
						form.toggle("Неймтэг виден всегда", true);
						form.onSubmit((___, resp) -> {
							npc.setNameTag(resp.getInput(0));
							npc.setNameTagVisible(resp.getToggle(0));
							npc.setNameTagAlwaysVisible(resp.getToggle(1));
							npc.spawn();
							gamer.sendMessage(TextFormat.GREEN + "Успешно создан NPC c id " + npc.runtimeId());
						});
						gamer.sendForm(form);
					})
					.button("Удалить", __ -> {
						gamer.inventory().addItem(Items.GOLDEN_HOE()
								.setCustomName(NPC_DELETER)
								.onInteract((p, ___) -> {
									gamer.inventory().remove(Items.GOLDEN_HOE());
								})
						);
					})
			);
		}
	}
}
