import java.util.ArrayList;

public class Grammar {

    ArrayList<Pair> rules;// правила
    String start; // начальный символ

    Grammar( ArrayList<Pair> r, String s){
        start=s;
        rules=r;
    }
}

