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



## Features:
- Creating yearly/monthly/daily arena/swiss Lichess leagues for a Lichess team
- Creating yearly/monthly/daily arena/swiss Chess.com leagues for a chess.com club
- Leaderboards/crosstables with different point systems (grandprix/normal)
- Round robins support, ability to create round robins on Lichess/Chess.com
- Ticket management for users to be able to communicate with teachers/teach team
- Anti-cheat detection system

## User docs

```TDdocs.md``` contain tournament director/admin and user
command docs 

## Set up

- to run the Discord side for admins to create the tournaments must have the env variables for the following
- ```DOJ0_LIGA_BETA ``` the Discord bots token
- ```LICHESS_BOT_TOKEN```the Lichess bots token

to run the full chess league and round robins you would need 

- ```CONNECTION_STRING``` your MongoDB db connection string

## Authors:

- @jalpp main developer of the backend system for DojoLiga
- @jackstenglein main developer of frontend ChessDojo.club site, also helping with API integration


