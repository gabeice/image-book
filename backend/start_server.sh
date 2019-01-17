#!/usr/bin/env bash
$DATOMIC_HOME/bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d image-book,datomic:mem://image-book