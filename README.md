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

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Web API Sequence Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAEIQHJb1AN4ARP2UaMAAtigztTMwMwA0m7jqAO7QHGsb25soS8BICCebAL6YwjUwFazsXJT1YxPTcwOLK1uZxme1Uhygx3Wmx2MwuVxuUJmDzYnG4sFeT1E9SgmWyYEoAAoMlkcpQMgBHVI5ACUj2qohelVk8iUKnU9XsKDAAFUBgT5lAAShaZhmYplGpVIyjDpagAxJCcGC8yhimA6SwwAVC0w6HHAADWyoGMH2SDA8S1-2WhmACH1ExgKAAHniNGLWZKXhj6Sp6iqoGK6SIVN7Kk9PjAFPaUMAJu1DegAKLOlTYAgFYPPUqvYrmeqBJzBYazFbqYCctabJNQbx1K0LG0wO0OzXyA3oJFmTii7Se9RhqohlD1NA+BAILMM14eiXskD6-EB-kDMW02dsqWvYy1BQcDjG1XaKehmd9ueqWoL2P4hQ+C0E4D3+Jr3ssi-Snd7g93i1BzGnq8KIfPWxJ4mSajjlgwFooOEb1rM2o2hs9SnDCT4Wu0EAdmgKGbPcWaUIOeYYPU4ROE4JZ-I2gIwKh0KbBh8RYTheGnA86AcKYXi+AE0DsJyMRynASbSHACgwAAMhAWSFCRzA+tQkbNG0XS9AY6j5GgwzfJMMBTEhKw7KC4IcA88HSjBka6b8hmrIiMImUcQLIu8sGKVQWIwAgMmKgS0myRSVJgLSAGGGe76bhyXLLnZ67npun6ygqSoBmqGoNoKNq6vqRoBkKRgQGoMBoBAzAunib7iolObhr6I4leOk5hXB1SRmlRWqAAcmVKZ4imaYZoUFm1fAyD5jAhaUWgpaShW9nVrW0D1D4izPtASAAF4oMcHE9lV-Zbh5XlpceLURdVkr1NecgoL+8Qrke8jxZFXrbrKUb7lGz7-vVg5WfWAWKhkqhQZgAOtUpCHUVltH0cCTEsZ2dH4V2I25uNpEwOR02IdacOo+hz5I7hKPsd2XGeN4fj+F4KDoDEcSJHTDMBb4WDydK8H1A00hJpJSbtEm3Q9BpqhacMLCxhwkaI9h6CEeilQA-UUtxrLxPy4UEPHX63kyez-kG-eQVqCFJ7hUyCVXTAnJgPdj6azhL2XQO731AA4ly30Wuqmr3STOWxka92FcVpXla6OQHR+o1haOTUW5DNT1Pd0idT1YB9TkA0oOmWmK8RmNgAWTgAIxUWWqjzVWMw1nWK1rRaG3bbtFOmBub26w1adnX9F2HfUMvcLez6O3+2gu4dSX1NIKAj4YocZb38hJ6NKtScbYAg2DOseZGUzmW16IYyUJfYxRJZ7ZTPE0ziB7+NgipGpJeIwB7NoaJz+-1g0HuCyLewNpJbSw1phLWhcgJuUjGrGW9Y5Y4XBtApWQ5PJ62QDkfyeJTY0jXlbV67JbZcgdgg9AU9Y6VB3O-b2S9-ZOwVpgPUwcfaWg4J1EqZUnRRywJ3N23cE4TjXj-VOP0M69W4bnfOmZ0aVHkqXCuM0ZhVxrqheuy0tRN3iC3HaXZOId2tnwuqw4RET1XudfBrtLzeTxB-MsBJyE1UoR9L2zBbFqBjjVfh78myv2judKBqJIy+O3pBBA0FkHJwPpsIBZYqwNFmDElAABJaQVYy7hGCIEGE+x4jmhQPlZCUIADkRStjDE2MkUABoCmAlqCUmAJSYSJK6oU+pJS7gwE6EfKGRcz5kUvjNaJn84kJJtCktJGSsmbByXkmp9kNiNPKTMSpIBqn43sm00pQyVgtNqZsjpXT25U14v4DgAB2NwTgUBOBiEmYIcARIADZ4CLkMG4mARRi5c2PjzVoHRAHALQLAsBzEtY6XGHpKYzSbTdOzMrZBqtQHwPodpUYELpjQpWK5QJKD44wBuviAkcBXk4PNuYmQBirF2xISihxb0nGexoc+P2LDA6MNyiwsOUoI5cMqrwo6Ri0ENTHII-xgrIxpzEVnCRqY85DUgafCahYFGzXLJWVRS16yrSYtotueiPFd0FV5FewA8EUoIVYglKA3EEjca+flM9PoHneb9Yc-0EUvJvCgHeYSkE4siQhbZyTUn1HSZkmAsKiKjTkRfXGQbxmhsmRGo5t8AiWHnj5fYjMkAJDAOmicEAs0ACkICKm8SsGIKyDSfLPt8qGvzuRqR6IkkB6tkXgJwuCn4+lsAIGAOmqAcAIA+SgDsRJKTI0oJVsC9toLO1ou7VMXt-bKBDpHWOsZ0hsUgTrUK+oAArUtaACQluBigSkZtQr9wsYPIh9sx6kLQHSt2DLqHMFoayiB7LmGhzYeHThFVo4OrjvVARzV+7CJYenNQmds7SsGgXGRY0+mTXLpXOa6rFoNw0TqqAW0dHX30Ra5Oxqfp9zdQPC80UwA2vHdIZ9W5X0uPLYYDKdGg6GltpurlHDI58spSRvWIrwMUcg3aqVcHJHyqQzG5V6G1ULTrpqxuuH8N6v2sBrxdrjwR0MBAZ4eK4DxBQKsmASAABm+Lh3QDM1KdsDDv2cbXTZls0s2yJmGhB8V9ZnNQGjK2BMOEpOIePr0pVRZ5PV0w0p7Drm4zudYoRs1-KVrYC0PiG1y6B2+YY4678MAmh9oHTASALHzWmqEfCnFB6j0+vCf6kD9b9KTrC1jHGV8U3UwCF4ft2bc09c1IgWMsBgDYF7YQPIBQPnf28zzPmAshYi2MJAqrIFpjbvckavWIBuB4Hsclyl11dtQH28B19Q28AleM0YbQegDCVbeNV-Fx26t+p3Y1lOzWFWyOLv03GhGgA
