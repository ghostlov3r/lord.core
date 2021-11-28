package lord.core.util;

import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.minecraft.data.skin.SkinData;
import dev.ghostlov3r.nbt.NbtMap;
import lord.core.Lord;
import lord.core.gamer.Gamer;
import lord.core.union.UnionServer;

public class UnionNpc extends LordNpc {

	protected String serverId = "";

	public UnionNpc(Location location, SkinData skin) {
		super(location, skin);
	}

	public String serverId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public void readSaveData(NbtMap nbt) {
		super.readSaveData(nbt);
		serverId = nbt.getString("serverId", "");
	}

	@Override
	public void writeSaveData(NbtMap.Builder nbt) {
		super.writeSaveData(nbt);
		nbt.setString("serverId", serverId);
	}

	@Override
	protected void updateNameTag() {
		UnionServer server = Lord.unionHandler.getServer(serverId);
		if (server != null) {
			String nameTag = server.name + "\n";
			if (server.isOnline) {
				String strOnline = String.valueOf(server.onlineCount);
				String word = switch (strOnline.charAt(strOnline.length() - 1)) {
					case '1' -> " игрок";
					case '2', '3', '4' -> " игрока";
					default -> " игроков";
				};
				nameTag += TextFormat.GREEN+"Онлайн: "+TextFormat.YELLOW+strOnline+word;
			} else {
				nameTag += TextFormat.RED+"Оффлайн";
			}
			setNameTag(nameTag);
		}
		else {
			setNameTag(TextFormat.RED+"Оффлайн");
		}
	}

	@Override
	public void onClick(Gamer gamer) {
		UnionServer server;
		if (gamer.isAuthorized()) {

			server = Lord.unionHandler.getServer(serverId);
			if (server != null && server.isOnline) {
				gamer.transfer(server.address.getAddress().getHostAddress(), server.address.getPort());
			} else {
				gamer.sendMessage(TextFormat.RED + "Этот режим временно выключен");
			}
		}
	}
}
