import java.util.ArrayList;

public class Grammar {
    String name;
    String start;
    ArrayList<String> vN; // нетерминальные символы
    ArrayList<String> vT; // терминальные символы
    ArrayList<Pair> rules;
    // объект класса имеет поля "имя" и "правила"
    Grammar(String n, String s, ArrayList<String> nt,ArrayList<String> t, ArrayList<Pair> r){
        name=n;
        start=s;
        vN=nt;
        vT=t;
        rules=r;
    }
}
