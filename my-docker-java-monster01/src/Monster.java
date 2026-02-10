public class Monster {
    private String name;
    private String type;
    private String description;

    public void Monster(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String getDescription(){
        return this.name + " is a " + this.type + "-type monster from the Neon Ark training program.";
    }
}
