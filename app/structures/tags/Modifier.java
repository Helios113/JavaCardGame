package structures.tags;

/**
 * The Modifier class.
 * Takes care of the modifier tags
 * for more information on modifiers please refer to
 * README.md.
 * 
 * @author Preslav Aleksandrov
 */
public class Modifier extends Tag {
    String who;
    char attr; // the attribute targeted
    int val; // the value used

    public Modifier(String name, String who, char attr, int val) {
        super(name);
        this.who = who;
        this.attr = attr;
        this.val = val;
    }

    public String toString() {
        return String.format("%s{%s %c %d}", name, who, attr, val);
    }
}
