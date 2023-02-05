package structures.tags;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * The TagParses class.
 * This class takes a string representation of tags
 * and converts them to the relevant tag objects
 *  
 * @author Preslav Aleksandrov
 */
public class TagParser {

    /**
     * Takes string tags and converts them to 
     * an array list of Tag objects
     * @param tags
     * @return ArrayList<Tag>
     */
    public static ArrayList<Tag> parseTags(String tags) {

        ArrayList<char[]> C = splitText(tags.replaceAll("\\s+",""));
        ArrayList<Tag> res = extractTags(C);
        return res;

    }

    /**
     * This class breaks down the string into 
     * components
     * @param text
     * @return list of char arrays
     */
    public static ArrayList<char[]> splitText(String text) {
        ArrayList<char[]> C = new ArrayList<char[]>();
        char[] B = text.toCharArray();
        int brackets = 0;
        int l = 0;
        for (int i = 0; i < B.length; i++) {
            if (B[i] == '{')
                ++brackets;
            if (B[i] == '}')
                --brackets;
            if ((B[i] == ',' || i == B.length - 1) && brackets == 0) {
                C.add(Arrays.copyOfRange(B, l, i+(i/(B.length - 1))));
                l = i + 1;
            }
        }
        return C;
    }
    /**
     * Takes a list of char arrays and extracts the tags
     * @param A
     * @return List of Tag objects
     */
    public static ArrayList<Tag> extractTags(ArrayList<char[]> A) {
        ArrayList<Tag> ret = new ArrayList<Tag>();
        Tag item;
        String name, params;
        for (char[] tag : A) {
            name = "";
            params = "";
            int j; // index of first {
            // Extract tag name
            for (j = 0; j < tag.length; j++) {
                if (tag[j] != '{') {
                    name += tag[j];
                } else {
                    params = new String(tag,j+1,tag.length-j-2);
                    //System.out.println(params);
                    break;
                }
            }
            if (j == tag.length) {
                // create simple tag
                item = new Tag(name);
            } else {
                if (name.length() == 3) {
                    item = createModifier(name, params);
                } else {
                    item = createOnEvent(name, params);
                }
            }

            ret.add(item);
        }
        return ret;
    }
    public static Modifier createModifier(String name, String params)
    {
        /**
         * creates Modifier Tags
         */
        String[] param = params.split(",");
        return new Modifier(name, param[0], param[1].charAt(0) , Integer.parseInt(param[2]));
    }
    public static OnEvent createOnEvent(String name, String params)
    {
        /**
         * creates OnEvent Tags
         */
        //cant handle empty string
        String[] param = params.split(",",2);
        if(param.length == 2)
            return new OnEvent(name, param[0], parseTags(param[1]));
        else
            return new OnEvent(name, param[0], parseTags(""));

    }

}
