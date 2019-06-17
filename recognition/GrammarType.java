import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarType {

    // проверка на тип 0
    public static boolean test0(Pair pa){
        Pattern p= Pattern.compile("[a-z]*[A-Z]+[a-z]*"); // в левой части есть хотя бы один нетерминальный символ A-Z
        Pattern p1= Pattern.compile("[^a-zA-Z]*"); // посторонние символы - все кроме терминальных a-z  нетерминальных
        Matcher m= p.matcher(pa.pair[0]);
        Matcher m1= p1.matcher(pa.pair[0]); // посторонних символов не должно быть ни в левой, ни в правой частях
        Matcher m2= p1.matcher(pa.pair[1]);
        return m.matches()&&!(m1.matches())&&!(m2.matches());
    }

    // проверка на тип 3
    public static boolean test3(Pair pa){
        Pattern p=Pattern.compile("[A-Z]?[a-z]?"); //Р-грамматика, выровненная влево
        Pattern p1=Pattern.compile("[a-z]?[A-Z]?"); // Р-грамматика, выровненная вправо
        Matcher m= p.matcher(pa.pair[1]);
        Matcher m1= p1.matcher(pa.pair[1]);
        return m.matches()||m1.matches();
    }

    // метод для классификации
    public static int classify(ArrayList<Pair> r) {
        int cl = -1; // не является формальной грамматикой
        for (Pair p : r) // перебираем все правила грамматики
        // если левая часть пуста, либо не выполняется условие на тип 0 и при этом правая часть не пустая
            if ( p.pair[0].isEmpty()||!(test0(p)||p.pair[1].isEmpty())) return cl; // вернём значение -1
        cl=0; // прошли через цикл, значит, это грамматика типа 0
        for (Pair p: r)
            //проверка на неукорачивающую грамматику, так как она эквивалентна КЗ-грамматике: 1<=alp<=bet либо
            // начальный символ меняется на пустую последовательность
            if (!(p.pair[0].length()>=1 && p.pair[1].length()>=p.pair[0].length() || (p.pair[0].length()==1 && p.pair[1].isEmpty())))
                return cl; //если это не верно, то возвращаем тип 0

        cl=1;// прошли через цикл, значит, это тип 1

        for (Pair p: r)
            // если длина alp > 1, то это не тип 2
            if (p.pair[0].length()>1) return cl; // возвращаем тип 1
         cl=2;// прошли через цикл, значит, это тип 2
         for (Pair p:r)
             //условие для грамматики типа 3 (справа может быть пустая последовательность символов)
             if (!(test3(p)||p.pair[1].isEmpty())) return cl; // возвращаем тип 2
         cl=3; //прошли через цикл, значит, это тип 3
        return cl;
    }


    public static void main(String[] args) {
        // задаём правила грамматик
        ArrayList<Pair> r0= new ArrayList<Pair>();
        Grammar gr0=new Grammar("Grammar №1", r0);
        r0.add(new Pair("S", "BCS"));
        r0.add(new Pair("BCB", "D"));
        r0.add(new Pair("BC", "dc"));
        r0.add(new Pair("DC", "a"));
        r0.add(new Pair("CS", "c"));
        r0.add(new Pair("AS", "a"));

        ArrayList<Pair> r0_= new ArrayList<Pair>();
        Grammar gr1=new Grammar("Grammar №2", r0_);
        r0_.add(new Pair("S", "aSBC"));
        r0_.add(new Pair("S", "abC"));
        r0_.add(new Pair("CB", "BC"));
        r0_.add(new Pair("bB", "bb"));
        r0_.add(new Pair("bC", "bc"));
        r0_.add(new Pair("cC", "cc"));

        ArrayList<Pair> r1= new ArrayList<Pair>();
        Grammar gr2=new Grammar("Grammar №3", r1);
        r1.add(new Pair("S", "aSBC"));
        r1.add(new Pair("S", "abc"));
        r1.add(new Pair("bC", "bc"));
        r1.add(new Pair("CB", "BC"));
        r1.add(new Pair("cC", "cc"));
        r1.add(new Pair("BB", "bb"));

        ArrayList<Pair> r2= new ArrayList<Pair>();
        Grammar gr3=new Grammar("Grammar №4", r2);
        r2.add(new Pair("S", "AB"));
        r2.add(new Pair("A", "a"));
        r2.add(new Pair("B", "Bb"));
        r2.add(new Pair("B", ""));

        ArrayList<Pair> r3= new ArrayList<Pair>();
        Grammar gr4=new Grammar("Grammar №5", r3);
        r3.add(new Pair("S", "aS"));
        r3.add(new Pair("S", "bS"));
        r3.add(new Pair("S", "aA"));
        r3.add(new Pair("A", "aA"));
        r3.add(new Pair("A", "bA"));
        r3.add(new Pair("A", ""));

        ArrayList<Pair> r4= new ArrayList<Pair>();
        Grammar gr5=new Grammar("Grammar №6", r4);
        r4.add(new Pair("", "B$%5CS"));
        r4.add(new Pair("%", "D"));

        Grammar grammars[]= new Grammar[]{gr0, gr1, gr2, gr3, gr4, gr5};

        for (Grammar g:grammars){
                System.out.println( g.name+" has "+classify(g.rules) + " grammar type.");
        }

    }
}