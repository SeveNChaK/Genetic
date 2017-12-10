import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GeneticSolverTest {

    @org.junit.Test
    public void fillKnapsackGenetic() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(new Item(8, 10));
        items.add(new Item(5, 12));
        items.add(new Item(6, 8));
        items.add(new Item(10, 15));
        items.add(new Item(4, 2));

        Random random = new Random();

        Test test = new Test(items, 30);
        test.runTest();
        Integer testsQuantity = 30;
        for (int i = 0; i < testsQuantity; i++) {
            test = new Test(10 + random.nextInt(15));
            test.runTest();
        }
    }

    private List<Item> generateItems() {
        List<Item> list = new ArrayList<>();
        Random random = new Random();
        Integer itemQuantity = 5 + random.nextInt(20);
        for (int i = 0; i < itemQuantity; i++) {
            list.add(new Item((1 + random.nextInt(9)), (1 + random.nextInt(9))));
        }
        return list;
    }

    private class Test {
        Integer maxLoad;
        List<Item> items;
        GeneticSolver geneticSolver;
        Algorithms algoritms = new Algorithms();


        public Test(Integer load) {
            this.items = generateItems();
            this.maxLoad = load;
        }

        public Test(List<Item> items, Integer maxLoad) {
            this.items = items;
            this.maxLoad = maxLoad;
        }

        public void runTest() {
            geneticSolver = new GeneticSolver(items, maxLoad);

            Fill greedyResult = algoritms.fillKnapsackGreedy(maxLoad, items);
            Fill dynamicResult = algoritms.fillKnapsackDynamic(maxLoad, items, new HashMap<>());
            Fill geneticResult = geneticSolver.fillKnapsackGenetic(maxLoad, items);
            Integer g = 0;
            Integer d = 0;
            Integer gree = 0;
            for (Item item : geneticResult.getItems()) {
                g += item.getWeight();
            }
            for (Item item : dynamicResult.getItems()) {
                d += item.getWeight();
            }
            for (Item item : greedyResult.getItems()) {
                gree += item.getWeight();
            }
            System.out.println("Dynamic ves: " + d + " : " + "Greedy ves: " + gree + " : " + "Genetic ves: " + g + " / " + maxLoad);
            System.out.println("       cost: " + dynamicResult.getCost() + "         cost: " + greedyResult.getCost() + "          cost: " + geneticResult.getCost());
            if (dynamicResult.getCost() < geneticResult.getCost()) {
                System.out.println("ПЕРЕВЕС");
                System.out.println(items);
                System.out.println("Genetic: " + geneticResult.getItems());
                System.out.println("Dynamic: " + dynamicResult.getItems());
                System.out.println("Greedy: " + greedyResult.getItems());
            }
            System.out.println("");
        }
    }
}