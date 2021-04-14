package GUI;

public class MessageTracker {
    String id;
    String message = "";
    
    public MessageTracker(String id){
        this.id = id;
    }

    public MessageTracker(String id, String msg){
        this.id = id;
        this.message = msg +" \n";
    }

    public String getId(){
        return id;
    }

    public void add(String msg){
        message = message + msg + " \n";
    }
}
