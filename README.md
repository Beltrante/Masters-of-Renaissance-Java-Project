# Masters of Renaissance :flower_playing_cards: :game_die:

<div align="center">

<img src="deliverables/utils/logo.png?raw=true" alt="Masters of Renaissance" width="500">

**Java** implementation of the board game *Masters of Renaissance*

</div>

## Authors

- **Marco Bendinelli** ([@MarcoBendinelli](https://github.com/MarcoBendinelli))
- **Andrea Bosisio** ([@andreabosisio](https://github.com/andreabosisio))
- **Matteo Beltrante** ([@Beltrante](https://github.com/Beltrante))

## The Game

In Masters of Renaissance, you step into the shoes of a prominent citizen of Florence, striving to increase your fame and prestige.

Take resources from the market to purchase new cards, expand your influence in the city and surrounding territories, and demonstrate your devotion to the Pope for added benefits.

Each card grants production power to transform resources, allowing you to strategically manage your strongbox. With interesting decisions and a straightforward ruleset, you'll lead your noble family through the Renaissance, earning prestige and honor. The player with the most prestige at the end wins the game.

The official Game offered by *Cranio* can be found here [here](https://craniocreations.it/en/product/master-of-renaissance) and the full game rules can be consulted [here](deliverables/utils/rules.pdf)

## Functionalities

The project involves developing a software version of the board game _Masters of Renaissance_ in Java, following a **Software Engineering** approach. This includes creating initial high-level _UML_ diagrams of the application and final UML diagrams showing the software design.

The game rules are described in the [rules](deliverables/utils/rules.pdf) file, covering games with 1 to 4 players, including solo play without connecting to the server. Each player is identified by a nickname set on the client side, which must be unique in each game. The server ensures the uniqueness of nicknames during player acceptance.

The project implements a distributed system consisting of a single server capable of managing one game at a time and multiple clients (one per player) who can participate in only one game at a time. The entire system is designed using the **MVC** (Model-View-Controller) pattern.

The software is capable of **persistence**; the state of a game is saved to disk so that the game can resume even after the server execution is interrupted. Players must reconnect to the server using the same nicknames once it is back online to resume a game. It is assumed that the server does not interrupt its execution during disk saving, and that the disk provides entirely reliable memory.

Another important feature is that **disconnected** **players** can reconnect later and continue the game. While a player is not connected, the game continues by skipping that player's turns.

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | ✅ |
| Complete rules | ✅ |
| Socket | ✅ |
| CLI | ✅ |
| GUI | ✅ |

| Advanced Functionality | State |
|:-----------------------|:------------------------------------:|
| Resilience to disconnection | ✅ |
| Persistence | ✅ |
| Local Game | ✅ |
| Multiple Games | ❌ |
| Parameters Editor | ❌ |

## CLI Screens

<p align="center">
  <img width="700" src="deliverables/utils/cli_1.png" alt="cli_1">
</p>

<p align="center">
  <img width="650" src="deliverables/utils/cli_2.png" alt="cli_2">
</p>

<p align="center">
  <img src="deliverables/utils/cli_3.png" alt="cli_3">
</p>

## GUI Screens

<p align="center">
  <img width="600" src="deliverables/utils/gui_1.png" alt="gui_1">
</p>

<p align="center">
  <img src="deliverables/utils/gui_2.png" alt="gui_2">
</p>

<p align="center">
  <img src="deliverables/utils/gui_3.png" alt="gui_3">
</p>

## Setup

- The jar file allows to start the application as both Server or Client.
- To run it as Server type the following command (as default it runs on port 1337):
    ```shell
    > java -jar Masters_of_Renaissance_MAM.jar -server
    ```
  This command can be followed by a desired port that server will be listening on.

  
- To run it as Client type the following command:
    ```shell
    > java -jar Masters_of_Renaissance_MAM.jar
    ```
    This command **MUST** be followed by one of these arguments:
  - **-cli**: to start the Command Line Interface (NOTE: Windows users must run it using WSL);
  - **-gui**: to start the Graphical User Interface;
  
  The Server's IP and port can be specified during the execution.
  
 ## Local Game
 
 To play a local game run only the Client application and:
  - for the **CLI** just follow the instructions available during execution
  - for the **GUI** go to *Settings* and select "Local Game"
  
 ## Demo Utilites
 
 To facilitate the demo of the game, a cheat functionality has been implemented. It simply adds some Resources into the StrongBox.

<details>
  <summary>Click here to see the command to insert in the Server terminal</summary>
     
    > cheat
     
</details>
 
 ## Tools
 
 * [DrawIO](http://draw.io) - UML Diagrams
 * [Maven](https://maven.apache.org/) - Dependency Management
 * [IntelliJ](https://www.jetbrains.com/idea/) - IDE
 * [JavaFX](https://openjfx.io) - Graphical Framework
 * [SceneBuilder](https://gluonhq.com/products/scene-builder/) - Visual Layout Tool for JavaFX
 
 ## License
 
 This project is developed in collaboration with [Politecnico di Milano](https://www.polimi.it) and [Cranio Creations](http://www.craniocreations.it).
 
 [license]: https://github.com/MarcoBendinelli/Software-Engineering-project-2021/blob/master/LICENSE
[license-image]: https://img.shields.io/badge/License-MIT-blue.svg
