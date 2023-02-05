package structures.tags;
import java.util.ArrayList;


/**
 * The OnEvent class.
 * Takes care of the onEvent tags
 * for more information on onEvents please refer to
 * README.md.
 * 
 * @author Preslav Aleksandrov
 */
public class OnEvent extends Tag{
    String who; //who triggers the on event
    ArrayList<Tag> modifiers;
    public OnEvent(String name,String who, ArrayList<Tag> modifiers)
    {
        super(name);
        this.who = who;
        this.modifiers = modifiers;
    }
    public String toString()
    {
        return String.format("%s{%s %s}", name,who,modifiers.toString().replaceAll("\\[|\\]", ""));
    }

}
