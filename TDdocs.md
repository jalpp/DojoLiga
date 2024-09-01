# **Dojo League Guide**

Welcome to the DojoLigaBot Admin/User Guide! This guide covers everything from bot workings to creating leagues, adding roles, and displaying league leaderboards. The bot is simple yet powerful for Dojo tournament directors. With great power comes great responsibility!

## **The Basics**
The basics contain all commands that can be run by members. Admins should understand these as users may ask for help or get stuck.

### **/help**
Displays an embed containing all DojoLigaBot command info. This is the best way to learn what LigaBot can do and how to use it. It also shows which commands are for admins and which are for regular Discord members.

### **/verify**
Verifies a Discord user's Lichess account, providing detailed instructions. Verification is necessary for the following commands: `/profile`, `/rank`, `/score`, `/leagueregister`, `/update`. After successful verification, users get a Dojo color belt based on their rapid or classical rating. The bot explains how the rating belt is calculated.

### **/profile**
Displays a verified user's Lichess profile, including their rating and any awards. Note: The user must be verified before running this command.

### **/leagueregister**
Checks if a user is verified and gives the ChessDojo team URL for registration. Note: The bot does not check if the user is already in the team and may paste the link anyway.

### **/rank**
Gives users their ranks across all leagues. If they didn’t play in some leagues, it will give a random position near the bottom. Note: Users must be verified before checking their ranks.

### **/score**
Gives users their scores across all leagues. If they never played or scored 0 points, it will show a score of 0. Note: Users must be verified before checking their scores.

### **/update**
Updates the user's Dojo Belt according to their new Lichess rapid or classical live rating. Note: The user must have verified their Lichess account before running this command.

### **/standing**
Accepts a Lichess arena/swiss URL as a parameter and shows the standings of that tournament in Discord embeds. This is useful for posting standings from any Lichess arena/swiss URL.

### **/top10**
Accepts a rating parameter and shows a leaderboard for that rating type (e.g., blitz, rapid, classical).

### **/standingshelp**
Displays a Discord embed explaining how the leaderboard works for normal Dojo leagues, including info about tiebreaks and the scoring system.

### **/stream**
Gives the user the Dojo Twitch Live Stream URL, useful for checking in on what Kostya is doing.

### **/pairings**
Accepts a Lichess arena/swiss URL as a parameter and returns the URL containing the pairings of that tournament. This is useful for posting pairings from any Lichess arena/swiss URL.

## **The Powerful Admin Commands**
These commands can only be run by admins! Specifically, Alex Dodd, hellokostya, dmhookie, and the great nmp123. Admins, please read this section carefully before using any admin commands, as it contains parameter details not present in the bot.

### **/leagueconfigarena**
Responsible for creating any arena league in the ChessDojo Lichess team connected with Main and testing ChessDojo servers. Parameters:

- **Lichess Account**: The account used to create the tournaments.
- **League name**: The name of the league and its tournaments. For sparring leagues, include "endgame/middlegame" in the name. To override tournament names, use the format `NAME1,NAME2,NAME3` without a trailing comma. For standard tournaments, the bot follows the format `LEAGUE NAME No. + tournament number` (e.g., DojoLiga No. 1).
- **League Description**: A short description shown on individual tournaments. Keep it under 3 sentences.
- **Arena FEN**: The FEN for tournaments within the league. Use `rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1` for standard FENs. For sparring leagues, input multiple FENs in the format `FEN1,FEN2,FEN3...`.
- **Tournament Count**: The number of future tournaments (1-12).
- **Max Rating**: The upper rating limit for the tournament (e.g., 1400 for U1400 tournaments).
- **Time Start**: The start time in EST (0-23). If an invalid value is entered, the bot defaults to 1:00 PM EST.
- **Clock Time**: The time control in minutes (3-180).
- **Clock Increment**: The increment in seconds (0-180).
- **Duration**: The duration of each tournament in minutes (e.g., 20-720).
- **Interval**: The interval between tournaments (daily, weekly, monthly).
- **Day of Week**: The day of the week for tournament creation. Only valid for weekly/monthly intervals.

### **/leagueconfigswiss**
Creates any swiss league in the ChessDojo Lichess team. Parameters are the same as for arenas, except:

- **Number of Rounds**: The number of rounds (3-100).
- **Round Interval**: The break time between rounds (1-360 minutes).

### **/computescores**
Computes tournament scores for leagues. Parameters:

- **URL Parameter**: Enter the Lichess arena/swiss tournament URL generated by the bot. Only valid for league tournaments.

### **/displaystandings**
Posts leaderboards across various categories in a channel. Parameters:

- **Time control**: Select blitz, rapid, classical, or sparring.
- **Point Type**: Select point type from arena, swiss, grand prix, sparring middlegame, or sparring endgame.
