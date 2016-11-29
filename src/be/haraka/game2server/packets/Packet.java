package be.haraka.game2server.packets;


import be.haraka.game2server.main.GameServer;

public abstract class Packet {

    public static enum PacketTypes {

        INVALID(-01), LOGIN(00), DISCONNECT(01), GETPLAYERSTATES(02);
        private int packetId;

        private PacketTypes (int packetId) {
            this.packetId = packetId;
        }

        public int getId() {
            return packetId;
        }
    }

    public byte packetId;

    public Packet(int packetId){
        this.packetId = (byte) packetId;
    }

    public abstract void writeData(GameServer server);

    //Function that reads the packet's first two digits
    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String packetId) {
        try {
            return lookupPacket(Integer.parseInt(packetId));
        } catch(NumberFormatException e) {
            return PacketTypes.INVALID;
        }
    }

    //Function that checks if the packet is valid, else it returns the invalid packet
    public static PacketTypes lookupPacket(int id) {
        for (PacketTypes p : PacketTypes.values()) {
            if (p.getId() == id) {
                return p;
            }
        }

        return PacketTypes.INVALID;
    }

}
