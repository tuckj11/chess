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
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAEIQHJb1AN4ARP2UaMAAtigztTMwMwA0m7jqAO7QHGsb25soS8BICCebAL6YwjUwFazsXJT1YxPTcwOLK1uZxme1Uhygx3Wmx2MwuVxuUJmDzYnG4sFeT1E9SgmWyYEoAAoMlkcpQMgBHVI5ACUj2qohelVk8iUKnU9XsKDAAFUBgT5lAAShaZhmYplGpVIyjDpagAxJCcGC8yhimA6SwwAVC0w6HHAADWyoGMH2SDA8S1-2WhmACH1ExgKAAHniNGLWZKXhj6Sp6iqoGK6SIVN7Kk9PjAFPaUMAJu1DegAKLOlTYAgFYPPUqvYrmeqBJzBYazFbqYCctabJNQbx1K0LG0wO0OzXyA3oJFmTii7Se9RhqohlD1NA+BAILMM14eiXskD6-EB-kDMW02dsqWvYy1BQcDjG1XaKehmd9ueqWoL2P4hQ+C0E4D3+Jr3ssi-Snd7g93i1BzGnq8KIfPWxJ4mSajjlgwFooOEb1rM2o2hs9SnDCT4Wu0EAdmgKGbPcWaUIOeYYPU4ROE4JZ-I2gIwKh0KbBh8RYTheGnA86AcKYXi+AE0DsJyMRynASbSHACgwAAMhAWSFCRzA+tQkbNG0XS9AY6j5GgwzfJMMBTEhKw7KC4IcA88HSjBka6b8hmrIiMImUcQLIu8sGKVQWIwAgMmKgS0myRSVJgLSAGGGe76bhyXLLnZ67npun6ygqSoBmqGoNoKNq6vqRoBkKRgQGoMBoBAzAunib7iolObhr6I4leOk5hXB1SRmlRWqAAcmVKZ4imaYZoUFm1fAyD5jAhYAIxUWWqgVvZ1a1tA9Q+Isz7QEgABeKDHBxPZVf2W4eV5aXHqV+IwBAzxhfUIDxCgIBGkgABmMBORCUqXGA92YHqsZGk0+hgkcUocBAJVlTA32-S1o3wfUQMHKD0gQD1YAALJyPdA0oOmWmEeiubjaRk1ODNaClpKC1VjMNZ1qt60WptO17d2XEnuFTIJZKd2Ligv7xI+z6vhuXrbrKUb7lGIvHnDQFuZGAWKhkqhQZgVlE3VSkIdRWW0fRwJMSxnZ0fhXYjcTJRgGRFFUXZbEMTMxvYabhv7RzPF+P4XgoOgMRxIkvv+wFvhYPJ0oI400hJpJSbtEm3Q9BpqhacMLCxhwkYuzhhOWYr9YZ3G2fPibhSa5H9X1D59hh-5Mlh0FaghZz0pi+yMCcmAgvC5hrtoPFkXi5UO4wAA4lyMsWuqmqC2XOUA1Plrg8VF1Oq6OSHR+8NV41E6tzvOv1ILqNqOjfU5Lj+OZpblTyQW5OzdTlaofTK1akz8Qs7tXacaY7fHW1p5P0S9-z1TbjzDuWduC3mfL3F82hB7VWHjKeo0gUAwMMILGeoC5bgNGpreood7yq3VhXQ+NRpjmTalrMa1tbaUUph7bi3hvY4gPP4bAiojSSTxOPG0GgI4eWUmPeOSd7A2nTpnEufdc4jUqIQ1g0j6w53QBrAuldhzVzxP5PETcaQH25kPDuXce6qIHlvGqI9JYT2YNgjKc9+4L0NEvQqq8oYVU3gA1qWi97NXwcI+sJ9Ornw3mAK+Q086jXvmTCmVNywvyWgzD+TFv5sz-pY8WJ0QEnzwcOCBxjLzeTxGPARBIkFHSSvUWx-CyyZIHBQryvCcgEgkSsV88sFEFyIXoyCCBoIaIoZGKYmw2lqCrA0WYYyACS0gqxTXCMEQIMJ9jxHNCgfKyEoQAHJtlbGGJsZIoADSbMBLUXZMBdkwjGV1LZFzdl3BgJ0ahOtiIkxtjAcijCRkzDGaoCZUybSzPmYs5ZmxVnrNOfZDYVyDkzCOU9KFax7l7NGTaW5ZyUWPOeezFhvF-AcAAOxuCcCgJwMQkzBDgCJAAbPAfmtTDBFHeZoyhjRWgdHEZItARcs4qNLv3HS4w9JTBuTaF52YumokjLymRzFBWjGFdMMVKxXLSvREAry145AoAJHAfm+iW5wyMcgkxXIzECpwhU7e1jqmT3sbPS1ai-q5VcSvKUa9PFYG8Y0kBY596dKHGy4JZ9ephIiQTW+dCJrTSfgkxadNlr1jWqkqA20f7MPqYAoNXlcnyEMTISBRTtX4lKSsAkZaUCiyLVUqWB5K2FvzYGxR+qbwoFIf09R6qfFsp+TMuZ9QFlLJgBKoi0T3kMJLGilYwLB2gpHbizwrCAiWAwT5fYAckAJDAKuicEAN0ACkICKkZTEBFBpmXW1ZcpJo3I1I9DGVI4u-LZHoCFT8fS2AEDAFXVAOAEAfJQB2P20dGq3jqtqLKl98qcLvpFV+n9lB-2AeA0C6QaqQKsq8gAK2PWgAkR6VYoEpM3UK+CTVHWit3OB5jrVWNQePe1z4cGONzi6xe2D3WQ3KmErNPavL+v8fkoZQSRYhLDf1VMeNIlRpibGymMw5o01fkmxmqb03pIOj67JDU83AALQAqjlbWloboyg0eNSG0ZX7c4o0-a3Eeo8bx7Tmq-VNQPoE6pNpT7dQk5fKT19ho0LefQsm3zFPPwTW-esegDw4hIzkX+Wma2+oapWoMF1DDXSIrdeAD0nowFejAEAAHoCFalO2Z17GXHIbKy2TObZExBeE55+ApWoDRlbAmHCEab7BfHaFwsxYFNKcSYm5J9W4yNdYpm41jbTVFL8FoUtNoTMzukMZb9v7atAcykKAkL1vBLGbLLeQ1IzMNNtXWmATQttEUgKeoMHmpUgXqLhojatO3kNa1MUDIWJpfJLJmr2AQvA-s3dusHmpECxlgMAbAX7CB5AKDAS95hr31gaDHOOCck7GCiS9tEVCu2YZ03dbgeBykGaLeT2HPcLtbiuzDvAJ3p7Lf03NwzxWKdQEraoKnPqmc887k2dn1PCm07wAGfnDPa3M9gAKIweTgHhQVhB7nsOO0DO7SJ4nsmJ2fLtkwxdQA