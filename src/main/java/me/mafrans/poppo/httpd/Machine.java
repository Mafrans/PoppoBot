package me.mafrans.poppo.httpd;

public class Machine {
    private String id = null;
    private String ip = null;

    public Machine(String id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public String getID() {
        return id;
    }

    public String getIP() {
        return ip;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Machine && ((Machine)obj).id.equals(id) && ((Machine) obj).ip.equals(ip);
    }
}
