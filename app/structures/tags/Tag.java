package structures.tags;
/**
 * The Tag class.
 * Basic tag object used by the game
 * for more information on tags please refer to
 * README.md.
 * 
 * @author Preslav Aleksandrov
 */
public class Tag {
    String name;
    public Tag(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }
    public String toString()
    {
        return name;
    }

}
