#!/bin/sh
cd bin/
rmiregistry &
cd ..
java -cp bin/ -Djava.security.policy=policy Server < test
