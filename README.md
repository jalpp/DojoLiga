# DojoLiga

DojoLiga is an open source serverless Discord backed backend system that allows Dojo community admins to create/config and manage Lichess and Chess.com arena, swiss and round robin tournaments, while allows users to play in Dojo yearly tournaments and qualify for Dojo candidates! More info [here](https://www.chessdojo.club/tournaments?type=info)


## Tech Stack

- Java 21
- MongoDB
- Maven
- Discord API 
- Lichess API 
- ChessCom API 
- AWS API Gateway
- AWS EventBridge
- AWS lambda


## Authors:

- @jalpp main developer of the backend system for DojoLiga
- @jackstenglein main developer of frontend ChessDojo.club site, also helping with API integration


## Set up

- to run the Discord side for admins to create the tournaments must have the env variables for the following
- ```DOJ0_LIGA_BETA ``` the Discord bots token
- ```LICHESS_BOT_TOKEN```the Lichess bots token

to run the full chess league and round robins you would need 

- ```CONNECTION_STRING``` your MongoDB db connection string


## License:
DojoLiga is licensed as GPL-3.0, please read license requirements