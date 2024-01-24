# Chesspresso
Description: 
Chesspresso is envisioned as an innovative and immersive platform catering to chess enthusiasts who desire a blend of conventional and unique chess experiences. 

Project Design: 
The structure of Chesspresso adheres to a client-server model, combining Swing and Java AWT for the client-side interface, Java Socket for networking, and JDBC and SQLite for database interactions. The server is responsible for juggling player connections, overseeing game sessions, and managing database interactions, while the client focuses on rendering the game interface and processing user inputs. The SQLite database serves as the repository for user accounts, gameplay history, and statistics.

Project Game Execution Details:

    Extract Folder:
    • Unzip the Chess.zip file and launch it in an Eclipse Workspace. Add SQLite to libraries:
    • Once project is open in an Eclipse Workspace, right-click on the project -> click on properties -> click on Java Build Path -> click on add JAR file -> add SQLite JDBC jar.
    
    Starting the Server:
    • Run the Matchmaking.java file. This initiates the server. In this implementation, we use thread pool to handle multiple games concurrently.
    
    Starting a game instance:
    • Run the LoginSignupScreen.java file. Multiple instances can be run simultaneously since the server supports it.
    
    How to play the chess game:
    • In the login/signup screen, enter your username and password and then proceed to click signup. This will add a salt and pepper to the beginning and end of the password and hash it using SHA 256 for security measures.
    • Once signed up, you can click the login button. This will open a message dialog that would display your ELO rating. This rating is based on your recorded match history; a new user would have an ELO rating of 800.
    • Once you continue from the dialog box, you will be taken to the game screen. The chessboard and all its pieces will be rendered already with two buttons at the bottom. Click on search for opponent to find a match. If another instance is also searching for opponent the server will pair the two clients.
    • You can play chess as per the normal rules. The turns will be indicated in the top by a status label. Once you are done you can resign - the opponent will be notified as well and both the instances will return to the default state (with all the chess pieces reset). The win and loss will be updated at the same time.
    
    Repeat Usage:
    • Repeat these required steps as desired to play again with a new opponent and boost your ELO ratings! Enjoy!
    
    Execution Flow:
    ![Execution Flow Diagram](/Users/vaishnavichellappa/Desktop/Screenshot 2024-01-23 at 11.55.52 PM.png)


     For a more detailed explanation of the execution of our project, kindly refer to the video demo.
