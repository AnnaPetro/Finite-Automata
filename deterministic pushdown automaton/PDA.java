/* Программа осуществляет построение автомата с магазинной памятью по
заданной КС-грамматики в приведенной форме (с удаленными бесполезными и недостижимыми символами).
Программа проверяет, является ли грамматика контекстно-свободной, порождает ли она язык,
а также выполняет преобразования к эквивалентной КС-грамматике без бесплодных и недостижимых символов.

Программу выполнила Петрошенко Анна, стб 1032161951, НФИбд-01-16
*/


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class PDA {

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
        return new Grammar(g.start,vNUs,g.vT,rUs);
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

        return new Grammar(g.start,vNAp,vTAp,rAp);
    }

    public static void automat(Stack w, Grammar g){ //построение автомата, принимающего на вход слово на ленте
        Stack stack=new Stack(); // стек, изначально пуст
        int c=w.size();
        stack.push(g.start);
        System.out.println("push S   "+stack+"  "+w);
        if (w.isEmpty()){
            System.out.println("pop S   "+stack+"  "+w);
        }
        else {
                while (!w.isEmpty()) {
                    String p = (String) stack.pop();
                    switch (p) {
                        case "S":
                            System.out.println("pop S   " + stack + "  " + w);
                            if (w.size()>c/2) {
                                switch ((String) w.peek()) {
                                    case "a":
                                        stack.push("a");
                                        stack.push("S");
                                        stack.push("a");
                                        System.out.println("push aSa   " + stack + "  " + w);
                                        break;
                                    case "b":
                                        stack.push("b");
                                        stack.push("S");
                                        stack.push("b");
                                        System.out.println("push bSb   " + stack + "  " + w);
                                        break;
                                }
                            }
                            break;
                        case "a":
                            System.out.println("pop "+p+"  " + stack + "  " + w);
                            if (w.peek() == "a") {
                                w.pop();
                                System.out.println("read a " + stack + "  " + w);
                            }
                            else {
                                System.out.println("CANNOT READ");
                                return;
                            }
                            break;

                        case "b":
                            System.out.println("pop "+p+"  " + stack + "  " + w);
                            if (w.peek() == "b"){
                                w.pop();
                                System.out.println("read b " + stack + "  " + w);
                            }
                            else {
                                System.out.println("CANNOT READ");
                                return;
                            }
                            break;
                    }
                }

            System.out.println("accept " + stack + "  " + w);
            if (stack.isEmpty()) System.out.println("READ");
            else System.out.println("CANNOT READ");
        }
    }


    public static void main(String[] args) {
        // задаём правила грамматики
        ArrayList<Pair> r= new ArrayList<Pair>();
        String[] n={"S"};
        ArrayList<String> vN=new ArrayList<>();
        Collections.addAll(vN,n);
        String[] t={"a","b"};
        ArrayList<String> vT=new ArrayList<>();
        Collections.addAll(vT,t);
        Grammar g=new Grammar("S",vN,vT, r);
        r.add(new Pair("S", "aSa"));
        r.add(new Pair("S", "bSb"));
        r.add(new Pair("S", ""));


        if (checkIfCF(g.rules)) {
            System.out.println( "Grammar is context free"); // правильный тип грамматики
            ArrayList<String> nCT = nontermCreateTerm(g); // существование языка

            if (nCT.contains(g.start)) {

                g=useful(g,nCT);
                g=approachable(g);

                System.out.print("VN: ");
                for (String non:g.vN) System.out.print(non+" ");
                System.out.println();
                System.out.print("VT: ");
                for (String ter:g.vT) System.out.print(ter+" ");
                System.out.println();
                for (Pair pa:g.rules) System.out.println(pa.pair[0]+"->"+pa.pair[1]);

                System.out.println("Instruction  Stack   Tape");

                Stack stack1=new Stack();  // слово читается автоматом
                stack1.push("a");
                stack1.push("b");
                stack1.push("b");
                stack1.push("a");

                automat(stack1,g);

                System.out.println();
                Stack stack2=new Stack();  // слово не читается автоматом
                stack2.push("a");
                stack2.push("b");

                automat(stack2,g);

            }
            else System.out.println("Grammar has no language!");

        }
        else System.out.println("Mistake! Grammar is not context free");

    }

}
