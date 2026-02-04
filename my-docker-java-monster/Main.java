public class Main {
    public static void main(String[] args) {

        // Referencing the Monster class AND creating the object, all in one go, baby!!!
        Monster monster = new Monster("Secretly Aquatic Fire Monster", "Water");

        System.out.println(monster.getDescription());
    }
}