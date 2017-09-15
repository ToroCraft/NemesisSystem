# NemesisSystem Minecraft Mod

### This mod is still in early development
If you have any thoughts or problems with the mod, please let us know.

### Required Dependency [ToroTraits](https://minecraft.curseforge.com/projects/torotraits).

Add a little more personality to the vanilla mobs in Minecraft with the NemesisSystem Mod! NemesisSystem is inspired from Shadow of Mordor’s nemesis system, and adapted for Minecraft.  Every time a player is killed by a nemesis class mob (zombies and skeletons by default) a new Nemesis is created.

![Spawned Nemesis and Body Guard](https://i.imgur.com/LRvgNvP.png)

## Getting Started
When the game is first started, up to half of the total allowed nemeses (default total is 16) will be created. Your first goal will be looking for discovery books to gain intel on the nemeses in your world. The easiest way to get discovery books is by killing standard mobs which have a chance of dropping them (settable with the BOOK_DROP_CHANCE_MOB config parameter). Discovery books can also be found in loot chests. The body guards that spawn with the nemesis will have a higher chance to drop books.  Once you find a book containing the location of a nemesis you can make plans to fight them.  Nemeses are not guaranteed to spawn but instead have a chance to spawn within a 100 meter radius of their location.  Nemeses will only spawn when standard mobs spawn and only if within eyesight of a player.

## Levels
Every nemesis is given a unique name and trait.  Nemeses start at level one and increase in level after every successful battle with a player or another nemesis.  The number of body guards accompanying the nemesis and the amount of health given to the nemesis is proportional to the nemesis’s level. When defeated, a nemesis will lose a level.  If a nemesis is at level one when defeated, they be permanently killed and removed from the system.

## Traits
Every time a nemesis is promoted they will gain more enchantments on their armor and weapons along with an improved trait.    Each nemesis starts with one strength trait and has a chance to gain additional traits as they level up.  Traits can also be a weakness which can be used against a nemesis to gain the upper hand.

## GUIs
Pressing the `k` key will open the Nemesis List GUI (the keyboard key used can be changed in the client settings).

![Nemesis List GUI](https://i.imgur.com/fh28kWx.png)

Clicking on a nemesis in the Nemesis List GUI will open the Nemesis Details GUI which shows more information for that particular nemesis.

![Nemesis Details](https://i.imgur.com/yVtRJcF.png)

## Discovery System

![Nemesis Discovery Books](https://i.imgur.com/1gtYTJy.png)

![Fully Undiscovered Nemesis List GUI](https://i.imgur.com/Wckrg4v.png)

## Command for OP Players and Testing
NemesisSystem provides the `nemesis_system` command to allow OP players to interact and control the nemesis system.  The sub commands are listed below: 

- __create__ <mob_type> <level>: create a new nemesis using the provided mob type (example: minecraft:zombie) and level
- __spawn__ <nemesis_name>: spawn the provided nemesis 
- __promote__ <nemesis_name>: increase the provided nemesis's level
- __demote__ <nemesis_name>: decrease the provided nemesis's level
- __give_book__: give a nemesis discovery book to the player running the command


## Mod Packs
This mod can be included in mod packs.  While vanilla mobs are used by default, custom mobs can be used too.  Simply add the mob’s name in the MOB_WHITELIST config option.  The available config options are:

- __MOB_WHITELIST__: Mobs that will be used to create nemeses. (Must extend EntityCreature)
- __BOOK_DROP_CHANCE_BODY_GUARD__: Chance a body guard will drop a discovery book (1 out of n)
- __BOOK_DROP_CHANCE_MOB__: Chance a mob will drop a discovery book (1 out of n)
- __DISCOVERY_ENABLED__: Provides the ability to disable the discovery system
- __NEMESIS_LIMIT__: Maximum number of nemeses in each dimension

## Development Environment Setup

```
git clone git@github.com:ToroCraft/NemesisSystem.git
cd NemesisSystem
gradle setupDecompWorkspace
```

To setup an Intellij environment:
```
gradle idea
```

To setup an Eclipse environment:
```
gradle eclipse
```
