# Team PRAMS
## ITSDTeamProject

## The PRAMS tag language
Tags are divided into three major groups:
1. Events
2. Modifiers
3. Parameters

These carry various functionality with them and a table containing all card (excluding highlight and ai) tags is shown below.

| Events | Modifiers | Parameters | Who | What |
|---|---|---|---|---|
| onCast{`<who>,<list of modifiers>`} | add{`<who>,<what>,<value>`} | provoke | me := this unit | h:=health no cap |
| onSummoned{`<who>,<list of modifiers>`} | set{`<who>,<what>,<value>`} | ranged | avatar := avatar on the same team as unit | j :=health with cap |
| onDamageTaken{`<who>,<list of modifiers>`} |  | airdrop | enemy_avatar :=avarar on enemy team | a:=damage |
| onDeath{`<who>,<list of modifiers>`} |  | flying | enemy := all enemy units incl avatar | m:=mana |
|  |  | attack2 | friendly := all friendly units incl avatar | c := card |
|  |  |  | player := firendly player |
|  |  |  | enemy_player := enemy player|

The tags are stored in the config file of each card/unit and are read at compile time.   
### Events
Event tags implement behavior which covers events which can happen during a round in this game implementation. Whenever one of these events occurs, such as a unit taking damage, all units with an onDamageTaken tag are called. The game then decides if this tag applies in this scenario and proceeds by execution a list of modifiers contained in the Event tag.
### Modifiers
Modifier tags implement behavior which changes some value in the game. Two exist currently 'add' and 'set'. These modifiers can be used directly ex. for spells or be contained in events as part of a Event tag.
### Parameters
Parameter tags are the simplest tag. They serve as a flag which tells the game that a unit or card can do a particular action.
### Who
Who is a system of local and global naming based on context.
Both Events and Modifiers hold a who parameter which is a local representation of who the unit's actions affect. Once a event or modifier is executed this local naming is converted to a global name by the Who class. This new global name is then used to determine the proper behavior.
### What
What is an argument used by modifiers. It tells the modifier what attribute to modify. 