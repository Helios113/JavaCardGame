package structures.basic;

public class Spell {
private String name;
private int ManaCost;

	public Spell(String name, int ManaCost){
	this.name = name;
	this.ManaCost = ManaCost;
	}

	public String getSpellName(){
      //  checkTag?
	return name;
	}

	public int getManaCost(){
	return ManaCost;
    }
	
    
}
