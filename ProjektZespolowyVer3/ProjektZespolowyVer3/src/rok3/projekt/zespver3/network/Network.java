package rok3.projekt.zespver3.network;

import rok3.projekt.zespver3.network.packet.CharacterState__;
import rok3.projekt.zespver3.network.packet.CreateRoom__;
import rok3.projekt.zespver3.network.packet.JoinRoom__;
import rok3.projekt.zespver3.network.packet.KillRecord__;
import rok3.projekt.zespver3.network.packet.LoginInfo__;
import rok3.projekt.zespver3.network.packet.RegistrationInfo__;
import rok3.projekt.zespver3.network.packet.RoomChat__;
import rok3.projekt.zespver3.network.packet.ShootEvent__;
import rok3.projekt.zespver3.network.packet.__CharacterState;
import rok3.projekt.zespver3.network.packet.__EndGame;
import rok3.projekt.zespver3.network.packet.__GameInitializer;
import rok3.projekt.zespver3.network.packet.__PlayerLeft;
import rok3.projekt.zespver3.network.packet.__PrimitiveSignal__;
import rok3.projekt.zespver3.network.packet.__RoomChatBuffor;
import rok3.projekt.zespver3.network.packet.__RoomInfo;
import rok3.projekt.zespver3.network.packet.__RoomInitializer;
import rok3.projekt.zespver3.network.packet.__RoomStatus;
import rok3.projekt.zespver3.network.packet.__ShootEvent;
import rok3.projekt.zespver3.network.packet.__Update;
import rok3.projekt.zespver3.network.packet.__UserInfo;
import rok3.projekt.zespver3.network.packet.__YourId;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
 
public class Network {
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		

		kryo.register(__PrimitiveSignal__.class);
		
		
		/* from client to server */
		kryo.register(CharacterState__.class);
		kryo.register(ShootEvent__.class);
		kryo.register(ShootEvent__[].class);
		kryo.register(CreateRoom__.class);
		kryo.register(JoinRoom__.class);
		kryo.register(LoginInfo__.class);
		kryo.register(RegistrationInfo__.class);
		kryo.register(RoomChat__.class);
		kryo.register(KillRecord__.class);
		
		/* from server to client */
		kryo.register(__Update.class);
		kryo.register(__EndGame.class);
		kryo.register(__RoomInitializer.class);
		kryo.register(__RoomInfo.class);
		kryo.register(__RoomInfo[].class);
		kryo.register(__RoomStatus.class);
		kryo.register(__YourId.class);
		kryo.register(__GameInitializer.class);
		kryo.register(__UserInfo.class);
		kryo.register(__ShootEvent.class);
		kryo.register(__ShootEvent[].class);
		kryo.register(__RoomChatBuffor.class);
		kryo.register(__CharacterState.class);
		kryo.register(__CharacterState[].class);
		kryo.register(__PlayerLeft.class);
		  
		/* related classes */
		kryo.register(short[].class);
		kryo.register(int[].class);

		kryo.register(boolean[].class);
		kryo.register(byte[].class);
		kryo.register(byte[][].class);
		    
		System.out.println("Network#registered EndPoint");
	}
}
   