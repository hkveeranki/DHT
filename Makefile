bin/Server.class:bin/Nodedef.class bin/Node.class src/Server.java
	javac -cp bin -d bin src/Server.java
bin/Nodedef.class:src/Nodedef.java
	javac -cp bin -d bin src/Nodedef.java
bin/Node.class: src/Node.java
	javac -cp bin -d bin src/Node.java
clean:
	rm bin/*.class
