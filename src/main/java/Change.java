import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Change {
    Map<Integer, Integer> currentCoins;
    final Map<Integer, String> grToDenominations = new TreeMap<>() {
        {
            put(500, "5 zł");
            put(200, "2 zł");
            put(100, "1 zł");
            put(50, "50 gr");
            put(20, "20 gr");
            put(10, "10 gr");
            put(5, "5 gr");
            put(2, "2 gr");
            put(1, "1 gr");
        }
    };

    public Change(Map<Integer, Integer> coins) {
        currentCoins = coins;
    }

    static public void main(String[] args) {
        Map<Integer, Integer> coins = new TreeMap<>();
        coins.put(500, 1);
        coins.put(200, 3);
        coins.put(100, 5);
        coins.put(50, 10);
        coins.put(20, 20);
        coins.put(10, 200);
        coins.put(5, 100);
        coins.put(2, 100);
        coins.put(1, 10000);

        Scanner scanner = new Scanner(System.in);
        Change change = new Change(coins);
        String input;
        while (true) {
            System.out.print("Wpisz resztę: ");
            input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }

            try {
                int changeValue = Change.parseInput(input);
                System.out.printf("Dla reszty: %.2f zł:\n", (float) changeValue / 100);

                Map<Integer, Integer> result = change.getCoins(changeValue);
                change.removeUsedCoins(result);
                change.printResult(result);

            } catch (IllegalArgumentException e) {
                System.out.println("Nieprawidłowy format pieniędzy!");
            }

        }
    }

    static public int parseInput(String input) {
        String regex = "^(\\d+([.,]\\d{2})?)\\s*(z[lł])?";
        if (input.endsWith("gr")) {
            regex = "^(\\d+)\\s*gr";
        }
        Matcher matcher = Pattern.compile(regex).matcher(input);

        if (matcher.find()) {
            String numberString = matcher.group(1);
            int value = Integer.parseInt(numberString.replaceAll("[,.]", ""));
            if (!input.endsWith("gr") && numberString.matches("\\d+")) {
                value *= 100;
            }
            return value;
        } else {
            throw new IllegalArgumentException("Wrong input!");
        }
    }

    public Map<Integer, Integer> getCoins(int value) {
        List<Map<Integer, Integer>> table = new ArrayList<>(value + 1);
        table.add(Collections.emptyMap());
        for (int i = 1; i <= value; i++) {
            table.add(i, null);
        }

        for (int i = 1; i <= value; i++) {
            for (Map.Entry<Integer, Integer> coinEntry : currentCoins.entrySet()) {
                int denomination = coinEntry.getKey();
                if (denomination <= i && currentCoins.get(denomination) > 0) {
                    Map<Integer, Integer> sub_res = table.get(i - denomination);
                    if (Objects.equals(sub_res.get(denomination), currentCoins.get(denomination))) {
                        continue;
                    }
                    int possible_size = sub_res.values().stream().reduce(0, Integer::sum);
                    int current_size = table.get(i) == null ? Integer.MAX_VALUE : table.get(i).values().stream().reduce(0, Integer::sum);
                    if (possible_size + 1 < current_size) {
                        Map<Integer, Integer> newRes = new TreeMap<>(sub_res).descendingMap();
                        newRes.merge(denomination, 1, Integer::sum);
                        table.set(i, newRes);
                    }

                }
            }
        }

        return table.get(value);
    }

    private void removeUsedCoins(Map<Integer, Integer> usedCoins) {
        for (Map.Entry<Integer, Integer> res : usedCoins.entrySet()) {
            currentCoins.replace(res.getKey(), currentCoins.get(res.getKey()) - res.getValue());
        }
    }

    private void printResult(Map<Integer, Integer> result) {
        for (Map.Entry<Integer, Integer> res : result.entrySet()) {
            System.out.println("Wydaj " + res.getValue() + " monet " + grToDenominations.get(res.getKey()));
        }
        System.out.println();
    }
}
