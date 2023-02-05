package utils;
import java.util.HashMap;
import java.util.Map;
/**
 * This is a utility class that just has short-cuts to the location of various
 * config files.
 *
 * IMPORTANT: Note the start letter for unit types is u_... while the start letter
 * for card types is c_...
 *
 * @author Dr. Richard McCreadie
 *
 */

public class StaticConfFiles {

	// Board Pieces
	public final static String tileConf = "conf/gameconfs/tile.json";
	public final static String gridConf = "conf/gameconfs/grid.json";

	// Avatars
	public final static String humanAvatar = "conf/gameconfs/avatars/avatar1.json";
	public final static String aiAvatar = "conf/gameconfs/avatars/avatar2.json";

	// Deck 1 Cards
	public final static String c_truestrike = "conf/gameconfs/cards/1_c_s_truestrike.json";
	public final static String c_sundrop_elixir = "conf/gameconfs/cards/1_c_s_sundrop_elixir.json";
	public final static String c_comodo_charger = "conf/gameconfs/cards/1_c_u_comodo_charger.json";
	public final static String c_azure_herald = "conf/gameconfs/cards/1_c_u_azure_herald.json";
	public final static String c_azurite_lion = "conf/gameconfs/cards/1_c_u_azurite_lion.json";
	public final static String c_fire_spitter = "conf/gameconfs/cards/1_c_u_fire_spitter.json";
	public final static String c_hailstone_golem = "conf/gameconfs/cards/1_c_u_hailstone_golem.json";
	public final static String c_ironcliff_guardian = "conf/gameconfs/cards/1_c_u_ironcliff_guardian.json";
	public final static String c_pureblade_enforcer = "conf/gameconfs/cards/1_c_u_pureblade_enforcer.json";
	public final static String c_silverguard_knight = "conf/gameconfs/cards/1_c_u_silverguard_knight.json";

	// Deck 1 Units
	public final static String u_comodo_charger = "conf/gameconfs/units/comodo_charger.json";
	public final static String u_azure_herald = "conf/gameconfs/units/azure_herald.json";
	public final static String u_azurite_lion = "conf/gameconfs/units/azurite_lion.json";
	public final static String u_fire_spitter = "conf/gameconfs/units/fire_spitter.json";
	public final static String u_hailstone_golem = "conf/gameconfs/units/hailstone_golem.json";
	public final static String u_ironcliff_guardian = "conf/gameconfs/units/ironcliff_guardian.json";
	public final static String u_pureblade_enforcer = "conf/gameconfs/units/pureblade_enforcer.json";
	public final static String u_silverguard_knight = "conf/gameconfs/units/silverguard_knight.json";

	// Deck 2 Cards
	public final static String c_staff_of_ykir = "conf/gameconfs/cards/2_c_s_staff_of_ykir.json";
	public final static String c_entropic_decay = "conf/gameconfs/cards/2_c_s_entropic_decay.json";
	public final static String c_blaze_hound = "conf/gameconfs/cards/2_c_u_blaze_hound.json";
	public final static String c_bloodshard_golem = "conf/gameconfs/cards/2_c_u_bloodshard_golem.json";
	public final static String c_planar_scout = "conf/gameconfs/cards/2_c_u_planar_scout.json";
	public final static String c_pyromancer = "conf/gameconfs/cards/2_c_u_pyromancer.json";
	public final static String c_rock_pulveriser = "conf/gameconfs/cards/2_c_u_rock_pulveriser.json";
	public final static String c_serpenti = "conf/gameconfs/cards/2_c_u_serpenti.json";
	public final static String c_windshrike = "conf/gameconfs/cards/2_c_u_windshrike.json";

	// Deck 2 Units
	public final static String u_blaze_hound = "conf/gameconfs/units/blaze_hound.json";
	public final static String u_bloodshard_golem = "conf/gameconfs/units/bloodshard_golem.json";
	public final static String u_hailstone_golemR = "conf/gameconfs/units/hailstone_golem2.json";
	public final static String u_planar_scout = "conf/gameconfs/units/planar_scout.json";
	public final static String u_pyromancer = "conf/gameconfs/units/pyromancer.json";
	public final static String u_rock_pulveriser = "conf/gameconfs/units/rock_pulveriser.json";
	public final static String u_serpenti = "conf/gameconfs/units/serpenti.json";
	public final static String u_windshrike = "conf/gameconfs/units/windshrike.json";

	// Effects
	public final static String f1_inmolation = "conf/gameconfs/effects/f1_inmolation.json";
	public final static String f1_buff = "conf/gameconfs/effects/f1_buff.json";
	public final static String f1_martyrdom = "conf/gameconfs/effects/f1_martyrdom.json";
	public final static String f1_projectiles = "conf/gameconfs/effects/f1_projectiles.json";
	public final static String f1_summon = "conf/gameconfs/effects/f1_summon.json";



	// Additions to the template below here

	// Arrays to hold the each player's deck in order of loading
	// first card loaded is the bottom of the deck i.e. last to be drawn
	public final static String[] deck1 = {
		c_hailstone_golem,
		c_sundrop_elixir,
		c_azurite_lion,
		c_ironcliff_guardian,
		c_azure_herald,
		c_truestrike,
		c_pureblade_enforcer,
		c_comodo_charger,
		c_fire_spitter,
		c_silverguard_knight,
		c_hailstone_golem,
		c_sundrop_elixir,
		c_azurite_lion,
		c_ironcliff_guardian,
		c_azure_herald,
		c_truestrike,
		c_silverguard_knight,
		c_fire_spitter,
		c_pureblade_enforcer,
		c_comodo_charger,
	};

	public final static String[] deck2 = {
		c_hailstone_golem,
		c_planar_scout,
		c_entropic_decay,
		c_serpenti,
		c_pyromancer,
		c_windshrike,
		c_blaze_hound,
		c_staff_of_ykir,
		c_bloodshard_golem,
		c_rock_pulveriser,
		c_hailstone_golem,
		c_planar_scout,
		c_entropic_decay,
		c_serpenti,
		c_pyromancer,
		c_windshrike,
		c_blaze_hound,
		c_staff_of_ykir,
		c_bloodshard_golem,
		c_rock_pulveriser,
	};

	public static final Map<String, String> humanUnits = Map.of(
  	"Comodo Charger", u_comodo_charger,
  	"Azure Herald", u_azure_herald ,
	"Azurite Lion", u_azurite_lion ,
	"Fire Spitter",u_fire_spitter,
	"Hailstone Golem",u_hailstone_golem ,
	"Ironcliff Guardian",u_ironcliff_guardian ,
	"Pureblade Enforcer",u_pureblade_enforcer ,
	"Silverguard Knight",u_silverguard_knight );


public static final Map<String, String> aiUnits = Map.of(
  	"Blaze Hound", u_blaze_hound ,
  	"Bloodshard Golem", u_bloodshard_golem,
  	"Hailstone Golem",u_hailstone_golemR ,
  	"Planar Scout",u_planar_scout ,
  	"Pyromancer",u_pyromancer ,
  	"Rock Pulveriser",u_rock_pulveriser,
	"Serpenti", u_serpenti,
	"WindShrike", u_windshrike);

}
