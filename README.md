# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

Chess Phase 2 diagram:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmP4AAyLQAOIAJIAHKeN4fj+NA7CMjEIpwBG0hwAoMBMRAWSFFhzBOtQ-rNG0XS9AY6j5GgWaKnMay-P8HBXKBgpAc84HlsRpHfAZOw0WhZlQIpVBIjACAyeKGLSbJBJEmApJvoYu40vutSMiybK6VyN4hXeS7CjAYoSm6MpymW7xKpgKrBhqABCIYwL5ajMBinEQDAVpouKjhvHOsUOkmvrVP6ZVRlVDhulGMZxoUoGNfAyCpjA6b4aMyw5qoebzNBRYlvUejriihLFUsdFNsODXOUiKVbtl6owOxaAgNAKLgDATT6H8OzbkFzn+odx3Fig4AXVsOxdSgsZaeh8LJoN2EwAArHhBETVNNGzdA9Tio9p3MLZAIwNg3guRwKC7GtDGBRUG30uFz7xOel7XrjArxfUcDxM9GoE+YhB5PGe0amg5WgHeWQ0L1zq3U1Sl1GVBNUZG0afT1P2Yf9YC1OmACMoP8uDM3FlDCrTJe0DZOjq2Nlj3Pto5tTeeKGSqABmCOSBzVgYRlnzCRqHfBRQvIfbtHi-18k4SDow2xlVkO7Bl7O-ZDY64xLGsS0zLtDxvgBF4KDoDEcSJAnSfeb4WDyYKoH1A00gRkxEbtBG3Q9BpqhacMTuIeg7tUre-K1OMgu18htRXKT94JUl65vLK8o10hWWqhqh1UCaSDrs7MW8nFd2lsyat6hrBgcM7H1ffGJke5LaZOHLaAwNmCv5krc0wKrFGr1rMCY5oOj9cuhVHBANACqABA0DAkDD13C-1DRtwdEAoK7fR3n9EoUsYC4ScKYei4d2IdBgKxBQABZCMZBY58RROufw2BxQaiYmiFBSoNDZwAY0Vixcy72CVNXIObdTB9XKKTBkTJWJkNnguVQFRn690rAgY04AkDfzoTmEeOUDpoAnsgaeTD-6UKXtfKAmt15t03mLCBlRPbDQPsMY+YwwZn0LMrUsV91aqLXtrBBijeYuRdEoL+KBOFzFfHrfqudiE5BgBAAAZjAcRagfqNU9rArMywgmqALA0cYQT2LSALDLcIwRAggk2PEXUKA3Scj2N8ZIoA1Q5Mgosb4QTOJKlKRcGAnQrhh0wP4OAaQIwKBLig9BEZsEBA4AAdjcE4FATgYgRmCHAUSAA2eAE5DCuMMEUSWOcrZ51aB0Wh9Dl6UTblmcpSo6k70qAbY+Oy5jQkeHCLaLoplHhcUqDExzorY1YY3ekgSOG3PuXVOeDVKj8PFBwAUQSB4wE5JI-asyYAVLmNIjc3D9yW07LUWZ7jOyNVzgGKsoZwxoE0eAq2EsoG1GBqNEY41T7TVMRfI0JozQ1ixTY9azyyYXK7MC3s-Z-4-ISo+UhbityBX1mc-0cBpmzJNmbC2nilnH0iUqBJBYxgNm0QNAlMDvYkrGPExJ9RkmpLvvU-wAApFoXF2kYK6f4SwKA+wQE2MnJACQwCWutbag1EBxQ8sMP4ApIA1TzKgYsvmyzo5rLmAw+CWzRjYAQMAS1UA4AQHclANYmrjJ4o-IKuoIwo0xsoPGxNybZXSFObCDCzLagACs3VoFmRiV14pRUoGWiSG6ONGVhTeXMO5hbPk8L4QlPxfyPVAs1aYJmELyrgr8b4Bl9VFxltZX2G68Kaj1DKrMnF2802QKGkS+WuYTFjEhqWFmgSKw6j1D-eIcgYBTxgFOnwnAGy2MZcu1ySKtwcqFBTKmPqAwT1OLobg8AE3QFHaPGQagkC4OA4m4FEBmAimnQxOxHYHEsqaNG2NMHoAYj8WygKHilHTH-dG5IKAN1cz5vindaqjFkvlUewBkHoPHVg7Wes99YXz3sa5DDObYB5ugDADEPY+wEZRQ3Wdo4DDMH7pAJGmGMKsaE1O2Aepr1BK498r9MAZMCmU7AVTCn+MtoqGiyt4oBTZqw-JzTfUKhhLVXUhBDS4BFwUGkc1XgY12odd5+UiBgywGANgKN9MeowD9eYANK7GgFyLiXMuxhmFWz7Ux4BKAeR6AMA-NLRgrVMkMCaIRWh5C5bM0swLeB0a6atiEhze9VXErqWHIAA

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
