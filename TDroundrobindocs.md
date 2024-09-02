# **Tournament Round Robin System Guide**

This guide explains the commands and parameters used to configure and manage a Round Robin tournament using the system. Admins can set up tournaments, manage participants, and publish results with ease.

## **Commands**

### **/configroundrobin `<params>`**
Configures a new Round Robin tournament. The command accepts the following parameters:
- **name**: The name of the tournament.
- **description**: A short description of the tournament.
- **automode**: Automatically generate URLs for the tournament (true/false).
- **start cohort**: The starting cohort rating (0 to 2400).
- **end cohort**: The ending cohort rating (0 to 2400).

### **/register**
Registers a player for the upcoming Round Robin tournament.

### **/opentournament `<ID>`**
Opens the tournament to allow players to join. The tournament ID is required.

### **/closetournament `<ID>`**
Closes the tournament and moves it to the finished state. The tournament ID is required.

### **/publishtournament `<ID>`**
Publishes the tournament to the players, providing them with tournament info. The tournament ID is required.

### **/generatepairings `<ID>`**
Generates Round Robin pairings for the tournament. The tournament ID is required.

### **/displaypairings `<ID>`**
Displays the pairings to players. The tournament ID is required.

### **/adminaddplayer `<ID> <User> <Platform>`**
Admins can use this command to force-add a player to the tournament, overriding any system restrictions. The tournament ID, user, and platform are required.

### **/viewtournamentgames `<ID>`**
Views the games submitted for the tournament. The tournament ID is required.

### **/submitgame**
Manually calculate the players' scores. This command is admin-only.

## **Parameters**
- **name**: The tournament's name.
- **description**: A brief description of the tournament.
- **automode**: Set to true/false to automatically generate URLs for the tournament.
- **start cohort**: The starting rating cohort, ranging from 0 to 2400.
- **end cohort**: The ending rating cohort, ranging from 0 to 2400.
- **tournament ID**: A unique identifier for each tournament, used in several commands.

---

## Round Robin Tournament management lifecycle

- create a tournament using the /configroundrobin command with valid parameters above, create as many as you want as player size limit is 10 per cohort
- players must verify their accounts with /verify
- the tournament is now automatically open for players to start registration via /register
- there are 10 player max limit
- once the registration max limit is reached the same cohort players must ask TD to create a different tournament of same cohort
- players can also withdraw with /withdraw
- pairings are automatically generated but the backup command can be used to do so
- leaderboards and crosstable are auto generated
- TDs can publish a tournament in a Discord channel


Use these commands to effectively manage and participate in Round Robin tournaments. Ensure you have the correct permissions to use admin-level commands.
