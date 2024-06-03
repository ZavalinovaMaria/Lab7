package lab;

import lab.client.Authentication;
import lab.client.OnlineUser;
import lab.console.Console;

public class Main {
    public static void main(String[] args) {

        Authentication auth = new Authentication(new OnlineUser());

        Console session = new Console(auth.start());
        session.toStart();
    }

}
