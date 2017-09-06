# NemesisSystem Minecraft Mod

## This mod is still in early development

Add a little more personality to the vanilla mobs in Minecraft with the NemesisSystem Mod! NemesisSystem is inspired from Shadow of Mordor’s nemesis system, and adapted for Minecraft.  Every time a player is killed by a nemesis class mob (zombies and skeletons by default) a new Nemesis is created. 

![Spawned Nemesis and Body Guard](https://i.imgur.com/LRvgNvP.png)

## Levels
Every nemesis is given a unique name and trait.  Nemeses start at level one and increase in level after every successful battle with a player or another nemesis.  The number of body guards accompanying the nemesis and the amount of health given to the nemesis is proportional to the nemesis’s level. When defeated, a nemesis will lose a level.  If a nemesis is at level one when defeated, they be permanently killed and removed from the system. 

## Traits
Every time a nemesis is promoted they will gain more enchantments on their armor and weapons along with an improved trait.    Each nemesis starts with one strength trait and has a chance to gain additional traits as they level up.  Traits can also be a weakness which can be used against a nemesis to gain the upper hand.

## GUIs

![Nemesis List GUI](https://i.imgur.com/fh28kWx.png)

![Nemesis Details](https://i.imgur.com/yVtRJcF.png)

## Discovery System

![Nemesis Discovery Books](https://i.imgur.com/1gtYTJy.png)

![Fully Undiscovered Nemesis List GUI](https://i.imgur.com/Wckrg4v.png)

## Mod Packs
This mod can be included in mod packs.  While vanilla mobs are used by default, custom mobs can be used too.  Simply add the mob’s name in the MOB_WHITELIST config option.  The other config options currently support are:
- BOOK_DROP_CHANCE_BODY_GUARD
- BOOK_DROP_CHANCE_MOB
- DISCOVERY_ENABLED
- NEMESIS_LIMIT

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
