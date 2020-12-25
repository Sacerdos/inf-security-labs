import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CaezarCode {
    public static int englishAlfabetSize = ('z' - 'a') + 1;
    public static int russianAlfabetSize = ('я' - 'а') + 1;
    public static int numberAlfabetSize = ('9' - '0') + 1;
    private static int getType(char tmp){
        if (tmp >= 'a' && tmp <= 'z')
            return 0;
        if (tmp >= 'A' && tmp <= 'Z')
            return 1;
        if (tmp >= 'а' && tmp <= 'я')
            return 2;
        if (tmp >= 'А' && tmp <= 'Я')
            return 3;
        if (tmp >= '0' && tmp <= '9')
            return 4;
        return 5;
    }

    private static String getCodingIncodMessage(String message, int key) {
        System.out.println("Шаг кода - " + key);
        StringBuilder strBox = new StringBuilder(message.length());
        char tmp;
        int alfabetSize = 0;
        char firstCharAlfabet = 0;
        char lastCharAlfabet = 0;
        for (int i = 0; i < message.length(); i++) {
            tmp = message.charAt(i);
            int type = getType(tmp);
            switch(type){
                case 0:
                    alfabetSize = englishAlfabetSize;
                    firstCharAlfabet = 'a';
                    lastCharAlfabet = 'z';
                    break;
                case 1:
                    alfabetSize = englishAlfabetSize;
                    firstCharAlfabet = 'A';
                    lastCharAlfabet = 'Z';
                    break;
                case 2:
                    alfabetSize = russianAlfabetSize;
                    firstCharAlfabet = 'а';
                    lastCharAlfabet = 'я';
                    break;
                case 3:
                    alfabetSize = russianAlfabetSize;
                    firstCharAlfabet = 'А';
                    lastCharAlfabet = 'Я';
                    break;
                case 4:
                    alfabetSize = numberAlfabetSize;
                    firstCharAlfabet = '0';
                    lastCharAlfabet = '9';
                    break;
                default:
                    break;
            }
            if (type<5) {
                tmp += key % alfabetSize;
                if (tmp > lastCharAlfabet){
                    tmp = (char)(tmp % lastCharAlfabet + firstCharAlfabet - 1);
                }
                else if (tmp < firstCharAlfabet)
                    tmp = (char)(tmp + alfabetSize);

            }

            strBox.append(tmp);
        }
        return strBox.toString();
    }
    public static void main(String[] args) {
        Stream<String> stream = Stream.of(args[0].toLowerCase().replace(" ", "").split("")).parallel();
        Map<String, Long> wordFreq = stream
                .collect(Collectors.groupingBy(String::toString, Collectors.counting()));

        Iterator<Map.Entry<String, Long>> iterator = wordFreq.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, Long> pair = iterator.next();
            String key = pair.getKey();
            Long value = pair.getValue();
            System.out.println(key + ": " + value);
        }
        System.out.println(wordFreq.size());
        System.out.println("Кодирование");
        String coded = getCodingIncodMessage(args[0], Integer.parseInt(args[1]));
        System.out.println(args[0] + " -> " + coded);
        System.out.println("\nДекодирование");
        String decoded = getCodingIncodMessage(args[0], -1*Integer.parseInt(args[1]));
        System.out.println(args[0] + " -> " + decoded);
    }
}
