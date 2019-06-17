import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarToFA {
    public static Map<String, Map<String,ArrayList<String>>> nfa = new TreeMap<>(); // таблица НКА

public static void addNonterm(Grammar g, ArrayList<String> fin){//пополняет правила грамматики и ищет конечные состояния
    Iterator<Pair> iter=g.rules.iterator();
    ArrayList<Pair> pair=new ArrayList<>();
    while (iter.hasNext()) { // перебираем правила грамматики
        Pair pa=iter.next();
        if (pa.alpha==g.start&&pa.beta[0].isEmpty()) fin.add(pa.alpha); // если S-> "", то S - конечное состояние
        if (pa.beta[1].isEmpty()) { // если правило имеет вид А->a
            String a=pa.alpha;
            String t=pa.beta[0];
            String temp=""; // временный символ
            Iterator<Pair> iter2=g.rules.iterator(); // ищем правила вида A->aB
            while ((iter2.hasNext())){
                Pair pa2=iter2.next();
                if (pa2.alpha==a && pa2.beta[0]==t) temp=pa2.beta[1]; // если такие есть, запоминаем B в temp
            }
            if (!(temp.isEmpty()&fin.contains(temp))) fin.add(temp); // запоминаем В в качестве конечного состояния

            if (temp.isEmpty()) { // если не нашли правила вида A->aB
                pair.add(new Pair(pa.alpha, pa.beta[0], "N")); // запоминаем правило вида A->aN во временном списке
                if (!fin.contains("N")) fin.add("N"); //запоминаем N в качестве конечного состояния
            }

        }
    }
    g.rules.addAll(pair); // добавляем новые правила вида A->aN из временного списка
}

    public static void buildNFA(ArrayList<Pair> r) {
        for (Pair pa : r) {// перебираем правила грамматики
            if (!pa.beta[1].isEmpty()){
            if (nfa.containsKey(pa.alpha)){// содержит альфа
                if (nfa.get(pa.alpha).containsKey(pa.beta[0]) ){// содержит t
                    nfa.get(pa.alpha).get(pa.beta[0]).add(pa.beta[1]); // добавляем n
                }
                else if (!pa.beta[0].isEmpty()){ // не содержит t и t (следовательно, бета) не пустое
                    ArrayList<String> arrayList=new ArrayList<>();
                    arrayList.add(pa.beta[1]);
                    nfa.get(pa.alpha).put(pa.beta[0],arrayList); // добавляем t и n
                }
            }
            else {// не содержит альфа
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(pa.beta[1]);
                String t = pa.beta[0];
                Map<String, ArrayList<String>> map = new TreeMap<>();
                map.put(t, arrayList);
                nfa.put(pa.alpha, map); // добавляем альфа, t и n
            }
            }
        }
    }


    public static void main(String[] args) {


        // задаём правила грамматики
        ArrayList<Pair> r1 = new ArrayList<Pair>();
        Grammar gr = new Grammar( r1, "S");
        r1.add(new Pair("S", "a", "B"));
        r1.add(new Pair("S", "a", "A"));
        r1.add(new Pair("B", "b", "B"));
        r1.add(new Pair("B", "a", ""));
        r1.add(new Pair("A", "a", "A"));
        r1.add(new Pair("A", "b", ""));



        ArrayList<String> finish=new ArrayList<>();// Множество конечных состояний автомата
        addNonterm(gr, finish); // Пополняем грамматику правилами, заполняем множество конечных состояний автомата
        while (finish.contains("")) finish.remove("");
        buildNFA(gr.rules); // Строим НКА
        String start=gr.start; // Начальное состояние

        // Вывод таблицы
            ArrayList <String> term=new ArrayList<>();
            for (Map.Entry<String, Map<String,ArrayList<String>>> item: nfa.entrySet()){
                Set<String> k=item.getValue().keySet();
                for (String key:k){
                if (!term.contains(key)) term.add(key);
                }
            }
        System.out.print("   ");
            for (String t:term) System.out.print(t+"     ");

        System.out.println();

            for(Map.Entry<String, Map<String,ArrayList<String>>> item: nfa.entrySet()){
                String out="";
                for (String ke:item.getValue().keySet()) {
                    String st="";
                    for(String it: item.getValue().get(ke)) st=st+it+",";
                    st=st.substring(0,st.length()-1);
                    out =out+st+"     ";
                }

                System.out.print(item.getKey()+"  "+ out);

                // можно вывести в "сыром" виде:
                //System.out.print("   "+item.getKey()+item.getValue().entrySet());
                System.out.println();
                }
            System.out.println();
        System.out.println("Start: "+start); // Начальные состояния
        System.out.print("Finish: "); // Конечные состояния
        String fini="";
        for (String ff: finish) fini=fini+ff+",";
        fini=fini.substring(0,fini.length()-1);
        System.out.println(fini);

    }
}
