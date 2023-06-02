<div style="text-align:center">
  <h1>AlphaMusic - Discord Music Bot</h1>
  <img src="https://github.com/BlitzOffline/AlphaMusic/blob/master/src/main/resources/icon.png?raw=true" alt="AlphaMusic Logo">
</div>

## Build Application
### Requirements:
- [Java 11 or higher](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot)
- [Gradle 8](https://gradle.org/install/)
### Command:
`./gradlew shadowJar`

## Run Application
### Requirements:
- [Java 11 or higher](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot)
- Discord application and bot token. [Learn here how to create a discord application](https://discordpy.readthedocs.io/en/latest/discord.html). Required permissions:
    - `Read Messages/View Channels`
    - `Send Messages`
    - `Send Messages in Threads`
    - `Manage Messages`
    - `Embed Links`
    - `Use External Emojis`
    - `Use External Stickers`
    - `Add Reactions`
    - `Use Slash Commands`
    - `Connect`
    - `Speak`
    - `Use Voice Activity`

### Command:
`java -jar build/libs/AlphaMusic.jar`
#### Parameters:
- `-dt` or `--discord_token` - specify discord token - optional  
Used to link the application to a discord bot
- `-ye` or `--youtube_email` - specify YouTube email - optional  
Used for YouTube search synchronization and to allow playing of age restricted tracks
- `-yp` or `--youtube_pass` - specify YouTube password - optional

### Environment Variables:
If environment variables are not found, the application will check java system properties and if it can't find those either, it will attempt use the default value or throw an exception if value is not optional. 
- `ALPHAMUSIC_DISCORD_TOKEN` - specify discord token - required if -dt parameter is not specified
- `ALPHAMUSIC_YOUTUBE_EMAIL` - optional - default: `-dt value` or `null`
- `ALPHAMUSIC_YOUTUBE_PASSWORD` - optional - default: `-yp value` or `null`
- `ALPHAMUSIC_MYSQL_HOST` - specify MySQL host - required
- `ALPHAMUSIC_MYSQL_PORT` - specify MySQL port - optional - default: `3306`
- `ALPHAMUSIC_MYSQL_DATABASE` - specify MySQL database - required
- `ALPHAMUSIC_MYSQL_USERNAME` - specify MySQL username - required
- `ALPHAMUSIC_MYSQL_PASSWORD` - specify MySQL password - required
- `ALPHAMUSIC_MYSQL_SAVE_DELAY` - specify delay between data saving tasks - default: `30`
- `ALPHAMUSIC_MYSQL_SAVE_DELAY_TIMEUNIT` - specify the time unit to represent the save delay by - default: `MINUTES`

#### Example:
`java -DALPHAMUSIC_MYSQL_HOST=example.test -DALPHAMUSIC_MYSQL_DATABASE=alphamusic -DALPHAMUSIC_MYSQL_USERNAME=alphamusic -DALPHAMUSIC_MYSQL_PASSWORD=pass1234 -jar build/libs/AlphaMusic.jar --discord_token mydiscord.token.123`

## Runtime Console Commands:
- `app`
  #### Parameters:
  - `-h` or `--help` - show help menu
  - `-v` or `--version` - show application version
  - `-d` or `--debug` - toggle debug mode
  - `-s` or `--shutdown` - shutdown application