package lord.core;

import cn.nukkit.Player;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.TextPacket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lord.core.form.Form;
import lord.core.gamer.Gamer;
import lord.core.listener.service.EvH;

public class PacketHandler implements EvH<DataPacketReceiveEvent> {
	
	@Override
	public void handle (DataPacketReceiveEvent event) {
		int type = event.getPacket().pid();
		
		if (ProtocolInfo.TEXT_PACKET == type) {
			Player player = event.getPlayer();
			if (player.spawned && player.isAlive()) {
				TextPacket textPacket = (TextPacket) event.getPacket();
				if (textPacket.type == TextPacket.TYPE_CHAT) {
					((Gamer)player).wantSay(textPacket.message);
				}
			}
			event.setCancelled();
			return;
		}
		
		if (ProtocolInfo.MODAL_FORM_RESPONSE_PACKET == type) {
			Gamer<?, ?> player = (Gamer) event.getPlayer();
			if (player.spawned && player.isAlive()) {
				ModalFormResponsePacket packet = (ModalFormResponsePacket) event.getPacket();
				Form<?> form = player.getFormz().remove(packet.formId);
				if (form != null) {
					try {
						JsonNode response = new JsonMapper().readTree(packet.data);
						
						if ("null".equals(response.asText())) {
							form.onClose(player);
						} else {
							try {
								form.handleResponse(player, response);
							} catch (Exception e) {
								LordCore.log.error("Error while handling form response");
								form.onError(player);
							}
						}
					} catch (JsonProcessingException e) {
						LordCore.log.error("Received corrupted form json data");
					}
				}
			}
			event.setCancelled();
		}
		
	}
	
}
