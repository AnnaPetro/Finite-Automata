// Автор - Петрошенко Анна, НФИбд-01-16, 31.10.2018

import java.util.*;

public class Mini {


    // функция для удаления недостижимых состояний
    public static void reachableKnots(String s, Map<String, String[]> t, Map<String, Boolean> r) {

        int prevCount = 0;
        r.put(s, true);
        int count = 1;
        while (count != prevCount) { // если количество достижимых состояний на данном шаге не равно оному на предыдущем
            for (String k : t.keySet()) {
                if (r.get(k)) {
                    for (String i : t.get(k)) {
                        r.put(i, true);
                    }
                }
            }
            prevCount = count;
            count = count(r);
        }
        ;

        for (String k : r.keySet()) { // удаляем недостижимые состояния
            if (!r.get(k)) t.remove(k);
        }
    }


    public static int count(Map<String, Boolean> r) { // функция подсчёта достижимых состояний
        int c = 1;
        for (Map.Entry<String, Boolean> item : r.entrySet()) {
            if (item.getValue()) c++;
        }
        return c;
    }

// функция поиска эквивалентных состояний и нахождения минимального автомата
    public static Map<String, String[]> equal(Map<String, String[]> table,ArrayList<HashSet<String>> p,int[] let ) {

        HashSet<String> finish = p.get(1);
        HashSet<String> notFin = p.get(0);

        Map<String, Integer> classOfP = new TreeMap<>(); // класс состояния

        for (String key : table.keySet()) {
            if (finish.contains(key)) classOfP.put(key, 1);
            else if (notFin.contains(key)) classOfP.put(key, 0);
        }

        Map<Integer, ArrayList<String>> pOfClass = new TreeMap<>(); // все состояния в классах

        for (Map.Entry<String, Integer> item : classOfP.entrySet()) {
            if (pOfClass.containsKey(item.getValue())) pOfClass.get(item.getValue()).add(item.getKey());
            else {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(item.getKey());
                pOfClass.put(item.getValue(), temp);
            }
        }

        ArrayList<Integer[]> queue = new ArrayList<>(); // очередь
        for (int cl : let) for (Integer bigC : pOfClass.keySet()) queue.add(new Integer[]{bigC, cl});


        /*for( Integer[] iter:queue) {
            for (Integer iter2:iter) System.out.print(iter2+" ");
            System.out.println();
        }*/

        Map<String, Map<Integer, ArrayList<String>>> inv = new TreeMap<>();

        for (Map.Entry<String, String[]> item : table.entrySet()) {
            for (Integer iter : let) {
                if (inv.containsKey(item.getValue()[iter])) {
                    if (inv.get(item.getValue()[iter]).containsKey(iter)) {
                        inv.get(item.getValue()[iter]).get(iter).add(item.getKey());
                    } else {
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(item.getKey());
                        inv.get(item.getValue()[iter]).put(iter, temp);
                    }
                } else if (!item.getValue()[iter].isEmpty()) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(item.getKey());
                    Map<Integer, ArrayList<String>> tempMap = new TreeMap<>();
                    tempMap.put(iter, temp);
                    inv.put(item.getValue()[iter], tempMap);
                }
            }
        }

        Map<Integer, ArrayList<String>> involved = new TreeMap<>(); //ассоциирует номера классов с векторами из состояний

        while (!queue.isEmpty()) { //пока очередь не пуста

            Integer[] split = queue.get(queue.size() - 1); // берём из очереди пару класс-переход
            queue.remove(split); // убираем её из очереди
            for (String q : classOfP.keySet()) { // для всех q, которые принадлежат классу из split
                if (classOfP.get(q) == split[0]) {
                    if (inv.containsKey(q))
                        if (inv.get(q).containsKey(split[1]))
                            for (String r : inv.get(q).get(split[1])) { // для всех r, из которых есть переход по символу из split в q
                                Integer i = classOfP.get(r); // присваиваем i класс состояния r
                                //System.out.println(r+" "+i);
                                if (involved.containsKey(i)) involved.get(i).add(r);
                                else {
                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add(r);
                                    involved.put(i, temp);
                                }
                            }
                }
            }
            for (Integer i : involved.keySet()) {
                if (involved.get(i).size() < p.get(i).size()) {
                    p.add(new HashSet<>());
                    Integer j = p.size() - 1;
                    for (String r : involved.get(i)) {
                        p.get(i).remove(r);
                        p.get(j).add(r);
                    }
                    if (p.get(j).size() > p.get(i).size()) Collections.swap(p, i, j);
                    for (String r : p.get(j)) classOfP.put(r, j);
                    for (Integer c : let) queue.add(new Integer[]{j, c});
                }
            }
        }

        TreeMap<Integer,String> newNames=new TreeMap<>();// новые имена состояний

        for (HashSet<String> iter:p){
            for (String s:iter){
                if (newNames.containsKey(classOfP.get(s))) newNames.get(classOfP.get(s)).concat(s);
                else newNames.put(classOfP.get(s),s);
            }
        }

        Map<String, String[]> minTable = new TreeMap<>(); // минимальный автомат

        for (String ke:table.keySet()) {
            String key = newNames.get(classOfP.get(ke));
            if (!minTable.containsKey(key)) {
                if (classOfP.get(table.get(ke)[0]) != null & classOfP.get(table.get(ke)[1]) != null)
                    minTable.put(key, new String[]{newNames.get(classOfP.get(table.get(ke)[0])), newNames.get(classOfP.get(table.get(ke)[1]))});
                 else if (classOfP.get(table.get(ke)[0]) != null)
                    minTable.put(key, new String[]{newNames.get(classOfP.get(table.get(ke)[0])), " "});
                 else  minTable.put(key, new String[]{" ",newNames.get(classOfP.get(table.get(ke)[1]))});


            }
        }
        return minTable;
    }

    public static void main(String[] args) {
        String start = "A"; // Начальное состояние
        HashSet<String> finish = new HashSet<>();// Множество конечных состояний автомата
        finish.add("D");
        finish.add("E");
        int[] let = {0,1}; // символы алфавита, который распознаёт ДКА


        Map<String, Boolean> reach = new TreeMap<>(); // маркеры достижимых состояний

        reach.put("A", false);
        reach.put("B", false);
        reach.put("C", false);
        reach.put("D", false);
        reach.put("E", false);
        reach.put("F", false);
        reach.put("G", false);

        Map<String, String[]> table = new TreeMap<>(); // таблица ДКА

        table.put("A", new String[]{"B", "C"});
        table.put("B", new String[]{"", "D"});
        table.put("C", new String[]{"", "E"});
        table.put("D", new String[]{"C", "E"});
        table.put("E", new String[]{"B", "D"});
        table.put("F", new String[]{"D", "G"});
        table.put("G", new String[]{"F", "E"});

        reachableKnots(start, table, reach); // удаляем недостижимые состояния

        HashSet<String> notFin = new HashSet<>();
        for (String k : table.keySet()) if (!finish.contains(k)) notFin.add(k);

        ArrayList<HashSet<String>> p = new ArrayList<>(); //разбиение состояний на классы
        p.add(notFin);
        p.add(finish);

        Map<String, String[]> minTable=equal(table,p,let);


        System.out.print("   ");
        for (int i:let) System.out.print(i+"  ");
        System.out.println();
         for (Map.Entry<String, String[]> item : minTable.entrySet()) {
            System.out.print(item.getKey());
            for (String v : item.getValue()) System.out.print("  " + v);
            System.out.println();
        }

        }

    }

