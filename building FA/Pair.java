public class Pair {
    String alpha;
    String[] beta;

    Pair (String a, String t, String n){
        alpha=a;
        beta= new String[]{t, n};
        // alpha - альфа, beta - бета, t - терминальный символ либо пустой, n - нетерминальный либо пустой
        // Правило: альфа-> бета

    }
}

