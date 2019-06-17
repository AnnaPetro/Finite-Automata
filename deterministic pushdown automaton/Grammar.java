import java.util.ArrayList;

public class Grammar {

    String start;
    ArrayList<String> vN; // нетерминальные символы
    ArrayList<String> vT; // терминальные символы
    ArrayList<Pair> rules;
    // объект класса имеет поля "имя" и "правила"
    Grammar( String s, ArrayList<String> nt,ArrayList<String> t, ArrayList<Pair> r){

        start=s;
        vN=nt;
        vT=t;
        rules=r;
    }
}
