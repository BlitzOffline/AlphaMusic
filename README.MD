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
- `-dt` or `--discord_token` - specify discord token - required  
Used to link the application to a discord bot
- `-ye` or `--youtube_email` - specify YouTube email - optional  
Used for YouTube playlist and search synchronization
- `-yp` or `--youtube_pass` - specify YouTube password - optional

#### Example:
`java -jar build/libs/AlphaMusic.jar -dt 1234567890 -ye some@email.test -yp password`

## Runtime Console Commands:
- `app`
  #### Parameters:
  - `-h` or `--help` - show help menu
  - `-v` or `--version` - show application version
  - `-d` or `--debug` - toggle debug mode
  - `-s` or `--shutdown` - shutdown application