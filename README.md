# Chatroom
While running the project run in this order:
	1)Server
	2)Client
Server:
The server has to be run first while implementing.By default the port given is 6464.It prints the port Number and the Inet address once it is run. 
Port Number 6464 Inet Address 0.0.0.0/0.0.0.0
Client:

The hostname and port number has to be passed via args localhost 6464.If the user does not pass values exception is thrown. Client name is received from the user.Two threads are created and processed.The messageReceive and messsageSend call the ReceiveText and sendText classes respectively and the thread is started.

On entering your name, the client returns a message if the connection was successful.It also returns the total number of users that have connected to the server.

Enter your Name:
Client2
Successfully Connection Established
There are 0 connected clients

Enter a message option
@Client1 hi
"hi" is sent to user "Client1"

Enter a message option
who
Active Users in the channel are : Client1,Client2,

Enter a message option
@all Hi How are you
"Hi How are you" sent to all connected clients.

Enter a message option
!Client1
Client2 to Client1 is sent You are so slovenly that even a barnacle would not want to fondle you .


Enter a message option
logoff
Connection terminated successfully
You are no longer connected client1
You can exit the program now

Enter a message option
?
• logoff: sends a DISCONNECT_MESSAGE to the server
• who: sends a QUERY_CONNECTED_USERS to the server
• @user: sends a DIRECT_MESSAGE to the specified user to the server
• @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected
• !user: sends a SEND_INSULT message to the server, to be sent to the specified user

Enter a message option
Hi
Unknown Command

Above are the output for different types of inputs entered by the user. On adding users the total number of connected users and total users are printed separately in the Server.The insult_grammar.json file is located inside the InsultGrammar folder which is the input to generate insult and the Jar file is located inside the libs folder. You can add a maximum of 10 clients to send messages.
