import java.util.ArrayList;

public class Grammar {
    String name;
    ArrayList<Pair> rules;
    // объект класса имеет поля "имя" и "правила"
    Grammar(String n, ArrayList<Pair> r){
        name=n;
        rules=r;
    }
}
