/* Преобразование КС-грамматики
Программа проверяет, является ли грамматика контекстно-свободной, порождает ли она язык,
а также выполняет преобразования к эквивалентной КС-грамматике без бесплодных и недостижимых символов, эпсилон-правил и
цепных правил.
В консоли необходимо ввести название грамматики, образец:
gr1

Программу выполнила Петрошенко Анна, стб 1032161951, НФИбд-01-16
*/


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


public class ContextFree {

    public static boolean test0(Pair pa){
        Pattern p= Pattern.compile("[A-Z]"); // в левой части нетерминальный символ A-Z
        Matcher m= p.matcher(pa.pair[0]);
        return m.matches();
    }

    // метод для проверки на принадлежность к классу КС-грамматик
    public static boolean checkIfCF(ArrayList<Pair> r) {
        for (Pair p : r) // перебираем все правила грамматики

            if ((p.pair[0].isEmpty()||!(test0(p)||p.pair[1].isEmpty()))||
                    (!(p.pair[0].length()==1 )))
                return false; // не является KC-грамматикой
        return true;
    }

    // функция ищет множество нетерминалов, порождающих терминальные строки
    public static ArrayList<String> nontermCreateTerm(Grammar gr){
        ArrayList<String> n=new ArrayList<>();
        int prevCount=-1;
        int count=0;
        String myCharsTerm = gr.vT.stream().map(e->new String(e)).collect(Collectors.joining());
        String regexp;
        while (count!=prevCount) {
            for (Pair p : gr.rules) {

                if (!n.isEmpty()) {
                    String myChars = n.stream().map(e -> new String(e)).collect(Collectors.joining());
                    regexp = "[" + myChars + myCharsTerm + "]";
                }
                else regexp = "[" + myCharsTerm + "]";

                    Pattern pat = Pattern.compile(regexp);
                    Matcher m = pat.matcher(p.pair[1]);
                    if ((m.matches()||p.pair[1].isEmpty())&!n.contains(p.pair[0])) n.add(p.pair[0]);

            }
            prevCount = count;
            count = n.size();
        }
        return n;
    }

    // функция строит новую грамматику, не содержащую бесплодные символы
    public static Grammar useful(Grammar g, ArrayList<String> nct){
        ArrayList<String> vNUs=new ArrayList<>(); // новый набор нетерминальных символов
        ArrayList<Pair> rUseless= new ArrayList<Pair>(); //  набор бесполезных правил

        for(String n:g.vN){
            if (nct.contains(n)) vNUs.add(n);
            else for (Pair p:g.rules)
                if ((p.pair[0].contains(n)||p.pair[1].contains(n))&!rUseless.contains(p)) rUseless.add(p);
        }
        ArrayList<Pair> rUs= (ArrayList<Pair>) g.rules.stream().
                filter(e->!rUseless.contains(e)).collect(Collectors.toList());
        return new Grammar(g.name+" Us",g.start,vNUs,g.vT,rUs);
    }

    // строим грамматику без недостижимых символов
    public static Grammar approachable(Grammar g){
        ArrayList<String> w=new ArrayList<>();// множество достижимых символов
        w.add(g.start); // W0={S}
        ArrayList<String> vStar=new ArrayList<>(); // V
        vStar.addAll(g.vN);
        vStar.remove(g.start);
        vStar.addAll(g.vT);

        ArrayList<Pair> rNotAp= new ArrayList<Pair>(); //  набор правил с недостижимыми символами

        int prevCount=0;
        int count=1;

        while (prevCount != count){
            for(String u:vStar)
                for (Pair p : g.rules) if (p.pair[1].contains(u)&w.contains(p.pair[0])&!w.contains(u)) w.add(u);
                prevCount=count;
                count=w.size();
            }



        ArrayList<String> vNAp= (ArrayList<String>) g.vN.stream(). //пересечение
                filter(w::contains).collect(Collectors.toList());
        ArrayList<String> vTAp= (ArrayList<String>) g.vT.stream(). //пересечение
                filter(w::contains).collect(Collectors.toList());
        ArrayList<String> vStarNotAp= (ArrayList<String>) vStar.stream(). // разность
                filter(e->!w.contains(e)).collect(Collectors.toList());
        for(String n:vStarNotAp)
            for (Pair p : g.rules)
                if ((p.pair[0].contains(n) || p.pair[1].contains(n)) & !rNotAp.contains(p)) rNotAp.add(p);

        ArrayList<Pair> rAp = (ArrayList<Pair>) g.rules.stream(). // разность
                filter(e -> !rNotAp.contains(e)).collect(Collectors.toList());

        return new Grammar(g.name+" Ap",g.start,vNAp,vTAp,rAp);
    }

// функция строит грамматику без eps-правил
    public static Grammar epsFree(Grammar g){
        ArrayList<String> n=new ArrayList<>();// eps-порождающие нетерминальные символы
        ArrayList<Pair> rEps1=new ArrayList<>();

        for(String a:g.vN) {
            for (Pair r:g.rules){
            if (r.pair[0]==a&&r.pair[1].isEmpty()&&a!=g.start&!a.isEmpty()) {
                n.add(a);
                if (!rEps1.contains(r))rEps1.add(r);// правила A->eps
            }
            }
        }

        int prevCount=-1;
        int count=n.size();

        while(prevCount!=count){
            for (Pair p:g.rules){
                if (n.contains(p.pair[1])&!n.contains(p.pair[0])) n.add(p.pair[0]);
            }
            prevCount=count;
            count=n.size();
        }

        ArrayList<Pair> rEpsFr = (ArrayList<Pair>) g.rules.stream(). // разность всех правил и eps-правил
                filter(e -> !rEps1.contains(e)).collect(Collectors.toList());

        ArrayList<Pair> rEpsFr2=new ArrayList<>(); // новые правила
        for (Pair p:rEpsFr) {
            if(!n.isEmpty()){
            String myChars = n.stream().map(e -> new String(e)).collect(Collectors.joining());
            String regexp = "[" + myChars + "]*";
            Pattern pat = Pattern.compile(regexp);
            Matcher m = pat.matcher(p.pair[1]);
            if (m.find()) {
                ArrayList<String> nIn = (ArrayList<String>) g.vN.stream().
                        filter(e -> p.pair[1].contains(e)).collect(Collectors.toList());
                Set<String> strs = ImmutableSet.copyOf(nIn);
                Set<Set<String>> combs = Sets.powerSet(strs); //все комбинации нетерминалов данного правила, порождающих eps
                for (Set<String> set : combs) {
                    String remove=new String();
                    for (String s:set){
                        remove=remove+s;
                    }
                    if(!remove.isEmpty()) {
                        String bet = p.pair[1].replace(remove, "");
                        Pair nP = new Pair(p.pair[0], bet);

                        if (!(bet.isEmpty()) & !(rEpsFr2.contains(nP))) rEpsFr2.add(nP);
                    }

                }
            }
        }
        }

        ArrayList<String> vNEpsFr=g.vN;
        String start=g.start;

        if (!n.contains(g.start)){
            String exStart=start;
            start=start+"'";
            vNEpsFr.add(start);
            rEpsFr2.add(new Pair(start,exStart)); // правило S'->S
        }
        Pair newP=new Pair(start,"");
        rEpsFr2.add(newP); // правило S'->"", либо S->""


        return new Grammar(g.name+" epsFree",start,vNEpsFr,g.vT,rEpsFr2);
    }

    //строим грамматику без цепных правил
   public static Grammar unchain(Grammar g){

       Map<String, ArrayList<String>> nA = new TreeMap<>();
        for (String a:g.vN){
            ArrayList<String> cs=new ArrayList<>();
            cs.add(a);
            int prevcount=0;
            int count=1;
            while (prevcount!=count){
                for(Pair p:g.rules)
                    if (!cs.isEmpty()&&cs.contains(p.pair[0])&&g.vN.contains(p.pair[1])&& !cs.contains(p.pair[1]))
                        cs.add(p.pair[1]);
                prevcount=count;
                count=cs.size();
            }
            nA.put(a,cs);
        }

       ArrayList<Pair> rCh=new ArrayList<>();

       for (Pair p:g.rules){
           String b=p.pair[0];
           String alp=p.pair[1];
           for(Map.Entry<String, ArrayList<String>> item : nA.entrySet()){
               String myChars = g.vT.stream().map(e -> new String(e)).collect(Collectors.joining());
               String regexp = "[" + myChars + "]";
               Pattern pat = Pattern.compile(regexp);
               Matcher m = pat.matcher(alp);
               if(item.getValue().contains(b)&&m.find()){
                   Pair tempP=new Pair(item.getKey(),alp);
                   if (!rCh.contains(tempP)) rCh.add(tempP);
               }
           }
       }
        return new Grammar(g.name+" Chainless", g.start, g.vN, g.vT,rCh);
   }

    public static void main(String[] args) {
        // задаём правила грамматик
        ArrayList<Pair> r0= new ArrayList<Pair>();  // GR0   нет языка
        String[] n0={"S","A","B","C"};
        ArrayList<String> vN0=new ArrayList<>();
        Collections.addAll(vN0,n0);
        String[] t0={"a","b","c"};
        ArrayList<String> vT0=new ArrayList<>();
        Collections.addAll(vT0,t0);
        Grammar gr0=new Grammar("gr0", "S",vN0,vT0, r0);
        r0.add(new Pair("S", "ab"));
        r0.add(new Pair("S", "AC"));
        r0.add(new Pair("A", "AB"));
        r0.add(new Pair("B", "b"));
        r0.add(new Pair("C", "cb"));

        ArrayList<Pair> r0_= new ArrayList<Pair>();  //GR1  не является КС
        String[] n0_={"S","A","B"};
        ArrayList<String> vN0_=new ArrayList<>();
        Collections.addAll(vN0_,n0_);
        String[] t0_={"a","b"};
        ArrayList<String> vT0_=new ArrayList<>();
        Collections.addAll(vT0_,t0_);
        Grammar gr1=new Grammar("gr1", "S",vN0_,vT0_, r0_);
        r0_.add(new Pair("Sa", "AB"));
        r0_.add(new Pair("A", "aA"));
        r0_.add(new Pair("A", ""));
        r0_.add(new Pair("B", "bB"));
        r0_.add(new Pair("B", ""));

        ArrayList<Pair> r1= new ArrayList<Pair>();   // GR2  устраняются все правила
        String[] n1={"S","A","B"};
        ArrayList<String> vN1=new ArrayList<>();
        Collections.addAll(vN1,n1);
        String[] t1={"a","b"};
        ArrayList<String> vT1=new ArrayList<>();
        Collections.addAll(vT1,t1);
        Grammar gr2=new Grammar("gr2", "S",vN1,vT1, r1);
        r1.add(new Pair("S", "A")); //
        r1.add(new Pair("A", "B"));
        r1.add(new Pair("B", "Ba"));
        r1.add(new Pair("B", "b"));


        ArrayList<Pair> r4= new ArrayList<Pair>(); //GR3  нет языка
        String[] n4={"S","A","B"};
        ArrayList<String> vN4=new ArrayList<>();
        Collections.addAll(vN4,n4);
        String[] t4={"a","b"};
        ArrayList<String> vT4=new ArrayList<>();
        Collections.addAll(vT4,t4);
        Grammar gr3=new Grammar("gr3", "S",vN4,vT4, r4);
        r4.add(new Pair("S", "AB"));
        r4.add(new Pair("A", "aA"));
        r4.add(new Pair("A", "a"));
        r4.add(new Pair("B","b"));

        ArrayList<Pair> r5= new ArrayList<Pair>();  // GR4    строится эквивалентная грамматика
                                                // без бесплодных и недостижимых символов, эпсилон-правил и
                                                // цепных правил
        String[] n5={"E","T","F","G","H"};
        ArrayList<String> vN5=new ArrayList<>();
        Collections.addAll(vN5,n5);
        String[] t5={"c","d","a","b","n","m","h"};
        ArrayList<String> vT5=new ArrayList<>();
        Collections.addAll(vT5,t5);
        Grammar gr4=new Grammar("gr4", "E",vN5,vT5, r5);
        r5.add(new Pair("E", "T"));
        r5.add(new Pair("E", "EcT"));
        r5.add(new Pair("E", "EdT"));
        r5.add(new Pair("E", ""));
        r5.add(new Pair("T", "F"));
        r5.add(new Pair("T", "FaT"));
        r5.add(new Pair("T", "FbT"));
        r5.add(new Pair("T", ""));
        r5.add(new Pair("F", "G"));
        r5.add(new Pair("F", "Fn"));
        r5.add(new Pair("F", "n"));
        r5.add(new Pair("G","Gm"));
        r5.add(new Pair("H", "Hh"));
        r5.add(new Pair("H", "h"));

        Grammar grammars[]= new Grammar[]{gr0, gr1, gr2, gr3, gr4};

        Map<String, Grammar> grammarMap= new TreeMap<>();

        for (Grammar g:grammars)grammarMap.put(g.name, g);

        Scanner in = new Scanner(System.in);
        System.out.print("Input a name of the grammar (gr0, gr1, gr2, gr3 or gr4): ");
        String name = in.nextLine();
        Grammar g=grammarMap.get(name);


            if (checkIfCF(g.rules)) {
                System.out.println( g.name+" is context free"); // правильный тип грамматики
                ArrayList<String> nCT = nontermCreateTerm(g); // существование языка

            if (nCT.contains(g.start)) {

                g=useful(g,nCT);
                g=approachable(g);
                g=epsFree(g);
                g=unchain(g);
                System.out.println(g.name);
                System.out.print("VN: ");
                for (String non:g.vN) System.out.print(non+" ");
                System.out.println();
                System.out.print("VT: ");
                for (String ter:g.vT) System.out.print(ter+" ");
                System.out.println();
                for (Pair pa:g.rules) System.out.println(pa.pair[0]+"->"+pa.pair[1]);
            }
            else System.out.println("Grammar has no language!");


        }
        else System.out.println("Mistake! "+g.name+" is not context free");


    }

}