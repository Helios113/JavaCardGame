package structures.basic;
import structures.tags.*;
import java.util.ArrayList;
/**
 * This is the parent class to Card and Unit
 * This class implements the tag system and provides
 * methods to use tags
 */
public class Taggable{
    ArrayList<Tag> tags;
   
    public Taggable()
    {
        tags = new ArrayList<Tag>();
    }
    public void setTags(String tags) {
        this.tags = TagParser.parseTags(tags);
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
    public Tag getTagByName(String name)
    {
        int i = checkTagByName(name);
        return i>-1?tags.get(i):null;
    }
    public int checkTagByName(String name) {
        for (Tag t : this.tags) {
            if(t.getName().equals(name))
            {
                return tags.indexOf(t);
            }
        }
        return -1;
    }
	public boolean checkTag(String name) {
        for (Tag t : this.tags) {
            if(t.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
    public boolean removeTagByName(String name)
    {
        return removeTagByIndex(checkTagByName(name));
    }
    public boolean removeTagByIndex(int i)
    {
        if(i<tags.size() && i>=0) {
            this.tags.remove(i);
            return true;
        }
        return false;
    }

}
