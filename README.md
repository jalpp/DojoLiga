# DojoLiga

This repo contains source code for the backend of DojoLiga, a chess league management system. 
Also foundation code for ChessDojo.club's round robin system
## Tech Stack

- Java 21
- MongoDB
- Maven
- AWS

## Integrations
- Discord API 
- Lichess API 
- ChessCom API 
- Github API


## Features:
- Creating yearly/monthly/daily arena/swiss Lichess leagues for a Lichess team
- Creating yearly/monthly/daily arena/swiss Chess.com leagues for a chess.com club
- Leaderboards with different point systems (grandprix/normal)
- foundation for round robin system API for ChessDojo.club 
- Ticket management for users to be able to communicate with admins, tech tickets for devs (Github issues)
- Anti-cheat detection system

## User docs

```TDdocs.md``` contain tournament director/admin and user
command docs 

## Set up

to run the Discord side for admins to create the tournaments must have the env variables for the following
- ```DOJ0_LIGA_BETA ``` the Discord bots token
- ```LICHESS_BOT_TOKEN```the Lichess bots token

to run Discord to Github Ticket system set
- ```GITHUB_PROD_TOKEN``` the Github token for the bot to create issues

to run the full chess league on Lichess/Chess.com set
- ```CONNECTION_STRING``` your MongoDB db connection string

to run the logical Round robin API need to have

- AWS SAM CLI
- AWS account
- AWS API GATEWAY
- ```CONNECTION_STRING``` your MongoDB db connection string

## Authors:

- @jalpp main developer of the backend system for DojoLiga
- @jackstenglein main developer of frontend ChessDojo.club site, also helping with API integration



## License:
DojoLiga has unknown license, any code/logic that is used/modified, the devs must ask for permission and consult with authors

