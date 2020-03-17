# Courier

This is a full stack web development project where I plan to create a messaging web app.

## Technologies

Front-end: ReactJS with CSS (eventually SCSS) \
Back-end: Java (probably with Spring framework) \
Database: MongoDB for messages/chats, MySQL for user accounts, authentication etc. 

## Things to explore

Messages DB is implemented using MongoDB. I have read about scaling issues once you have very large amounts of data. I should investigate whether Cassandra would be a good alternative for this.

## Further aspirations

Once a significant amount of development has been done (I have a working MVP), I want to start hosting on AWS. When I do this, I want to use Docker and Jenkins for easy and automated deployments. I also want to start looking into using Grafana for monitoring and see what the AWS SDK has in terms of monitoring.

I would like to write some C++ for potentially slow operations (search, cryptography etc.) but unsure as of yet whether this is a good idea or not. Need to investigate about compiling C++ for linux and the Java Native Interface (JNI) for calling this from the Java back-end.

I would love to use the tensorflow library (or some other ML library) for Python with the chat data to see if anything interesting can be done there eg. chat bot, learning predictive suggestions etc. If I am doing this, I also want to look at implementing some kind of dictionary based compression, and using some kind of algorithm to optimise this (probably doesn't need to be ML though).
