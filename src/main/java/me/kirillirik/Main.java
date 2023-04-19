package me.kirillirik;

public class Main {

    public static void main(String[] args) {
        //final Rule test = new Rule("1 или 2 или 3 и да", "check");
        //System.out.println(test.parse(Set.of("да")));

        final Window window = new Window();
        window.init();
        window.run();
        window.destroy();

        System.exit(0);
    }
}