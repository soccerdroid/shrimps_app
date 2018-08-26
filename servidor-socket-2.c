/*
    C socket server example
*/
#include <stdlib.h>
#include <stdio.h>
#include <string.h>    //strlen
#include <sys/socket.h>
#include <arpa/inet.h> //inet_addr
#include <unistd.h>    //write
#include <pthread.h> 

int main(int argc , char *argv[])
{
    int socket_desc , client_sock , c , read_size;
    struct sockaddr_in server , client;
    char client_message[2000];
    char fail_message[5] = "FAIL\n";
    char success_message[8] = "SUCCESS\n";

     
    //Create socket
    socket_desc = socket(AF_INET , SOCK_STREAM , 0);
    if (socket_desc == -1)
    {
        printf("Could not create socket");
    }
    puts("Socket created");
     
    //Prepare the sockaddr_in structure
    server.sin_family = AF_INET;
    //server.sin_addr.s_addr = inet_addr("192.168.20.1");;
    server.sin_addr.s_addr = inet_addr("192.168.0.14");;
    server.sin_port = htons( 8888 );
     
    //Bind
    if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
    {
        //print the error message
        perror("bind failed. Error");
        return 1;
    }
    puts("bind done");
    
    while (1){

        //Listen
        listen(socket_desc , 3);   
        //Accept and incoming connection
        puts("Waiting for incoming connections...");
        c = sizeof(struct sockaddr_in);
         
        //accept connection from an incoming client
        client_sock = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c);
        if (client_sock < 0)
        {
            perror("accept failed");
            return 1;
        }
        puts("Connection accepted");
         
        //Receive a message from client
        while( 1 ){
            read_size = recv(client_sock , client_message , 2000 , 0);
	    if (read_size<1)break;            
            puts(client_message);
            //int status = system (client_message);
            FILE *fp;
	    char path[255];
	    char photo_command[40] = "fswebcam";
	    
	    //executes the command sent by the client
	    int status = system (client_message);
            if (status != 0){
                write(client_sock , fail_message , strlen(fail_message));  
                puts ("fail");     
                shutdown(client_sock, SHUT_RDWR);  //was before
                close(client_sock);     //was before 
            }
            else{
                //if the client wants to take a photo
	    	if(strstr(client_message, photo_command) != NULL){
		    fp = popen("ls /home/belen/ftp/ -tp | grep -v /$ | head -1", "r");
	    	    if (fp == NULL) {
		        write(client_sock , fail_message , strlen(fail_message));  
		        puts ("fail");     
            	        shutdown(client_sock, SHUT_RDWR);
            	        break;//close(client_sock);  
		    }  
		    else{
                        while (fgets(path, sizeof(path)-1, fp) != NULL) {
		            printf("%s", path);
		        }
		
		        printf("%d",strlen(path));
		        write(client_sock , path , strlen(path));
                        puts("success");
		        pclose(fp);
	    	    } 
	    	}
           	
                
            }
	    
            close(client_sock);
            memset(&client_message[0], 0, sizeof(client_message));
            

        }
         
        if(read_size == 0){
            puts("Client disconnected");
            fflush(stdout);
        }
        else if(read_size == -1){
            perror("recv failed");
        }
         
    }
    close(socket_desc);
    return 0;
}
