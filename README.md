# LaBoulangerieCore

This plugin supports minecraft 1.18 and might partially support other versions.
You can download the plugin [here.](https://github.com/LaBoulangerie/LaBoulangerieCore/releases/latest)

## BetonQuest integration

LaBoulangerieCore provides some new conditions for BetonQuest.


- Check if a player is the king of his nation, returns false if he isn't in any nation: `towny_is_king`

- Check if the player's nation has a house of nation: `towny_has_house`

- Check if there are still available houses of nation: `nation_houses_has_stocks`

## Building from sources

This project uses [Maven](https://maven.apache.org/) as its build system, once you have Maven installed you need to configure it to be able to download one of its dependencies, instructions are provided [here](https://github.com/LaBoulangerie/LaBoulangerieMmo/packages/1356101). 

Once it's configured, you can build the project by cloning the project and then, inside the project's folder, open your terminal and type `> mvn package`.

The generated JARs is located in `target/LaBoulangerieCore.jar`.